package com.realestate.service.impl;

import com.realestate.dao.AdminDao;
import com.realestate.model.Admin;
import com.realestate.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminDao adminDao;

    public AdminServiceImpl(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @Override
    public boolean login(String username, String password) {
        Optional<Admin> adminOpt = adminDao.findByUsername(username);
        // Simple plain-text comparison for this learning project (no JWT/Spring Security).
        return adminOpt.isPresent() && adminOpt.get().getPassword().equals(password);
    }
}
