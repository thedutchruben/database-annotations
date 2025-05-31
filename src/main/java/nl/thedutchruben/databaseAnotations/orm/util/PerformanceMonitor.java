package nl.thedutchruben.databaseAnotations.orm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Performance monitoring utility for tracking ORM operations.
 */
public class PerformanceMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);
    
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    private final ConcurrentHashMap<String, OperationStats> stats = new ConcurrentHashMap<>();
    private final AtomicLong totalOperations = new AtomicLong();
    private final AtomicLong totalExecutionTime = new AtomicLong();
    
    private boolean enabled = false;
    private long slowQueryThreshold = 1000; // milliseconds
    
    private PerformanceMonitor() {
        // Singleton
    }
    
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    /**
     * Enables or disables performance monitoring.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            logger.info("Performance monitoring enabled");
        } else {
            logger.info("Performance monitoring disabled");
        }
    }
    
    /**
     * Sets the threshold for slow query logging (in milliseconds).
     */
    public void setSlowQueryThreshold(long thresholdMs) {
        this.slowQueryThreshold = thresholdMs;
    }
    
    /**
     * Records the execution of an operation.
     */
    public void recordOperation(String operationType, String sql, long executionTimeMs) {
        if (!enabled) {
            return;
        }
        
        totalOperations.incrementAndGet();
        totalExecutionTime.addAndGet(executionTimeMs);
        
        // Record stats for this operation type
        stats.computeIfAbsent(operationType, k -> new OperationStats())
             .record(executionTimeMs);
        
        // Log slow queries
        if (executionTimeMs > slowQueryThreshold) {
            logger.warn("Slow query detected ({} ms): {} - SQL: {}", 
                       executionTimeMs, operationType, sql);
        } else {
            logger.debug("Operation completed ({} ms): {} - SQL: {}", 
                        executionTimeMs, operationType, sql);
        }
    }
    
    /**
     * Gets performance statistics summary.
     */
    public PerformanceStats getStats() {
        if (!enabled) {
            return new PerformanceStats();
        }
        
        long totalOps = totalOperations.get();
        long totalTime = totalExecutionTime.get();
        double avgTime = totalOps > 0 ? (double) totalTime / totalOps : 0;
        
        PerformanceStats perfStats = new PerformanceStats();
        perfStats.totalOperations = totalOps;
        perfStats.totalExecutionTimeMs = totalTime;
        perfStats.averageExecutionTimeMs = avgTime;
        
        // Copy operation-specific stats
        stats.forEach((operation, opStats) -> {
            OperationSummary summary = new OperationSummary();
            summary.operationType = operation;
            summary.count = opStats.count.sum();
            summary.totalTimeMs = opStats.totalTime.sum();
            summary.averageTimeMs = summary.count > 0 ? (double) summary.totalTimeMs / summary.count : 0;
            summary.minTimeMs = opStats.minTime.get();
            summary.maxTimeMs = opStats.maxTime.get();
            
            perfStats.operationStats.put(operation, summary);
        });
        
        return perfStats;
    }
    
    /**
     * Resets all performance statistics.
     */
    public void reset() {
        totalOperations.set(0);
        totalExecutionTime.set(0);
        stats.clear();
        logger.info("Performance statistics reset");
    }
    
    /**
     * Logs current performance statistics.
     */
    public void logStats() {
        if (!enabled) {
            logger.info("Performance monitoring is disabled");
            return;
        }
        
        PerformanceStats perfStats = getStats();
        
        logger.info("=== ORM Performance Statistics ===");
        logger.info("Total Operations: {}", perfStats.totalOperations);
        logger.info("Total Execution Time: {} ms", perfStats.totalExecutionTimeMs);
        logger.info("Average Execution Time: {:.2f} ms", perfStats.averageExecutionTimeMs);
        
        if (!perfStats.operationStats.isEmpty()) {
            logger.info("Operation Breakdown:");
            perfStats.operationStats.forEach((op, summary) -> {
                logger.info("  {}: {} ops, avg {:.2f}ms, min {}ms, max {}ms", 
                           op, summary.count, summary.averageTimeMs, 
                           summary.minTimeMs, summary.maxTimeMs);
            });
        }
        
        logger.info("=== End Performance Statistics ===");
    }
    
    /**
     * Internal class for tracking operation statistics.
     */
    private static class OperationStats {
        final LongAdder count = new LongAdder();
        final LongAdder totalTime = new LongAdder();
        final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE);
        final AtomicLong maxTime = new AtomicLong(0);
        
        void record(long executionTime) {
            count.increment();
            totalTime.add(executionTime);
            
            // Update min time
            long currentMin;
            do {
                currentMin = minTime.get();
            } while (executionTime < currentMin && !minTime.compareAndSet(currentMin, executionTime));
            
            // Update max time
            long currentMax;
            do {
                currentMax = maxTime.get();
            } while (executionTime > currentMax && !maxTime.compareAndSet(currentMax, executionTime));
        }
    }
    
    /**
     * Performance statistics data class.
     */
    public static class PerformanceStats {
        public long totalOperations;
        public long totalExecutionTimeMs;
        public double averageExecutionTimeMs;
        public final ConcurrentHashMap<String, OperationSummary> operationStats = new ConcurrentHashMap<>();
    }
    
    /**
     * Operation summary data class.
     */
    public static class OperationSummary {
        public String operationType;
        public long count;
        public long totalTimeMs;
        public double averageTimeMs;
        public long minTimeMs;
        public long maxTimeMs;
    }
}
