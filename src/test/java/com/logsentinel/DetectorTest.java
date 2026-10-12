package com.logsentinel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetectorTest {
    private final Detector detector = new Detector();

    @Test
    void rootLoginRaisesHighAlert() {
        List<LogEvent> events = List.of(new LogEvent("Oct 12 11:45:09", "root", "192.0.2.50", true));

        List<Alert> alerts = detector.detect(events);

        assertEquals(1, alerts.size());
        assertEquals("HIGH", alerts.get(0).severity());

    }

    @Test
    void bruteForceRaisesMediumAlert() {
        List<LogEvent> events = List.of(
                new LogEvent("Oct 13 12:56:12", "root", "200.0.3.10", false),
                new LogEvent("Oct 13 12:56:14", "user", "200.0.3.10", false),
                new LogEvent("Oct 13 12:56:15", "admin", "200.0.3.10", false));

        List<Alert> alerts = detector.detect(events);

        assertEquals(1, alerts.size());
        assertEquals("MEDIUM", alerts.get(0).severity());


    }

    @Test
    void fewFailuresRaiseNoAlert() {
        List<LogEvent> events = List.of(
                new LogEvent("Oct 13 12:56:12", "root", "200.0.3.10", false),
                new LogEvent("Oct 13 12:56:15", "root", "200.0.3.10", false));

        List<Alert> alerts = detector.detect(events);

        assertEquals(0, alerts.size());

    }

}

