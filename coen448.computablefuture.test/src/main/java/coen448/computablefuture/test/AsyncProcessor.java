package coen448.computablefuture.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {

    // Task A implementation [cite: 39]
    public CompletableFuture<String> processAsyncFailFast(List<MicroService> services, List<String> messages) {
        // Start all service calls concurrently [cite: 25]
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            futures.add(services.get(i).retrieveAsync(messages.get(i)));
        }

        // CompletableFuture.allOf handles the "Atomic" requirement: 
        // if one fails, the aggregate future fails [cite: 24, 44]
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                // Returns combined results only if all succeed [cite: 46]
                return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.joining(" "));
            });
            // Exceptions are not caught, so they propagate to the caller [cite: 45]
    }

    // Keep the processAsyncCompletionOrder method you were provided here
    public CompletableFuture<List<String>> processAsyncCompletionOrder(List<MicroService> microservices, String message) {
        // ... (use the code you provided in your prompt)
        return null; // Ensure you keep your original implementation here
    }
}