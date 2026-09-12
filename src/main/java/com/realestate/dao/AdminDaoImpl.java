package com.realestate.dao;

import com.realestate.model.Admin;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminDaoImpl implements AdminDao {

    private final JdbcTemplate jdbcTemplate;

    public AdminDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Admin mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getLong("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        return admin;
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username = ?";
        try {
            Admin admin = jdbcTemplate.queryForObject(sql, this::mapRow, username);
            return Optional.ofNullable(admin);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
