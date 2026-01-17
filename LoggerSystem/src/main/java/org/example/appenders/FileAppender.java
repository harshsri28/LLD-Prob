package org.example.appenders;

import org.example.formatter.LogFormatter;
import org.example.models.LogMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class FileAppender implements LogAppender {
    LogFormatter formatter;
    BufferedWriter writer;

    public FileAppender(LogFormatter formatter, String fileName) {
        this.formatter = formatter;

        try {
            this.writer = new BufferedWriter(new FileWriter(fileName, true));
        } catch (Exception e) {
            throw new RuntimeException("Failed to open log file", e);
        }
    }

    @Override
    public void append(LogMessage logMessage) {
        try {
            writer.write(formatter.format(logMessage));
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
