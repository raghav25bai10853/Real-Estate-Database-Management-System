package com.realestate.dao;

import com.realestate.model.Admin;

import java.util.Optional;

public interface AdminDao {

    Optional<Admin> findByUsername(String username);
}
