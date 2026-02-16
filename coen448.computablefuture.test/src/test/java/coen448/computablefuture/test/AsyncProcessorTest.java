package coen448.computablefuture.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AsyncProcessorTest {

    /**
     * Test for Task A: Fail-Fast (Atomic Policy)
     * Verifies that if one service fails, the entire operation fails and
     * the exception propagates to the caller[cite: 24, 71].
     */
    @Test
    public void testFailFast_WhenOneServiceFails_ShouldThrowException() {
        // Arrange
        AsyncProcessor processor = new AsyncProcessor();
        MicroService s1 = new MicroService("Service1");
        MicroService s2 = new MicroService("Service2");
        
        // Simulate a failure in one service [cite: 71]
        s2.setShouldFail(true); 

        List<MicroService> services = List.of(s1, s2);
        List<String> messages = List.of("task1", "task2");

        // Act
        CompletableFuture<String> resultFuture = processor.processAsyncFailFast(services, messages);

        // Assert: Verify exception propagates (assertThrows) [cite: 71]
        assertThrows(ExecutionException.class, () -> {
            // Requirement: Use a timeout to verify liveness 
            resultFuture.get(2, TimeUnit.SECONDS);
        });
    }

    /**
     * Test for Success Case of Fail-Fast
     * Verifies that when all services succeed, results are aggregated[cite: 25].
     */
    @Test
    public void testFailFast_Success_ShouldReturnCombinedResult() throws Exception {
        // Arrange
        AsyncProcessor processor = new AsyncProcessor();
        MicroService s1 = new MicroService("S1");
        MicroService s2 = new MicroService("S2");

        List<MicroService> services = List.of(s1, s2);
        List<String> messages = List.of("hello", "world");

        // Act
        CompletableFuture<String> resultFuture = processor.processAsyncFailFast(services, messages);
        String result = resultFuture.get(2, TimeUnit.SECONDS);

        // Assert
        assertTrue(result.contains("S1:HELLO"));
        assertTrue(result.contains("S2:WORLD"));
    }
}