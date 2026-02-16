package coen448.computablefuture.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {

    /**
     * Task A: Fail-Fast (Atomic Policy)
     */
    public CompletableFuture<String> processAsyncFailFast(List<MicroService> services, List<String> messages) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            futures.add(services.get(i).retrieveAsync(messages.get(i)));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" ")));
    }

    /**
     * Task B: Fail-Partial (Best-Effort Policy)
     */
    public CompletableFuture<List<String>> processAsyncFailPartial(List<MicroService> services, List<String> messages) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            CompletableFuture<String> call = services.get(i).retrieveAsync(messages.get(i))
                .handle((result, ex) -> (ex != null) ? null : result);
            futures.add(call);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    /**
     * Task C: Fail-Soft (Fallback Policy)
     * Replaces any failure with the provided fallbackValue.
     */
    public CompletableFuture<List<String>> processAsyncFailSoft(List<MicroService> services, List<String> messages, String fallbackValue) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            // .exceptionally handles the error by returning the fallback string
            CompletableFuture<String> call = services.get(i).retrieveAsync(messages.get(i))
                .exceptionally(ex -> fallbackValue);
            futures.add(call);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }
}