package com.logsentinel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("samples/auth.log"));
        LogParser parser = new LogParser();
        for (String line : lines) {
            LogEvent event = parser.parse(line);
            if (event != null) {
                System.out.println(event);
            }
        }


    }
}



