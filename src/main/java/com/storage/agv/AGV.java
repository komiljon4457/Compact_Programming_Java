package com.storage.agv;

import java.time.LocalDateTime;
import java.time.Duration;

public class AGV {
    private final int id;
    private final int batteryLevel;
    private final LocalDateTime arrivalTime;

    public AGV(int id, int batteryLevel) {
        this.id = id;
        this.batteryLevel = batteryLevel;
        this.arrivalTime = LocalDateTime.now();
    }

    public boolean isWaitingTooLong() {
        return Duration.between(arrivalTime, LocalDateTime.now())
                .toMinutes() > 15;
    }

    public int getId() { return id; }
    public int getBatteryLevel() { return batteryLevel; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
}
