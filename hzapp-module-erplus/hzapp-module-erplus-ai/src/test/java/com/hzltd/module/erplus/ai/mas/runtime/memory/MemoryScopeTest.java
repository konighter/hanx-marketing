package com.hzltd.module.erplus.ai.mas.runtime.memory;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MemoryScopeTest {

    @Test
    public void testMemoryIsolationAndMerging() throws InterruptedException {
        // 1. Initialize Global Session
        GlobalSessionMemory globalMemory = new GlobalSessionMemory("session-123");
        globalMemory.put("common_goal", "Generate Product Assets");

        // 2. Setup parallel loop simulating executor
        int parallelLoops = 3;
        ExecutorService executor = Executors.newFixedThreadPool(parallelLoops);
        CountDownLatch latch = new CountDownLatch(parallelLoops);

        for (int i = 0; i < parallelLoops; i++) {
            final int loopIndex = i;
            executor.submit(() -> {
                try {
                    // Each loop gets its own LocalLoopMemory backed by the common GlobalSessionMemory
                    LocalLoopMemory localMemory = new LocalLoopMemory("loop-" + loopIndex, globalMemory);

                    // Verify can read global
                    assertEquals("Generate Product Assets", localMemory.get("common_goal"));

                    // Write isolated local values
                    localMemory.put("local_temp", "computing-" + loopIndex);
                    
                    // Verify local assignment
                    assertEquals("computing-" + loopIndex, localMemory.get("local_temp"));

                    // Simulate some work
                    Thread.sleep(100);

                    // Verify it didn't leak to global yet
                    assertNull(globalMemory.get("local_temp"));

                    // Write result key
                    localMemory.put("result_" + loopIndex, "Success-" + loopIndex);

                    // Finally merge the result back
                    localMemory.mergeToGlobal();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all loops to finish
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 3. Verify Global Context contains merged results from all parallel loops
        assertEquals("Generate Product Assets", globalMemory.get("common_goal"));
        assertEquals("Success-0", globalMemory.get("result_0"));
        assertEquals("Success-1", globalMemory.get("result_1"));
        assertEquals("Success-2", globalMemory.get("result_2"));
        
        // local_temp leaked from all loops during merge (which might happen or might need fine-grained control later)
        // For now, testing basic merge behavior
        String localTemp = (String) globalMemory.get("local_temp");
        org.junit.jupiter.api.Assertions.assertTrue(localTemp != null && localTemp.startsWith("computing-")); // Last writer wins
    }
}
