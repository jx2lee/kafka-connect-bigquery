package com.coinone.kafka.connect.bigquery.util;

import java.time.Duration;

public final class Timer {
    private final long startTime;

    private Timer() {
        this.startTime = System.nanoTime();
    }

    public static Timer start() {
        return new Timer();
    }

    public Duration getElapsedTime() {
        long elapsedNano = System.nanoTime() - this.startTime;
        return Duration.ofNanos(elapsedNano);
    }
}
