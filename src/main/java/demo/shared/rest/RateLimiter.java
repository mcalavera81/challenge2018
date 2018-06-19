package demo.shared.rest;

interface RateLimiter {

    boolean tryAcquire();
    void stop();
}
