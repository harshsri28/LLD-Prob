package org.example.models;

import org.example.enums.Loglevel;

public class LogMessage {
    String message;
    Loglevel loglevel;
    long timestamp;

    public LogMessage(String message, Loglevel loglevel, long timestamp) {
        this.message = message;
        this.loglevel = loglevel;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Loglevel getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(Loglevel loglevel) {
        this.loglevel = loglevel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
