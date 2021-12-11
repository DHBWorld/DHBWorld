package com.main.dhbworld.Firebase;

import androidx.annotation.NonNull;

class Issue {
    private long timestamp;
    private int problem;

    Issue(long timestamp, int problem) {
        this.timestamp = timestamp;
        this.problem = problem;
    }

    Issue() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getProblem() {
        return problem;
    }

    public void setProblem(int problem) {
        this.problem = problem;
    }

    @NonNull
    @Override
    public String toString() {
        return "Timestamp: " + this.timestamp + "; isProblem: " + this.problem;
    }
}
