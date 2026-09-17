// mycmds.c - 10 Unix commands in one program (process, memory, file mgmt)
// Usage: ./mycmds <command> [args...]

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <signal.h>
#include <ctype.h>

// ---------- File management ----------

int cmd_ls(int argc, char *argv[]) {
    const char *path = (argc > 2) ? argv[2] : ".";
    DIR *d = opendir(path);
    if (!d) { perror("opendir"); return 1; }
    struct dirent *e;
    while ((e = readdir(d)) != NULL) printf("%s\n", e->d_name);
    closedir(d);
    return 0;
}

int cmd_pwd(int argc, char *argv[]) {
    char buf[1024];
    if (!getcwd(buf, sizeof(buf))) { perror("getcwd"); return 1; }
    printf("%s\n", buf);
    return 0;
}

int cmd_mkdir(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: mkdir <dirname>\n"); return 1; }
    if (mkdir(argv[2], 0755) == -1) { perror("mkdir"); return 1; }
    return 0;
}

int cmd_rm(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: rm <file>\n"); return 1; }
    if (unlink(argv[2]) == -1) { perror("unlink"); return 1; }
    return 0;
}

int cmd_mv(int argc, char *argv[]) {
    if (argc < 4) { fprintf(stderr, "Usage: mv <src> <dst>\n"); return 1; }
    if (rename(argv[2], argv[3]) == -1) { perror("rename"); return 1; }
    return 0;
}

