package com.storage.exception;

// Base custom exception for the application
public class StorageException extends Exception {
    private String errorCode;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public StorageException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public StorageException() {

    }

    public String getErrorCode() {
        return errorCode;
    }
}