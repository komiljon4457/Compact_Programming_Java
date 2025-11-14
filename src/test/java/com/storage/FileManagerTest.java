package com.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {
    private static final String TEST_DIR = "test-files/";

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
    @DisplayName("Test 1: Create file successfully")
    void testCreateFile() {
        String filePath = TEST_DIR + "test1.txt";
        String content = "Test content";

        FileManager.createFile(filePath, content);

        assertTrue(Files.exists(Paths.get(filePath)));
        assertEquals(content, FileManager.readFile(filePath).trim());
    }

    @Test
    @DisplayName("Test 2: Create binary file successfully")
    void testCreateBinaryFile() {
        String filePath = TEST_DIR + "test2.bin";
        byte[] data = "Binary test".getBytes();

        FileManager.createBinaryFile(filePath, data);

        assertTrue(Files.exists(Paths.get(filePath)));
        assertArrayEquals(data, readBinaryFile(filePath));
    }

    @Test
    @DisplayName("Test 3: Move file successfully")
    void testMoveFile() {
        String source = TEST_DIR + "source.txt";
        String dest = TEST_DIR + "moved/dest.txt";

        FileManager.createFile(source, "Move me");
        FileManager.moveFile(source, dest);

        assertFalse(Files.exists(Paths.get(source)));
        assertTrue(Files.exists(Paths.get(dest)));
    }

    @Test
    @DisplayName("Test 4: Delete file successfully")
    void testDeleteFile() {
        String filePath = TEST_DIR + "delete-me.txt";

        FileManager.createFile(filePath, "Delete this");
        assertTrue(Files.exists(Paths.get(filePath)));

        FileManager.deleteFile(filePath);
        assertFalse(Files.exists(Paths.get(filePath)));
    }

    @Test
    @DisplayName("Test 5: Archive file successfully")
    void testArchiveFile() throws IOException {
        String source = TEST_DIR + "archive-me.txt";
        String zipFile = TEST_DIR + "archived.zip";

        FileManager.createFile(source, "Archive this");
        FileManager.archiveFile(source, zipFile);

        assertTrue(Files.exists(Paths.get(zipFile)));
        assertTrue(Files.size(Paths.get(zipFile)) > 0);
    }

    private byte[] readBinaryFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}