package com.storage.exception;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExceptionHandler {
    private static final String ERROR_LOG = "logs/errors.log";

    // a. Handling Multiple Exceptions
    public static void handleMultipleExceptions(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new NoSuchFileException(filePath);
            }
            Files.readString(path);
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (AccessDeniedException e) {
            System.err.println("Access denied: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    // b. Re-throwing Exceptions
    public static void validateAndRethrow(String filePath, String operation)
            throws FileOperationException {
        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path) && operation.equals("read")) {
                throw new FileOperationException(
                        "File does not exist: " + filePath, "ERR_FILE_NOT_FOUND");
            }

            if (!Files.isReadable(path) && operation.equals("read")) {
                throw new FileOperationException(
                        "File is not readable: " + filePath, "ERR_NO_READ_ACCESS");
            }

            if (!Files.isWritable(path.getParent()) && operation.equals("write")) {
                throw new FileOperationException(
                        "Directory is not writable: " + path.getParent(),
                        "ERR_NO_WRITE_ACCESS");
            }

        } catch (InvalidPathException e) {
            // Re-throw with more context
            throw new FileOperationException(
                    "Invalid file path: " + filePath, e, "ERR_INVALID_PATH");
        } catch (SecurityException e) {
            // Re-throw as custom exception
            throw new FileOperationException(
                    "Security check failed for: " + filePath, e, "ERR_SECURITY");
        }
    }

    // c. Resource Management with try-with-resources
    public static String safeReadFile(String filePath) throws FileOperationException {
        // Try-with-resources ensures automatic closure
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();

        } catch (FileNotFoundException e) {
            throw new FileOperationException(
                    "File not found: " + filePath, e, "ERR_FILE_NOT_FOUND");
        } catch (IOException e) {
            throw new FileOperationException(
                    "Error reading file: " + filePath, e, "ERR_READ_FAILED");
        }
    }

    // Resource management for multiple resources
    public static void safeCopyFile(String source, String dest)
            throws FileOperationException {
        // Multiple resources managed automatically
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(dest)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

        } catch (FileNotFoundException e) {
            throw new FileOperationException(
                    "File not found during copy", e, "ERR_COPY_FILE_NOT_FOUND");
        } catch (IOException e) {
            throw new FileOperationException(
                    "Error during file copy", e, "ERR_COPY_FAILED");
        }
    }

    // d. Chaining Exceptions
    public static void processLogWithChaining(String logFile, String data)
            throws LogException {
        try {
            validateLogFile(logFile);
            writeLogData(logFile, data);
        } catch (FileOperationException e) {
            // Chain the exception with additional context
            throw new LogException(
                    "Failed to process log file: " + logFile, e, "ERR_LOG_PROCESS");
        }
    }

    private static void validateLogFile(String logFile)
            throws FileOperationException {
        try {
            Path path = Paths.get(logFile);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            throw new FileOperationException(
                    "Cannot validate log file structure", e, "ERR_LOG_VALIDATE");
        }
    }

    private static void writeLogData(String logFile, String data)
            throws FileOperationException {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(data);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Cannot write to log file", e, "ERR_LOG_WRITE");
        }
    }

    // Exception chaining with multiple levels
    public static void performComplexOperation(String file) throws StorageException {
        try {
            attemptFileOperation(file);
        } catch (FileOperationException e) {
            // Add another layer to exception chain
            throw new StorageException(
                    "Complex operation failed for: " + file, e, "ERR_COMPLEX_OP");
        }
    }

    private static void attemptFileOperation(String file)
            throws FileOperationException {
        try {
            Files.readString(Paths.get(file));
        } catch (IOException e) {
            throw new FileOperationException(
                    "File operation failed", e, "ERR_FILE_OP");
        }
    }

    // Utility: Log errors to file
    private static void logError(String context, String message, Throwable e) {
        try {
            Files.createDirectories(Paths.get(ERROR_LOG).getParent());
            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(ERROR_LOG, true))) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.printf("[%s] %s - %s: %s%n",
                        timestamp, context, message, e.getMessage());
                e.printStackTrace(writer);
                writer.println("---");
            }
        } catch (IOException ex) {
            System.err.println("Failed to log error: " + ex.getMessage());
        }
    }
}