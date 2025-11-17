package com.storage.agv;

import java.util.concurrent.*;
import java.time.*;
import java.util.logging.*;

public class ChargingStation {
    private final Semaphore chargingSlots;
    private final int totalSlots;
    private final BlockingQueue<AGV> waitingQueue;
    private final Logger logger = Logger.getLogger(ChargingStation.class.getName());
    private final ExecutorService executorService;

    public ChargingStation(int numberOfSlots) {
        this.totalSlots = numberOfSlots;
        this.chargingSlots = new Semaphore(numberOfSlots, true); // fair queue
        this.waitingQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newCachedThreadPool();
        startQueueMonitoring();
    }

    public void arriveForCharging(AGV agv) {
        executorService.submit(() -> {
            try {
                logger.info(String.format("AGV-%d arrived at %s",
                        agv.getId(), agv.getArrivalTime()));

                waitingQueue.put(agv);
                processCharging(agv);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("AGV-" + agv.getId() + " interrupted");
            }
        });
    }

    private void processCharging(AGV agv) throws InterruptedException {
        if (agv.isWaitingTooLong()) {
            waitingQueue.remove(agv);
            logger.warning(String.format("AGV-%d left queue - waiting > 15 min",
                    agv.getId()));
            return;
        }

        logger.info(String.format("AGV-%d waiting for charging slot... Queue size: %d",
                agv.getId(), waitingQueue.size()));

        if (chargingSlots.tryAcquire(15, TimeUnit.MINUTES)) {
            try {
                waitingQueue.remove(agv);
                charge(agv);
            } finally {
                chargingSlots.release();
                logger.info(String.format("AGV-%d released charging slot. Available: %d/%d",
                        agv.getId(), chargingSlots.availablePermits(), totalSlots));
            }
        } else {
            waitingQueue.remove(agv);
            logger.warning(String.format("AGV-%d timeout - left queue after 15 min",
                    agv.getId()));
        }
    }

    private void charge(AGV agv) {
        logger.info(String.format("AGV-%d CHARGING STARTED. Battery: %d%%. Available slots: %d/%d",
                agv.getId(), agv.getBatteryLevel(),
                chargingSlots.availablePermits(), totalSlots));

        try {
            // Simulate charging time (2-5 seconds)
            int chargingTime = ThreadLocalRandom.current().nextInt(2000, 5000);
            Thread.sleep(chargingTime);

            logger.info(String.format("AGV-%d CHARGING COMPLETED in %d ms",
                    agv.getId(), chargingTime));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void startQueueMonitoring() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000); // Check every 5 seconds
                    waitingQueue.removeIf(agv -> {
                        if (agv.isWaitingTooLong()) {
                            logger.warning(String.format(
                                    "AGV-%d removed from queue - exceeded 15 min wait",
                                    agv.getId()));
                            return true;
                        }
                        return false;
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public int getAvailableSlots() {
        return chargingSlots.availablePermits();
    }

    public int getQueueSize() {
        return waitingQueue.size();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}