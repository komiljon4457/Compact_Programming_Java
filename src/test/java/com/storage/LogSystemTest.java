package com.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogSystemTest {
    private LogSystem logSystem;
    private static final String LOG_DIR = "logs/";

    @BeforeEach
    void setup() {
        logSystem = new LogSystem();
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(Paths.get(LOG_DIR))) {
            Files.walk(Paths.get(LOG_DIR))
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        }
    }

    @Test
    @DisplayName("Test 1: Log vehicle activity creates file")
    void testLogVehicle() {
        String vehicleId = "V001";
        logSystem.logVehicle(vehicleId, "Test activity");

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String logFile = LOG_DIR + "vehicles/" + vehicleId + "_" + date + ".log";

        assertTrue(Files.exists(Paths.get(logFile)));
    }

    @Test
    @DisplayName("Test 2: Log station activity creates file")
    void testLogStation() {
        String stationId = "CS001";
        logSystem.logStation(stationId, "Charging started");

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String logFile = LOG_DIR + "stations/" + stationId + "_" + date + ".log";

        assertTrue(Files.exists(Paths.get(logFile)));
    }

    @Test
    @DisplayName("Test 3: Log system event creates file")
    void testLogSystem() {
        logSystem.logSystem("System started");

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String logFile = LOG_DIR + "system/system_" + date + ".log";

        assertTrue(Files.exists(Paths.get(logFile)));
    }

    @Test
    @DisplayName("Test 4: Search by name returns results")
    void testSearchByName() {
        logSystem.logVehicle("V123", "Test search");

        List<String> results = logSystem.searchByName("V123");

        assertFalse(results.isEmpty());
        assertTrue(results.get(0).contains("V123"));
    }

    @Test
    @DisplayName("Test 5: Get logs by type returns correct logs")
    void testGetLogsByType() {
        logSystem.logVehicle("V001", "Activity 1");
        logSystem.logVehicle("V002", "Activity 2");

        List<String> vehicleLogs = logSystem.getLogsByType("vehicles");

        assertTrue(vehicleLogs.size() >= 2);
        assertTrue(vehicleLogs.stream()
                .anyMatch(log -> log.contains("vehicles")));
    }
}