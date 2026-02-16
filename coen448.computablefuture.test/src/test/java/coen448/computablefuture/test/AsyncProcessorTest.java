package coen448.computablefuture.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AsyncProcessorTest {

    @Test
    void testFailFast_FailureThrowsException() {
        AsyncProcessor processor = new AsyncProcessor();
        MicroService s1 = new MicroService("S1");
        MicroService s2 = new MicroService("S2");
        s2.setShouldFail(true);

        CompletableFuture<String> result = processor.processAsyncFailFast(List.of(s1, s2), List.of("m1", "m2"));
        assertThrows(ExecutionException.class, () -> result.get(2, TimeUnit.SECONDS));
    }

    @Test
    void testFailPartial_ReturnsOnlySuccesses() throws Exception {
        AsyncProcessor processor = new AsyncProcessor();
        MicroService s1 = new MicroService("S1");
        MicroService s2 = new MicroService("S2");
        s2.setShouldFail(true);

        CompletableFuture<List<String>> resultFuture = processor.processAsyncFailPartial(List.of(s1, s2), List.of("h1", "h2"));
        List<String> results = resultFuture.get(2, TimeUnit.SECONDS);

        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("S1"));
    }

    @Test
    void testFailSoft_ShouldReplaceFailureWithFallback() throws Exception {
        AsyncProcessor processor = new AsyncProcessor();
        MicroService s1 = new MicroService("S1");
        MicroService s2 = new MicroService("S2");
        s2.setShouldFail(true);

        String fallback = "N/A";
        CompletableFuture<List<String>> resultFuture = processor.processAsyncFailSoft(List.of(s1, s2), List.of("d1", "d2"), fallback);
        List<String> results = resultFuture.get(2, TimeUnit.SECONDS);

        assertEquals(2, results.size()); 
        assertEquals("N/A", results.get(1));
    }
}