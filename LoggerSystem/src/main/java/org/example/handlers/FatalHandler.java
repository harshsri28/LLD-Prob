package org.example.handlers;

import org.example.enums.Loglevel;
import org.example.models.LogMessage;

public class FatalHandler extends LogHandler {
    @Override
    public boolean canHandle(LogMessage logMessage) {
        return logMessage.getLoglevel() == Loglevel.FATAL;
    }
}
