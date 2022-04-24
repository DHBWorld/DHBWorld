package com.main.dhbworld.KVV;

import java.time.LocalDateTime;

public class Departure {
    private String line;
    private String platform;
    private String attribute;
    private String destination;
    private LocalDateTime departureTime;

    public Departure(String line, String platform, String attribute, String destination, LocalDateTime departureTime) {
        this.line = line;
        this.platform = platform;
        this.attribute = attribute;
        this.destination = destination;
        this.departureTime = departureTime;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "Departure{" +
                "line='" + line + '\'' +
                ", platform='" + platform + '\'' +
                ", attribute='" + attribute + '\'' +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                '}';
    }
}
