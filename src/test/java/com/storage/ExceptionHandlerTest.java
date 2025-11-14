package com.storage;

import com.storage.exception.ExceptionHandler;
import com.storage.exception.FileOperationException;
import com.storage.exception.LogException;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest {
    private static final String TEST_DIR = "test-exception/";

    @BeforeEach
    void setup() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.walk(Paths.get(TEST_DIR))
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
    }

    @Test
    @DisplayName("Test 1: Handle multiple exceptions")
    void testHandleMultipleExceptions() {
        assertDoesNotThrow(() -> {
            ExceptionHandler.handleMultipleExceptions("nonexistent.txt");
        });
    }

    @Test
    @DisplayName("Test 2: Validate and rethrow for invalid path")
    void testValidateAndRethrowInvalidPath() {
        assertThrows(FileOperationException.class, () -> {
            ExceptionHandler.validateAndRethrow(
                    "nonexistent.txt", "read");
        });
    }

    @Test
    @DisplayName("Test 3: Safe read file with resource management")
    void testSafeReadFile() throws IOException, FileOperationException {
        String testFile = TEST_DIR + "read-test.txt";
        Files.writeString(Paths.get(testFile), "Test content");

        String content = ExceptionHandler.safeReadFile(testFile);

        assertNotNull(content);
        assertTrue(content.contains("Test content"));
    }

    @Test
    @DisplayName("Test 4: Safe copy file with resource management")
    void testSafeCopyFile() throws IOException, FileOperationException {
        String source = TEST_DIR + "source.txt";
        String dest = TEST_DIR + "dest.txt";

        Files.writeString(Paths.get(source), "Copy me");
        ExceptionHandler.safeCopyFile(source, dest);

        assertTrue(Files.exists(Paths.get(dest)));
        assertEquals(
                Files.readString(Paths.get(source)),
                Files.readString(Paths.get(dest))
        );
    }

    @Test
    @DisplayName("Test 5: Process log with exception chaining")
    void testProcessLogWithChaining() throws LogException {
        String logFile = TEST_DIR + "chain-test.log";

        assertDoesNotThrow(() -> {
            ExceptionHandler.processLogWithChaining(logFile, "Test data\n");
        });

        assertTrue(Files.exists(Paths.get(logFile)));
    }
}
