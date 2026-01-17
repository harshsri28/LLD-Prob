package org.example.appenders;

import org.example.models.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
}
