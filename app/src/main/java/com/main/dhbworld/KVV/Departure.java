package com.main.dhbworld.KVV;

import java.time.LocalDateTime;

public class Departure {
    private final String line;
    private final String platform;
    private final String attribute;
    private final String destination;
    private final LocalDateTime departureTime;
    private final boolean notServiced;

    public Departure(String line, String platform, String attribute, String destination, LocalDateTime departureTime, boolean notServiced) {
        this.line = line;
        this.platform = platform;
        this.attribute = attribute;
        this.destination = destination;
        this.departureTime = departureTime;
        this.notServiced = notServiced;
    }

    public String getLine() {
        return line;
    }

    public String getPlatform() {
        return platform;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public boolean isNotServiced() {
        return notServiced;
    }
}
