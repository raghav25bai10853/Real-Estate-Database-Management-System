package com.realestate.dao;

import com.realestate.model.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryDao {

    Inquiry save(Inquiry inquiry);

    List<Inquiry> findAll();

    Optional<Inquiry> findById(Long id);

    List<Inquiry> findByPropertyId(Long propertyId);

    boolean deleteById(Long id);
}
