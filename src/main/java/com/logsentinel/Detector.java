package com.logsentinel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detector {

    public List<Alert> detect(List<LogEvent> events) {
        List<Alert> alerts = new ArrayList<>();
        Map<String, Integer> failures = new HashMap<>();
        for (LogEvent event : events) {
            if (event.success() && event.user().equals("root")) {
                alerts.add(new Alert("HIGH", "Root login from " + event.ip()));
            }
            if (!event.success()) {
                failures.put(event.ip(), failures.getOrDefault(event.ip(), 0) + 1);


            }
            for (String ip : failures.keySet()) {
                int count = failures.get(ip);
                if (count >= 3) {
                    alerts.add(new Alert("MEDIUM", "Brute force " + ip + " failed " + count + " times"));

                }
            }
        }

        return alerts;
    }
}