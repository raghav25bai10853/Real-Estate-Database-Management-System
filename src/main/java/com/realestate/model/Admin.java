package com.realestate.model;

/**
 * Represents an admin user who can log in to manage properties.
 *
 * NOTE (beginner note): For simplicity this project stores/compares the
 * password as plain text in the database (see data.sql). This is fine for
 * learning/demo purposes only. In a real production system you must never
 * store plain text passwords — use a hashing algorithm like BCrypt.
 */
public class Admin {

    private Long id;
    private String username;
    private String password;

    public Admin() {
    }

    public Admin(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
