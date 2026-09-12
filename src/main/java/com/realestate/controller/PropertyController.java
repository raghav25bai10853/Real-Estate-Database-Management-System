package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.model.Property;
import com.realestate.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST endpoints for managing properties.
 *
 * Base path: /api/properties
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // CREATE - POST /api/properties
    @PostMapping
    public ResponseEntity<ApiResponse<Property>> create(@Valid @RequestBody Property property) {
        Property created = propertyService.createProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property created successfully", created));
    }

    // READ ALL - GET /api/properties
    @GetMapping
    public ApiResponse<List<Property>> getAll() {
        return ApiResponse.success("Properties fetched successfully", propertyService.getAllProperties());
    }

    // READ ONE - GET /api/properties/{id}
    @GetMapping("/{id}")
    public ApiResponse<Property> getById(@PathVariable Long id) {
        return ApiResponse.success("Property fetched successfully", propertyService.getPropertyById(id));
    }

    // UPDATE - PUT /api/properties/{id}
    @PutMapping("/{id}")
    public ApiResponse<Property> update(@PathVariable Long id, @Valid @RequestBody Property property) {
        return ApiResponse.success("Property updated successfully", propertyService.updateProperty(id, property));
    }

    // DELETE - DELETE /api/properties/{id}
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ApiResponse.success("Property deleted successfully", null);
    }

    // SEARCH - GET /api/properties/search?city=&type=&minPrice=&maxPrice=&bedrooms=&status=
    @GetMapping("/search")
    public ApiResponse<List<Property>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) String status) {

        List<Property> results = propertyService.searchProperties(city, type, minPrice, maxPrice, bedrooms, status);
        return ApiResponse.success("Search completed", results);
    }
}
