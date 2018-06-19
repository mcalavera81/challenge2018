package demo.shared.rest;

import lombok.val;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SimpleRateLimiter implements RateLimiter{

    private final Semaphore semaphore;
    private final int maxPermits;
    private final int periodSeconds;

    private ScheduledExecutorService scheduler;

    public static SimpleRateLimiter build(int permits, int periodSeconds) {
        val limiter = new SimpleRateLimiter(permits, periodSeconds);
        limiter.schedulePermitReplenishment();
        return limiter;
    }

 
    private SimpleRateLimiter(int permits, int periodSeconds) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;
        this.periodSeconds = periodSeconds;
    }
 
    public boolean tryAcquire() {
        try {
            return semaphore.tryAcquire(60, SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }
 
    public void stop() {
        scheduler.shutdownNow();
    }
 
    private void schedulePermitReplenishment() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::freeAllPermits, periodSeconds, periodSeconds, SECONDS);
 
    }

    private void freeAllPermits() {
        semaphore.release(maxPermits - semaphore.availablePermits());
    }
}