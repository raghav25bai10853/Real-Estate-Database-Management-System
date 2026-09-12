package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.LoginRequest;
import com.realestate.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/admin")
public class AdminController {  

    private final AdminService adminService;  

    public AdminController(AdminService adminService) {
        this.adminService = adminService;  
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        boolean valid = adminService.login(request.getUsername(), request.getPassword());  
        if (valid) {
            return ResponseEntity.ok(ApiResponse.success("Login successful", "Welcome, " + request.getUsername()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) 
                .body(ApiResponse.failure("Invalid username or password"));  
    }
}
