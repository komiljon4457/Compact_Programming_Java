package com.storage;

import java.util.*;

public class Main {
    // ... (same code as before, just add package at top)
    // Copy the entire Main class from the previous response

    private static LogSystem logSystem;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  STORAGE & TASK MANAGEMENT SYSTEM          ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        logSystem = new LogSystem();
        scanner = new Scanner(System.in);

        demoFileOperations();
        demoStreamOperations();
        createSampleLogs();
        interactiveMenu();

        scanner.close();
    }

    private static void demoFileOperations() {
        System.out.println("📂 PART 1: FILE OPERATIONS");
        System.out.println("═".repeat(50));

        FileManager.createFile("temp/sample.txt", "Hello from file system!");
        FileManager.createBinaryFile("temp/data.bin", "Binary data".getBytes());
        FileManager.moveFile("temp/sample.txt", "temp/archive/sample.txt");

        String content = FileManager.readFile("temp/archive/sample.txt");
        System.out.println("📄 Content: " + content.trim());

        FileManager.archiveFile("temp/archive/sample.txt",
                "temp/archive/sample.zip");
        FileManager.deleteFile("temp/data.bin");

        System.out.println("═".repeat(50) + "\n");
    }

    private static void demoStreamOperations() {
        System.out.println("🔄 PART 2: STREAM OPERATIONS");
        System.out.println("═".repeat(50));

        try {
            java.io.PipedOutputStream pos = new java.io.PipedOutputStream();
            java.io.PipedInputStream pis = new java.io.PipedInputStream(pos);

            Thread producer = new Thread(() -> {
                try (java.io.DataOutputStream dos =
                             new java.io.DataOutputStream(pos)) {
                    System.out.println("📤 Sending: Vehicle status data");
                    dos.writeUTF("Vehicle-001");
                    dos.writeInt(85);
                    dos.writeDouble(120.5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread consumer = new Thread(() -> {
                try (java.io.DataInputStream dis =
                             new java.io.DataInputStream(pis)) {
                    String vehicle = dis.readUTF();
                    int battery = dis.readInt();
                    double speed = dis.readDouble();

                    System.out.println("📥 Received: " + vehicle);
                    System.out.println("   Battery: " + battery + "%");
                    System.out.println("   Speed: " + speed + " km/h");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            producer.start();
            consumer.start();
            producer.join();
            consumer.join();

            System.out.println("✓ Data exchange completed\n");
        } catch (Exception e) {
            System.out.println("✗ Stream error");
        }

        System.out.println("═".repeat(50) + "\n");
    }

    private static void createSampleLogs() {
        System.out.println("📝 PART 3: CREATING SAMPLE LOGS");
        System.out.println("═".repeat(50));

        for (int i = 1; i <= 3; i++) {
            String vehicleId = "V" + String.format("%03d", i);
            logSystem.logVehicle(vehicleId, "Started operation");
            logSystem.logVehicle(vehicleId, "Loaded cargo: 250kg");
            logSystem.logVehicle(vehicleId, "Battery level: 85%");
            System.out.println("✓ Created logs for " + vehicleId);
        }

        for (int i = 1; i <= 2; i++) {
            String stationId = "CS" + String.format("%03d", i);
            logSystem.logStation(stationId, "Station operational");
            logSystem.logStation(stationId, "Charging vehicle V001");
            logSystem.logStation(stationId, "Power output: 150kW");
            System.out.println("✓ Created logs for " + stationId);
        }

        logSystem.logSystem("System initialized");
        logSystem.logSystem("All equipment online");
        logSystem.logSystem("Daily operations started");
        System.out.println("✓ Created system logs");

        System.out.println("═".repeat(50) + "\n");
    }

    private static void interactiveMenu() {
        System.out.println("🔍 PART 4: LOG SEARCH & RETRIEVAL");
        System.out.println("═".repeat(50));

        logSystem.showSummary();

        boolean running = true;
        while (running) {
            System.out.println("\n📋 MENU:");
            System.out.println("  1. Search by Equipment Name/ID");
            System.out.println("  2. Search by Date");
            System.out.println("  3. View All Vehicle Logs");
            System.out.println("  4. View All Station Logs");
            System.out.println("  5. View All System Logs");
            System.out.println("  6. Show Summary");
            System.out.println("  0. Exit");
            System.out.print("\nChoice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: searchByName(); break;
                case 2: searchByDate(); break;
                case 3: viewLogs("vehicles"); break;
                case 4: viewLogs("stations"); break;
                case 5: viewLogs("system"); break;
                case 6: logSystem.showSummary(); break;
                case 0:
                    running = false;
                    System.out.println("\n✓ Goodbye!");
                    break;
                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }

    private static void searchByName() {
        System.out.print("Enter name/ID (e.g., V001, CS001): ");
        String name = scanner.nextLine();
        List<String> results = logSystem.searchByName(name);
        showResults(results);
    }

    private static void searchByDate() {
        System.out.print("Enter date (yyyy-MM-dd): ");
        String date = scanner.nextLine();
        List<String> results = logSystem.searchByDate(date);
        showResults(results);
    }

    private static void viewLogs(String type) {
        List<String> results = logSystem.getLogsByType(type);
        showResults(results);
    }

    private static void showResults(List<String> results) {
        if (results.isEmpty()) {
            System.out.println("❌3 No logs found!");
            return;
        }

        System.out.println("\n📁 Found " + results.size() + " log(s):");
        for (int i = 0; i < results.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + results.get(i));
        }

        System.out.print("\nOpen log # (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= results.size()) {
            logSystem.displayLog(results.get(choice - 1));
        }
    }
}