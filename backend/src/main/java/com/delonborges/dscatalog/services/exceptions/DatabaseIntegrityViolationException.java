package com.delonborges.dscatalog.services.exceptions;

public class DatabaseIntegrityViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatabaseIntegrityViolationException(String message) {
        super(message);
    }
}
