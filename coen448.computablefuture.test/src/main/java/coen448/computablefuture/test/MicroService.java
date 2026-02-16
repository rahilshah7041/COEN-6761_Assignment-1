package coen448.computablefuture.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MicroService {
    
    private final String serviceId;
    private boolean shouldFail = false; 

    public MicroService(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public CompletableFuture<String> retrieveAsync(String message) {
        if (shouldFail) {
            // Task A Requirement: Propagate exceptions [cite: 45]
            return CompletableFuture.failedFuture(new RuntimeException("Failure at " + serviceId));
        }

        return CompletableFuture.supplyAsync(() -> {
            // Jitter: 0..30ms ensures nondeterministic completion order [cite: 16, 75]
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