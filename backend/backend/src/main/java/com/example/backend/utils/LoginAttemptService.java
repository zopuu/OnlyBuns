package com.example.backend.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPTS = 5;
    private final long TIME_WINDOW = 60*1000; //60sec

    private final Map<String, List<Long>> attempts = new ConcurrentHashMap<>();

    public synchronized boolean isBlocked(String ip) {
        long now = System.currentTimeMillis();
        List<Long> timestamps = attempts.getOrDefault(ip,new ArrayList<>());

        timestamps.removeIf(t -> (now - t) > TIME_WINDOW);

        if(timestamps.size() >= MAX_ATTEMPTS) {
            return true;
        }
        timestamps.add(now);
        attempts.put(ip,timestamps);
        return false;
    }
}
