package com.storage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogSystem {
    // ... (same code as before, just add package at top)
    // Copy the entire LogSystem class from the previous response

    private static final String LOG_DIR = "logs/";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogSystem() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR + "vehicles/"));
            Files.createDirectories(Paths.get(LOG_DIR + "stations/"));
            Files.createDirectories(Paths.get(LOG_DIR + "system/"));
            System.out.println("✓ Log directories ready\n");
        } catch (IOException e) {
            System.out.println("✗ Error creating directories");
        }
    }

    public void logVehicle(String vehicleId, String activity) {
        String date = LocalDateTime.now().format(DATE_FMT);
        String time = LocalDateTime.now().format(TIME_FMT);
        String file = LOG_DIR + "vehicles/" + vehicleId + "_" + date + ".log";

        String entry = String.format("[%s] Vehicle %s: %s\n",
                time, vehicleId, activity);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, true))) {
            writer.write(entry);
        } catch (IOException e) {
            System.out.println("✗ Error writing vehicle log");
        }
    }

    public void logStation(String stationId, String activity) {
        String date = LocalDateTime.now().format(DATE_FMT);
        String time = LocalDateTime.now().format(TIME_FMT);
        String file = LOG_DIR + "stations/" + stationId + "_" + date + ".log";

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(file, true))) {
            writer.printf("[%s] Station %s: %s%n", time, stationId, activity);
        } catch (IOException e) {
            System.out.println("✗ Error writing station log");
        }
    }

    public void logSystem(String event) {
        String date = LocalDateTime.now().format(DATE_FMT);
        String time = LocalDateTime.now().format(TIME_FMT);
        String file = LOG_DIR + "system/system_" + date + ".log";

        String entry = String.format("[%s] SYSTEM: %s\n", time, event);

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(entry.getBytes());
        } catch (IOException e) {
            System.out.println("✗ Error writing system log");
        }
    }

    public List<String> searchByName(String name) {
        List<String> results = new ArrayList<>();
        try {
            Files.walk(Paths.get(LOG_DIR))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".log"))
                    .filter(p -> p.toString().toLowerCase()
                            .contains(name.toLowerCase()))
                    .forEach(p -> results.add(p.toString()));
        } catch (IOException e) {
            System.out.println("✗ Search error");
        }
        return results;
    }

    public List<String> searchByDate(String date) {
        List<String> results = new ArrayList<>();
        try {
            Files.walk(Paths.get(LOG_DIR))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().contains(date))
                    .forEach(p -> results.add(p.toString()));
        } catch (IOException e) {
            System.out.println("✗ Search error");
        }
        return results;
    }

    public List<String> getLogsByType(String type) {
        List<String> results = new ArrayList<>();
        try {
            Path dir = Paths.get(LOG_DIR + type);
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .filter(Files::isRegularFile)
                        .forEach(p -> results.add(p.toString()));
            }
        } catch (IOException e) {
            System.out.println("✗ Error getting logs");
        }
        return results;
    }

    public void displayLog(String logFile) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📄 " + logFile);
        System.out.println("=".repeat(70));

        try (BufferedReader reader = new BufferedReader(
                new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("✗ Error reading log");
        }
        System.out.println("=".repeat(70) + "\n");
    }

    public void showSummary() {
        int vehicles = getLogsByType("vehicles").size();
        int stations = getLogsByType("stations").size();
        int system = getLogsByType("system").size();

        System.out.println("\n📊 LOG SUMMARY");
        System.out.println("═".repeat(40));
        System.out.println("  🚗 Vehicle logs:  " + vehicles);
        System.out.println("  🔌 Station logs:  " + stations);
        System.out.println("  ⚙️  System logs:   " + system);
        System.out.println("  📁 Total:         " + (vehicles + stations + system));
        System.out.println("═".repeat(40) + "\n");
    }
}
