package com.realestate.dao;

import com.realestate.model.Inquiry;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class InquiryDaoImpl implements InquiryDao {

    private final JdbcTemplate jdbcTemplate;

    public InquiryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Inquiry mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Inquiry inquiry = new Inquiry();
        inquiry.setId(rs.getLong("id"));
        inquiry.setPropertyId(rs.getLong("property_id"));
        inquiry.setName(rs.getString("name"));
        inquiry.setEmail(rs.getString("email"));
        inquiry.setPhone(rs.getString("phone"));
        inquiry.setMessage(rs.getString("message"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            inquiry.setCreatedAt(ts.toLocalDateTime());
        }
        return inquiry;
    }

    @Override
    public Inquiry save(Inquiry inquiry) {
        String sql = "INSERT INTO inquiries (property_id, name, email, phone, message, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, inquiry.getPropertyId());
            ps.setString(2, inquiry.getName());
            ps.setString(3, inquiry.getEmail());
            ps.setString(4, inquiry.getPhone());
            ps.setString(5, inquiry.getMessage());
            ps.setTimestamp(6, now);
            return ps;
        }, keyHolder);

        inquiry.setId(keyHolder.getKey().longValue());
        inquiry.setCreatedAt(now.toLocalDateTime());
        return inquiry;
    }

    @Override
    public List<Inquiry> findAll() {
        String sql = "SELECT * FROM inquiries ORDER BY id DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<Inquiry> findById(Long id) {
        String sql = "SELECT * FROM inquiries WHERE id = ?";
        try {
            Inquiry inquiry = jdbcTemplate.queryForObject(sql, this::mapRow, id);
            return Optional.ofNullable(inquiry);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Inquiry> findByPropertyId(Long propertyId) {
        String sql = "SELECT * FROM inquiries WHERE property_id = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, this::mapRow, propertyId);
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM inquiries WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
}
