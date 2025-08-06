package com.dvvz.omdb.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Rate limit exceeded - too many requests");
    }
}