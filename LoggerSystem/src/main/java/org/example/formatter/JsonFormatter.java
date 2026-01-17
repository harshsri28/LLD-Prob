package org.example.formatter;

import org.example.models.LogMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JsonFormatter implements LogFormatter {
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public String format(LogMessage logMessage) {
        String formattedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(logMessage.getTimestamp()), ZoneId.systemDefault())
                .format(FORMATTER);
        return String.format("{\"timestamp\": \"%s\", \"loglevel\": \"%s\", \"message\": \"%s\"}", formattedTime, logMessage.getLoglevel(), logMessage.getMessage());
    }
}