int cmd_cp(int argc, char *argv[]) {
    if (argc < 4) { fprintf(stderr, "Usage: cp <src> <dst>\n"); return 1; }
    int src = open(argv[2], O_RDONLY);
    if (src == -1) { perror("open src"); return 1; }
    int dst = open(argv[3], O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (dst == -1) { perror("open dst"); close(src); return 1; }
    char buf[4096]; ssize_t n;
    while ((n = read(src, buf, sizeof(buf))) > 0) write(dst, buf, n);
    close(src); close(dst);
    return 0;
}

int cmd_cat(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: cat <file>\n"); return 1; }
    int fd = open(argv[2], O_RDONLY);
    if (fd == -1) { perror("open"); return 1; }
    char buf[4096]; ssize_t n;
    while ((n = read(fd, buf, sizeof(buf))) > 0) write(STDOUT_FILENO, buf, n);
    close(fd);
    return 0;
}

// ---------- Process management ----------

int cmd_ps(int argc, char *argv[]) {
    DIR *proc = opendir("/proc");
    if (!proc) { perror("opendir"); return 1; }
    printf("%-8s %-20s\n", "PID", "NAME");
    struct dirent *e;
    while ((e = readdir(proc)) != NULL) {
        if (!isdigit(e->d_name[0])) continue;
        char path[300], name[256] = "?";
        snprintf(path, sizeof(path), "/proc/%s/comm", e->d_name);
        FILE *f = fopen(path, "r");
        if (f) {
            if (fgets(name, sizeof(name), f)) {
                size_t len = strlen(name);
                if (len && name[len-1] == '\n') name[len-1] = '\0';
            }
            fclose(f);
        }
        printf("%-8s %-20s\n", e->d_name, name);
    }
    closedir(proc);
    return 0;
}

int cmd_kill(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: kill <pid> [signal]\n"); return 1; }
    pid_t pid = atoi(argv[2]);
    int sig = (argc > 3) ? atoi(argv[3]) : SIGTERM;
    if (kill(pid, sig) == -1) { perror("kill"); return 1; }
    printf("Sent signal %d to PID %d\n", sig, pid);
    return 0;
}

// ---------- Memory management ----------

int cmd_free(int argc, char *argv[]) {
    FILE *f = fopen("/proc/meminfo", "r");
    if (!f) { perror("fopen"); return 1; }
    char key[64]; long val, total = 0, free_ = 0, avail = 0;
    while (fscanf(f, "%63s %ld kB\n", key, &val) == 2) {
        if (!total && !strcmp(key, "MemTotal:"))     total = val;
        if (!free_ && !strcmp(key, "MemFree:"))      free_ = val;
        if (!avail && !strcmp(key, "MemAvailable:")) avail = val;
    }
    fclose(f);
    printf("%-10s %10s %10s %10s\n", "", "total", "free", "available");
    printf("%-10s %10ld %10ld %10ld\n", "Mem:", total, free_, avail);
    return 0;
}

// ---------- Dispatcher ----------

void print_usage(const char *prog) {
    fprintf(stderr,
        "Usage: %s <command> [args...]\n\n"
        "Commands:\n"
        "  ls [dir]              mv <src> <dst>\n"
        "  pwd                   cp <src> <dst>\n"
        "  mkdir <dir>           cat <file>\n"
        "  rm <file>             ps\n"
        "  kill <pid> [sig]      free\n",
        prog);
}

int main(int argc, char *argv[]) {
    if (argc < 2) { print_usage(argv[0]); return 1; }

    const char *cmd = argv[1];

    if      (!strcmp(cmd, "ls"))    return cmd_ls(argc, argv);
    else if (!strcmp(cmd, "pwd"))   return cmd_pwd(argc, argv);
    else if (!strcmp(cmd, "mkdir")) return cmd_mkdir(argc, argv);
    else if (!strcmp(cmd, "rm"))    return cmd_rm(argc, argv);
    else if (!strcmp(cmd, "mv"))    return cmd_mv(argc, argv);
    else if (!strcmp(cmd, "cp"))    return cmd_cp(argc, argv);
    else if (!strcmp(cmd, "cat"))   return cmd_cat(argc, argv);
    else if (!strcmp(cmd, "ps"))    return cmd_ps(argc, argv);
    else if (!strcmp(cmd, "kill"))  return cmd_kill(argc, argv);
    else if (!strcmp(cmd, "free"))  return cmd_free(argc, argv);
    else { fprintf(stderr, "Unknown command: %s\n", cmd); print_usage(argv[0]); return 1; }
}// mycmds.c - 10 Unix commands in one program (process, memory, file mgmt)
// Usage: ./mycmds <command> [args...]

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <signal.h>
#include <ctype.h>

// ---------- File management ----------

int cmd_ls(int argc, char *argv[]) {
    const char *path = (argc > 2) ? argv[2] : ".";
    DIR *d = opendir(path);
    if (!d) { perror("opendir"); return 1; }
    struct dirent *e;
    while ((e = readdir(d)) != NULL) printf("%s\n", e->d_name);
    closedir(d);
    return 0;
}

int cmd_pwd(int argc, char *argv[]) {
    char buf[1024];
    if (!getcwd(buf, sizeof(buf))) { perror("getcwd"); return 1; }
    printf("%s\n", buf);
    return 0;
}

int cmd_mkdir(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: mkdir <dirname>\n"); return 1; }
    if (mkdir(argv[2], 0755) == -1) { perror("mkdir"); return 1; }
    return 0;
}

int cmd_rm(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: rm <file>\n"); return 1; }
    if (unlink(argv[2]) == -1) { perror("unlink"); return 1; }
    return 0;
}

int cmd_mv(int argc, char *argv[]) {
    if (argc < 4) { fprintf(stderr, "Usage: mv <src> <dst>\n"); return 1; }
    if (rename(argv[2], argv[3]) == -1) { perror("rename"); return 1; }
    return 0;
}

int cmd_cp(int argc, char *argv[]) {
    if (argc < 4) { fprintf(stderr, "Usage: cp <src> <dst>\n"); return 1; }
    int src = open(argv[2], O_RDONLY);
    if (src == -1) { perror("open src"); return 1; }
    int dst = open(argv[3], O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (dst == -1) { perror("open dst"); close(src); return 1; }
    char buf[4096]; ssize_t n;
    while ((n = read(src, buf, sizeof(buf))) > 0) write(dst, buf, n);
    close(src); close(dst);
    return 0;
}

int cmd_cat(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: cat <file>\n"); return 1; }
    int fd = open(argv[2], O_RDONLY);
    if (fd == -1) { perror("open"); return 1; }
    char buf[4096]; ssize_t n;
    while ((n = read(fd, buf, sizeof(buf))) > 0) write(STDOUT_FILENO, buf, n);
    close(fd);
    return 0;
}

// ---------- Process management ----------

int cmd_ps(int argc, char *argv[]) {
    DIR *proc = opendir("/proc");
    if (!proc) { perror("opendir"); return 1; }
    printf("%-8s %-20s\n", "PID", "NAME");
    struct dirent *e;
    while ((e = readdir(proc)) != NULL) {
        if (!isdigit(e->d_name[0])) continue;
        char path[300], name[256] = "?";
        snprintf(path, sizeof(path), "/proc/%s/comm", e->d_name);
        FILE *f = fopen(path, "r");
        if (f) {
            if (fgets(name, sizeof(name), f)) {
                size_t len = strlen(name);
                if (len && name[len-1] == '\n') name[len-1] = '\0';
            }
            fclose(f);
        }
        printf("%-8s %-20s\n", e->d_name, name);
    }
    closedir(proc);
    return 0;
}

int cmd_kill(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr, "Usage: kill <pid> [signal]\n"); return 1; }
    pid_t pid = atoi(argv[2]);
    int sig = (argc > 3) ? atoi(argv[3]) : SIGTERM;
    if (kill(pid, sig) == -1) { perror("kill"); return 1; }
    printf("Sent signal %d to PID %d\n", sig, pid);
    return 0;
}

