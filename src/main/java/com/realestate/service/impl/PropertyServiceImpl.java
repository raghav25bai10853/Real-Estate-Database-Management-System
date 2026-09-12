package com.realestate.service.impl;

import com.realestate.dao.PropertyDao;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.Property;
import com.realestate.service.PropertyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class PropertyServiceImpl implements PropertyService {

    private static final Set<String> VALID_STATUSES = Set.of("AVAILABLE", "SOLD", "RENTED");

    private final PropertyDao propertyDao;

    public PropertyServiceImpl(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    private void validateStatus(String status) {
        if (status != null && !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("status must be one of " + VALID_STATUSES);
        }
    }

    @Override
    public Property createProperty(Property property) {
        if (property.getStatus() == null || property.getStatus().isBlank()) {
            property.setStatus("AVAILABLE");
        }
        validateStatus(property.getStatus());
        property.setStatus(property.getStatus().toUpperCase());
        return propertyDao.save(property);
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyDao.findAll();
    }

    @Override
    public Property updateProperty(Long id, Property property) {
        if (!propertyDao.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id: " + id);
        }
        validateStatus(property.getStatus());
        property.setId(id);
        if (property.getStatus() != null) {
            property.setStatus(property.getStatus().toUpperCase());
        }
        propertyDao.update(property);
        return propertyDao.findById(id).orElseThrow();
    }

    @Override
    public void deleteProperty(Long id) {
        if (!propertyDao.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id: " + id);
        }
        propertyDao.deleteById(id);
    }

    @Override
    public List<Property> searchProperties(String city, String type, BigDecimal minPrice, BigDecimal maxPrice,
                                            Integer bedrooms, String status) {
        validateStatus(status);
        return propertyDao.search(city, type, minPrice, maxPrice, bedrooms, status);
    }
}
