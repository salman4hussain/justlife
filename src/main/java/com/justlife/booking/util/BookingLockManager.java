package com.justlife.booking.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class BookingLockManager {

    private final ConcurrentHashMap<TimeRange, Object> lockMap = new ConcurrentHashMap<>();
    private final Object monitor = new Object();

    public void acquireLock(TimeRange newRange) {
        synchronized (monitor) {
            while (lockMap.keySet().stream().anyMatch(existing -> existing.overlapsWith(newRange))) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for booking lock", e);
                }
            }
            lockMap.put(newRange, new Object());
        }
    }

    public void releaseLock(TimeRange range) {
        synchronized (monitor) {
            lockMap.remove(range);
            monitor.notifyAll();
        }
    }
}
