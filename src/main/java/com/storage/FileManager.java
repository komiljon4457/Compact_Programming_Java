package com.storage;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class FileManager {

    public static void createFile(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            System.out.println("✓ Created: " + path);
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    public static void createBinaryFile(String path, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
            System.out.println("✓ Binary file created: " + path);
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    public static void moveFile(String from, String to) {
        try {
            Files.createDirectories(Paths.get(to).getParent());
            Files.move(Paths.get(from), Paths.get(to),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✓ Moved: " + from + " → " + to);
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    public static void deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
            System.out.println("✓ Deleted: " + path);
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    public static void archiveFile(String sourceFile, String zipFile) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             ZipOutputStream zos = new ZipOutputStream(
                     new FileOutputStream(zipFile))) {

            ZipEntry entry = new ZipEntry(new File(sourceFile).getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            System.out.println("✓ Archived: " + zipFile);
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    public static String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("✗ Error reading: " + e.getMessage());
        }
        return content.toString();
    }
}