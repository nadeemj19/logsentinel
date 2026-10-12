package com.logsentinel;

public class LogParser {
    public LogEvent parse(String line) {

        if (!line.contains("Failed password") && !line.contains("Accepted password")) {
            return null;
        }

        boolean success = line.contains("Accepted password");

        String[] parts = line.split(" ");

        String time = parts[0] + " " + parts[1] + " " + parts[2];

        String user = null;
        String ip = null;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("from")) {

                user = parts[i - 1];

                ip = parts[i + 1];


            }



        }

        return new LogEvent(time, user, ip, success);

    }


}



