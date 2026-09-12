package com.realestate.exception;

/**
 * Thrown when a requested Property/Inquiry/Admin id does not exist in the DB.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
