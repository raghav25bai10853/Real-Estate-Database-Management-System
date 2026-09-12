package com.realestate.dao;

import com.realestate.model.Property;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) contract for Property.
 * The DAO layer is the ONLY place that talks to the database.
 */
public interface PropertyDao {

    Property save(Property property);

    Optional<Property> findById(Long id);

    List<Property> findAll();

    boolean update(Property property);

    boolean deleteById(Long id);

    boolean existsById(Long id);

    /**
     * Dynamic search. Any parameter can be null, meaning "don't filter by this field".
     */
    List<Property> search(String city, String type, BigDecimal minPrice, BigDecimal maxPrice,
                           Integer bedrooms, String status);
}
