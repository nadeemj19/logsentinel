package com.logsentinel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        String path = "samples/auth.log";
        if (args.length > 0) {
            path = args[0];
        }
        List<String> lines = Files.readAllLines(Path.of(path));
        LogParser parser = new LogParser();
        List<LogEvent> events = new ArrayList<>();
        for (String line : lines) {
            LogEvent event = parser.parse(line);
            if (event != null) {
                events.add(event);
            }

        }

        System.out.println("Parsed " + events.size() + " login events");

        Detector detector = new Detector();
        List<Alert> alerts = detector.detect(events);
        for (Alert alert : alerts) {
            System.out.println(alert);
        }

        AiAnalyzer analyzer = new AiAnalyzer();
        AiReport report = analyzer.analyze(alerts);
        System.out.println("=== AI Analysis ===");
        System.out.println(report.summary());
        for (AiFinding finding : report.findings()) {
            System.out.println(finding);
        }
    }
}



