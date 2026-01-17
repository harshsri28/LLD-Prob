package org.example.handlers;

import org.example.enums.Loglevel;
import org.example.models.LogMessage;

public class WarnHandler extends LogHandler {
    @Override
    public boolean canHandle(LogMessage logMessage) {
        return logMessage.getLoglevel() == Loglevel.WARN;
    }
}
