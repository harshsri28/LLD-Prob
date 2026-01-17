package org.example.formatter;

import org.example.models.LogMessage;

public interface LogFormatter {
    String format(LogMessage logMessage);
}
