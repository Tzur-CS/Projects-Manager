package com.example.demo.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

