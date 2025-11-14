package com.storage.exception;

public class FileOperationException extends StorageException {
    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOperationException(String message, String errorCode) {
        super(message, errorCode);
    }

    // ADD THIS:
    public FileOperationException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }
}
