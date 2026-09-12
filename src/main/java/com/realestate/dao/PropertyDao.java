package com.realestate.dao;

import com.realestate.model.Property;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
public interface PropertyDao {

    Property save(Property property);

    Optional<Property> findById(Long id);

    List<Property> findAll();

    boolean update(Property property);

    boolean deleteById(Long id);

    boolean existsById(Long id);
    List<Property> search(String city, String type, BigDecimal minPrice, BigDecimal maxPrice,
                           Integer bedrooms, String status);
}
