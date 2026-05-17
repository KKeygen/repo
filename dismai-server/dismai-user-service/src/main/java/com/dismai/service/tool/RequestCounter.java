package com.dismai.service.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RequestCounter {

    private static final long WINDOW_MILLIS = 1000L;

    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
    @Value("${request_count_threshold:1000}")
    private int maxRequestsPerSecond = 1000;

    public boolean onRequest() {
        long currentTime = System.currentTimeMillis();

        long lastReset = lastResetTime.get();
        if (currentTime - lastReset >= WINDOW_MILLIS && lastResetTime.compareAndSet(lastReset, currentTime)) {
            count.set(0);
        }

        if (count.incrementAndGet() > maxRequestsPerSecond) {
            log.warn("请求超过每秒{}次限制", maxRequestsPerSecond);
            return true;
        }
        return false;
    }
}
