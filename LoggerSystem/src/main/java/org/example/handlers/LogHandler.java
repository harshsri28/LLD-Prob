package org.example.handlers;

import org.example.appenders.LogAppender;
import org.example.models.LogMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LogHandler {
    LogHandler next;
    List<LogAppender> appenders = new CopyOnWriteArrayList<>();

    public void subsribe(LogAppender observer) {
        appenders.add(observer);
    }

    public void notifyObserver(LogMessage logMessage) {
        for (LogAppender logAppender : appenders) {
            logAppender.append(logMessage);
        }
    }

    public void handle(LogMessage logMessage) {
        if(canHandle(logMessage)){
            notifyObserver(logMessage);
        }else if(next != null){
            next.handle(logMessage);
        }
    }

    public LogHandler getNext() {
        return next;
    }

    public void setNext(LogHandler next) {
        this.next = next;
    }

    public List<LogAppender> getAppenders() {
        return appenders;
    }

    public void setAppenders(List<LogAppender> appenders) {
        this.appenders = appenders;
    }

    public abstract boolean canHandle(LogMessage logMessage);

}
