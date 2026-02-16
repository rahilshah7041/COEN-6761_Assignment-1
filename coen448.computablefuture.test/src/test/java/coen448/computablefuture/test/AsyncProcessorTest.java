package coen448.computablefuture.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        s2.setShouldFail(true); // S2 will fail, S1 will succeed

        List<MicroService> services = List.of(s1, s2);
        List<String> messages = List.of("hello", "world");

        CompletableFuture<List<String>> resultFuture = processor.processAsyncFailPartial(services, messages);
        List<String> results = resultFuture.get(2, TimeUnit.SECONDS);

        assertEquals(1, results.size());
        assertEquals("S1:HELLO", results.get(0));
    }
}