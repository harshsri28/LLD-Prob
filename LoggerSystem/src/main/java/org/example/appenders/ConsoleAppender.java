package org.example.appenders;

import org.example.formatter.LogFormatter;
import org.example.models.LogMessage;

public class ConsoleAppender implements LogAppender {
    LogFormatter formatter;

    public ConsoleAppender(LogFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void append(LogMessage logMessage) {
        System.out.println(formatter.format(logMessage));
    }

}
