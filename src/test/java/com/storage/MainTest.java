package com.storage;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    
    @Test
    @DisplayName("Test 1: Main class exists and is public")
    void testMainClassExists() {
        assertDoesNotThrow(() -> {
            Class<?> mainClass = Class.forName("com.storage.Main");
            assertTrue(java.lang.reflect.Modifier.isPublic(
                mainClass.getModifiers()));
        });
    }
    
    @Test
    @DisplayName("Test 2: Main method exists")
    void testMainMethodExists() {
        assertDoesNotThrow(() -> {
            Class<?> mainClass = Class.forName("com.storage.Main");
            mainClass.getMethod("main", String[].class);
        });
    }
    
    @Test
    @DisplayName("Test 3: FileManager utility is accessible")
    void testFileManagerAccessible() {
        assertNotNull(FileManager.class);
    }
    
    @Test
    @DisplayName("Test 4: LogSystem can be instantiated")
    void testLogSystemInstantiation() {
        assertDoesNotThrow(() -> {
            LogSystem logSystem = new LogSystem();
            assertNotNull(logSystem);
        });
    }
    
    @Test
    @DisplayName("Test 5: Application creates required directories")
    void testDirectoryCreation() {
        new LogSystem();
        
        assertTrue(Files.exists(Paths.get("logs/vehicles")));
        assertTrue(Files.exists(Paths.get("logs/stations")));
        assertTrue(Files.exists(Paths.get("logs/system")));
    }
}