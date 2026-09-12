package com.realestate.service;

public interface AdminService {

    /**
     * Validates admin credentials.
     * @return true if username/password match a record in DB
     */
    boolean login(String username, String password);
}
