package org.example.services;

import org.example.enums.Loglevel;
import org.example.handlers.LogHandler;
import org.example.models.LogMessage;

public class Logger {
    static Logger instance ;

    LogHandler handlerChain;

    public Logger() {
        handlerChain=  LogHandlerConfiguration.build();
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(Loglevel level, String message) {
        LogMessage msg = new LogMessage(message, level, System.currentTimeMillis());
        handlerChain.handle(msg);
    }

    public void debug(String message) {
        log(Loglevel.DEBUG, message);
    }

    public void info(String message) {
        log(Loglevel.INFO, message);
    }

    public void warn(String message) {
        log(Loglevel.WARN, message);
    }

    public void error(String message) {
        log(Loglevel.ERROR, message);
    }

    public void fatal(String message) {
        log(Loglevel.FATAL, message);
    }
}
