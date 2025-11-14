package com.storage.exception;

public class LogException extends StorageException {
    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(String message, String errorCode) {
        super(message, errorCode);
    }

    // ADD THIS:
    public LogException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }
}