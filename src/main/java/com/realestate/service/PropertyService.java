package com.realestate.service;

import com.realestate.model.Property;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {

    Property createProperty(Property property);

    Property getPropertyById(Long id);

    List<Property> getAllProperties();

    Property updateProperty(Long id, Property property);

    void deleteProperty(Long id);

    List<Property> searchProperties(String city, String type, BigDecimal minPrice, BigDecimal maxPrice,
                                     Integer bedrooms, String status);
}
