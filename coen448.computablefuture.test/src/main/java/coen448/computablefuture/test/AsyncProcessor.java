package coen448.computablefuture.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {

    /**
     * Task A: Fail-Fast (Atomic Policy)
     * If one service fails, the entire operation fails.
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
     * Returns results from successful services; ignores failed ones.
     */
    public CompletableFuture<List<String>> processAsyncFailPartial(List<MicroService> services, List<String> messages) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < services.size(); i++) {
            // .handle() catches exceptions per-service so the whole stream doesn't crash
            CompletableFuture<String> call = services.get(i).retrieveAsync(messages.get(i))
                .handle((result, ex) -> (ex != null) ? null : result);
            futures.add(call);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull) // Remove the nulls (failed calls)
                .collect(Collectors.toList()));
    }
}