package com.logsentinel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("samples/auth.log"));
        for (String line : lines) {
            System.out.println(line);
        }
        LogEvent e = new LogEvent("Oct 12 15:09:01", "root",
                "203.0.115.8", false);
        System.out.println(e.ip());


    }

}