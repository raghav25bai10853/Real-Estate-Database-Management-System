package com.realestate.service.impl;

import com.realestate.dao.InquiryDao;
import com.realestate.dao.PropertyDao;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.Inquiry;
import com.realestate.service.InquiryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryDao inquiryDao;
    private final PropertyDao propertyDao;

    public InquiryServiceImpl(InquiryDao inquiryDao, PropertyDao propertyDao) {
        this.inquiryDao = inquiryDao;
        this.propertyDao = propertyDao;
    }

    @Override
    public Inquiry createInquiry(Inquiry inquiry) {
        // Make sure the property being inquired about actually exists.
        if (!propertyDao.existsById(inquiry.getPropertyId())) {
            throw new ResourceNotFoundException("Property not found with id: " + inquiry.getPropertyId());
        }
        return inquiryDao.save(inquiry);
    }

    @Override
    public List<Inquiry> getAllInquiries() {
        return inquiryDao.findAll();
    }

    @Override
    public Inquiry getInquiryById(Long id) {
        return inquiryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id: " + id));
    }

    @Override
    public List<Inquiry> getInquiriesByProperty(Long propertyId) {
        if (!propertyDao.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found with id: " + propertyId);
        }
        return inquiryDao.findByPropertyId(propertyId);
    }

    @Override
    public void deleteInquiry(Long id) {
        if (inquiryDao.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Inquiry not found with id: " + id);
        }
        inquiryDao.deleteById(id);
    }
}
