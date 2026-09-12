package com.realestate.dao;

import com.realestate.model.Property;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository  
public class PropertyDaoImpl implements PropertyDao {

    private final JdbcTemplate jdbcTemplate;

    public PropertyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private Property mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Property p = new Property();
        p.setId(rs.getLong("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setCity(rs.getString("city"));
        p.setAddress(rs.getString("address"));
        p.setType(rs.getString("type"));   
        p.setPrice(rs.getBigDecimal("price"));
        p.setBedrooms((Integer) rs.getObject("bedrooms"));
        p.setBathrooms((Integer) rs.getObject("bathrooms"));
        p.setAreaSqft((Double) rs.getObject("area_sqft"));
        p.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            p.setCreatedAt(ts.toLocalDateTime());
        }
        return p;
    }

    @Override
    public Property save(Property property) {
        String sql = "INSERT INTO properties " +
                "(title, description, city, address, type, price, bedrooms, bathrooms, area_sqft, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";  

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, property.getTitle());
            ps.setString(2, property.getDescription());
            ps.setString(3, property.getCity());
            ps.setString(4, property.getAddress());  
            ps.setString(5, property.getType());
            ps.setBigDecimal(6, property.getPrice());
            ps.setObject(7, property.getBedrooms());
            ps.setObject(8, property.getBathrooms());
            ps.setObject(9, property.getAreaSqft());
            ps.setString(10, property.getStatus());
            ps.setTimestamp(11, now);
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        property.setId(generatedId);
        property.setCreatedAt(now.toLocalDateTime());
        return property;    
    }

    @Override
    public Optional<Property> findById(Long id) {
        String sql = "SELECT * FROM properties WHERE id = ?";
        try {
            Property property = jdbcTemplate.queryForObject(sql, this::mapRow, id);
            return Optional.ofNullable(property);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
  
    @Override
    public List<Property> findAll() {  
        String sql = "SELECT * FROM properties ORDER BY id DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }  

    @Override  
    public boolean update(Property property) {
        String sql = "UPDATE properties SET title = ?, description = ?, city = ?, address = ?, " +
                "type = ?, price = ?, bedrooms = ?, bathrooms = ?, area_sqft = ?, status = ? " +
                "WHERE id = ?";
        int rows = jdbcTemplate.update(sql,
                property.getTitle(),
                property.getDescription(),
                property.getCity(), 
                property.getAddress(),
                property.getType(),  
                property.getPrice(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getAreaSqft(),
                property.getStatus(),
                property.getId());
        return rows > 0;
    }
  
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM properties WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }  

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM properties WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    } 
    @Override  
    public List<Property> search(String city, String type, BigDecimal minPrice, BigDecimal maxPrice,
                                  Integer bedrooms, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM properties WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (city != null && !city.isBlank()) {
            sql.append(" AND LOWER(city) = LOWER(?)");
            params.add(city);
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND LOWER(type) = LOWER(?)");
            params.add(type);
        }  
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");  
            params.add(maxPrice);
        }
        if (bedrooms != null) {
            sql.append(" AND bedrooms = ?");
            params.add(bedrooms);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND LOWER(status) = LOWER(?)");
            params.add(status);
        }

        sql.append(" ORDER BY id DESC");

        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }
}
