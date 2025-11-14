package com.storage;

import com.storage.exception.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    private LogSystem logSystem;
    private static final String TEST_DIR = "integration-test/";
    
    @BeforeEach
    void setup() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
        logSystem = new LogSystem();
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
    @DisplayName("Test 1: Complete file operation workflow")
    void testCompleteFileWorkflow() {
        String file = TEST_DIR + "workflow.txt";
        String moved = TEST_DIR + "moved.txt";
        
        FileManager.createFile(file, "Workflow test");
        assertTrue(Files.exists(Paths.get(file)));
        
        FileManager.moveFile(file, moved);
        assertTrue(Files.exists(Paths.get(moved)));
        
        FileManager.deleteFile(moved);
        assertFalse(Files.exists(Paths.get(moved)));
    }
    
    @Test
    @DisplayName("Test 2: Complete logging workflow")
    void testCompleteLoggingWorkflow() {
        logSystem.logVehicle("V999", "Integration test");
        logSystem.logStation("CS999", "Integration test");
        logSystem.logSystem("Integration test");
        
        var vehicleLogs = logSystem.getLogsByType("vehicles");
        var stationLogs = logSystem.getLogsByType("stations");
        var systemLogs = logSystem.getLogsByType("system");
        
        assertFalse(vehicleLogs.isEmpty());
        assertFalse(stationLogs.isEmpty());
        assertFalse(systemLogs.isEmpty());
    }

    @Test
    @DisplayName("Test 3: Exception handling integration")
    void testExceptionHandlingIntegration() throws IOException, FileOperationException {
        String testFile = TEST_DIR + "exception-test.txt";
        Files.writeString(Paths.get(testFile), "Exception test");

        String content = ExceptionHandler.safeReadFile(testFile);
        assertNotNull(content);
        assertTrue(content.contains("Exception test"));
    }
    
    @Test
    @DisplayName("Test 4: Search functionality integration")
    void testSearchIntegration() {
        logSystem.logVehicle("SEARCH001", "Searchable content");
        
        var results = logSystem.searchByName("SEARCH001");
        assertFalse(results.isEmpty());
        
        var dateResults = logSystem.searchByDate(
            java.time.LocalDate.now().toString());
        assertFalse(dateResults.isEmpty());
    }
    
    @Test
    @DisplayName("Test 5: Resource management integration")
    void testResourceManagementIntegration() 
            throws IOException, FileOperationException {
        String source = TEST_DIR + "resource-source.txt";
        String dest = TEST_DIR + "resource-dest.txt";
        
        Files.writeString(Paths.get(source), "Resource test");
        
        ExceptionHandler.safeCopyFile(source, dest);
        
        assertTrue(Files.exists(Paths.get(source)));
        assertTrue(Files.exists(Paths.get(dest)));
        assertEquals(
            Files.readString(Paths.get(source)),
            Files.readString(Paths.get(dest))
        );
    }
}