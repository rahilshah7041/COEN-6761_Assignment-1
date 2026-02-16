package coen448.computablefuture.test;

import java.util.concurrent.*;

public class MicroService {
    private final String serviceId;
    private boolean shouldFail = false; 

    public MicroService(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Allows the test to manually trigger a failure without using Mockito.
     */
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public CompletableFuture<String> retrieveAsync(String message) {
        // If the flag is set, return a failed future immediately
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("Failure at " + serviceId));
        }

        return CompletableFuture.supplyAsync(() -> {
            // Adds jitter to ensure nondeterministic execution order
            int delayMs = ThreadLocalRandom.current().nextInt(0, 31);
            try {
                TimeUnit.MILLISECONDS.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return serviceId + ":" + message.toUpperCase();
        });
    }
}