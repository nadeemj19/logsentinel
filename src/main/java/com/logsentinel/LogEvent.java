package com.logsentinel;

public record LogEvent(String time, String user, String ip, boolean  success) {

}