// ---------- Memory management ----------

int cmd_free(int argc, char *argv[]) {
    FILE *f = fopen("/proc/meminfo", "r");
    if (!f) { perror("fopen"); return 1; }
    char key[64]; long val, total = 0, free_ = 0, avail = 0;
    while (fscanf(f, "%63s %ld kB\n", key, &val) == 2) {
        if (!total && !strcmp(key, "MemTotal:"))     total = val;
        if (!free_ && !strcmp(key, "MemFree:"))      free_ = val;
        if (!avail && !strcmp(key, "MemAvailable:")) avail = val;
    }
    fclose(f);
    printf("%-10s %10s %10s %10s\n", "", "total", "free", "available");
    printf("%-10s %10ld %10ld %10ld\n", "Mem:", total, free_, avail);
    return 0;
}

// ---------- Dispatcher ----------

void print_usage(const char *prog) {
    fprintf(stderr,
        "Usage: %s <command> [args...]\n\n"
        "Commands:\n"
        "  ls [dir]              mv <src> <dst>\n"
        "  pwd                   cp <src> <dst>\n"
        "  mkdir <dir>           cat <file>\n"
        "  rm <file>             ps\n"
        "  kill <pid> [sig]      free\n",
        prog);
}

int main(int argc, char *argv[]) {
    if (argc < 2) { print_usage(argv[0]); return 1; }

    const char *cmd = argv[1];

    if      (!strcmp(cmd, "ls"))    return cmd_ls(argc, argv);
    else if (!strcmp(cmd, "pwd"))   return cmd_pwd(argc, argv);
    else if (!strcmp(cmd, "mkdir")) return cmd_mkdir(argc, argv);
    else if (!strcmp(cmd, "rm"))    return cmd_rm(argc, argv);
    else if (!strcmp(cmd, "mv"))    return cmd_mv(argc, argv);
    else if (!strcmp(cmd, "cp"))    return cmd_cp(argc, argv);
    else if (!strcmp(cmd, "cat"))   return cmd_cat(argc, argv);
    else if (!strcmp(cmd, "ps"))    return cmd_ps(argc, argv);
    else if (!strcmp(cmd, "kill"))  return cmd_kill(argc, argv);
    else if (!strcmp(cmd, "free"))  return cmd_free(argc, argv);
    else { fprintf(stderr, "Unknown command: %s\n", cmd); print_usage(argv[0]); return 1; }
}