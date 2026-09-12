package com.realestate.service;

import com.realestate.model.Inquiry;

import java.util.List;

public interface InquiryService {

    Inquiry createInquiry(Inquiry inquiry);

    List<Inquiry> getAllInquiries();

    Inquiry getInquiryById(Long id);

    List<Inquiry> getInquiriesByProperty(Long propertyId);

    void deleteInquiry(Long id);
}
