package com.storage.agv;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class TaskExecutor {
    private final Semaphore availableAGVs;
    private final ExecutorService taskExecutor;
    private final Logger logger = Logger.getLogger(TaskExecutor.class.getName());
    private final int totalAGVs;

    public TaskExecutor(int numberOfAGVs, int numberOfTasks) {
        this.totalAGVs = numberOfAGVs;
        this.availableAGVs = new Semaphore(numberOfAGVs, true);
        this.taskExecutor = Executors.newFixedThreadPool(numberOfTasks);
    }

    public void executeTask(Runnable task, int taskId) {
        taskExecutor.submit(() -> {
            try {
                logger.info(String.format("Task-%d waiting for AGV... Available: %d/%d",
                        taskId, availableAGVs.availablePermits(), totalAGVs));

                availableAGVs.acquire();

                logger.info(String.format("Task-%d acquired AGV. Running...", taskId));
                task.run();
                logger.info(String.format("Task-%d completed", taskId));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("Task-" + taskId + " interrupted");
            } finally {
                availableAGVs.release();
                logger.info(String.format("Task-%d released AGV. Available: %d/%d",
                        taskId, availableAGVs.availablePermits(), totalAGVs));
            }
        });
    }

    public void shutdown() {
        taskExecutor.shutdown();
        try {
            if (!taskExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            taskExecutor.shutdownNow();
        }
    }
}