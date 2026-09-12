package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.model.Inquiry;
import com.realestate.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;  
@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
 
    public InquiryController(InquiryService inquiryService) { 
        this.inquiryService = inquiryService;
    }  
    @PostMapping
    public ResponseEntity<ApiResponse<Inquiry>> create(@Valid @RequestBody Inquiry inquiry) {
        Inquiry created = inquiryService.createInquiry(inquiry);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inquiry submitted successfully", created));
    }  
    @GetMapping
    public ApiResponse<List<Inquiry>> getAll() { 
        return ApiResponse.success("Inquiries fetched successfully", inquiryService.getAllInquiries());
    }  
    @GetMapping("/{id}")
    public ApiResponse<Inquiry> getById(@PathVariable Long id) {
        return ApiResponse.success("Inquiry fetched successfully", inquiryService.getInquiryById(id));
    } 
    @GetMapping("/property/{propertyId}")
    public ApiResponse<List<Inquiry>> getByProperty(@PathVariable Long propertyId) { 
        return ApiResponse.success("Inquiries fetched successfully", inquiryService.getInquiriesByProperty(propertyId));
    }  
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return ApiResponse.success("Inquiry deleted successfully", null);
    }
}
