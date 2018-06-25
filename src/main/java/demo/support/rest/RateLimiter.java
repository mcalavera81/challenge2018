package demo.support.rest;

interface RateLimiter {

    boolean tryAcquire();
    void stop();
}
