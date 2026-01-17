package org.example.services;

import org.example.appenders.LogAppender;
import org.example.enums.Loglevel;
import org.example.handlers.*;

public class LogHandlerConfiguration {
    static LogHandler debug = new DebugHandler();
    static LogHandler info = new InfoHandler();
    static LogHandler warn = new WarnHandler();
    static LogHandler error = new ErrorHandler();
    static LogHandler fatal = new FatalHandler();

    public static LogHandler build(){
        debug.setNext(info);
        info.setNext(warn);
        warn.setNext(error);
        error.setNext(fatal);
        return debug;
    }

    public static void addAppenderForLevel(Loglevel loglevel, LogAppender logAppender){
        switch (loglevel){
            case DEBUG -> debug.subsribe(logAppender);
            case INFO -> info.subsribe(logAppender);
            case WARN -> warn.subsribe(logAppender);
            case ERROR -> error.subsribe(logAppender);
            case FATAL -> fatal.subsribe(logAppender);
        }
    }
}
