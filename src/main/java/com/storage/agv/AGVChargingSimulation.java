package com.storage.agv;

import java.util.concurrent.*;
import java.util.logging.*;

public class AGVChargingSimulation {
    private static final Logger logger = Logger.getLogger(AGVChargingSimulation.class.getName());

    public static void main(String[] args) throws InterruptedException {
        final int CHARGING_STATIONS = 3;
        final int NUM_AGVS = 10;
        final int NUM_TASKS = 5;
        final int AVAILABLE_AGVS = 3;

        ChargingStation chargingStation = new ChargingStation(CHARGING_STATIONS);
        TaskExecutor taskExecutor = new TaskExecutor(AVAILABLE_AGVS, NUM_TASKS);

        ScheduledExecutorService agvArrivalSimulator = Executors.newScheduledThreadPool(1);
        agvArrivalSimulator.scheduleAtFixedRate(() -> {
            int agvId = ThreadLocalRandom.current().nextInt(1, NUM_AGVS + 1);
            int batteryLevel = ThreadLocalRandom.current().nextInt(10, 50);
            AGV agv = new AGV(agvId, batteryLevel);
            chargingStation.arriveForCharging(agv);
        }, 0, 2, TimeUnit.SECONDS); // AGV arrives every 2 seconds

        for (int i = 1; i <= NUM_TASKS; i++) {
            final int taskId = i;
            taskExecutor.executeTask(() -> {
                try {
                    // Simulate task work (3-6 seconds)
                    int workTime = ThreadLocalRandom.current().nextInt(3000, 6000);
                    logger.info(String.format("Task-%d working for %d ms", taskId, workTime));
                    Thread.sleep(workTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, taskId);
        }

        Thread.sleep(30000);

        logger.info("Shutting down simulation...");
        agvArrivalSimulator.shutdown();
        taskExecutor.shutdown();
        chargingStation.shutdown();

        logger.info("Simulation completed");
    }
}
