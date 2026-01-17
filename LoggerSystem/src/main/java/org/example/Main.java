package org.example;

import org.example.appenders.ConsoleAppender;
import org.example.appenders.FileAppender;
import org.example.enums.Loglevel;
import org.example.formatter.PlainTextFornatter;
import org.example.services.LogHandlerConfiguration;
import org.example.services.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

        LogHandlerConfiguration.addAppenderForLevel(Loglevel.INFO, new ConsoleAppender(new PlainTextFornatter()));

        LogHandlerConfiguration.addAppenderForLevel(Loglevel.ERROR, new ConsoleAppender(new PlainTextFornatter()));

        LogHandlerConfiguration.addAppenderForLevel(Loglevel.ERROR, new FileAppender(new PlainTextFornatter(), "error.log"));

        logger.info("This is some info");
        logger.error("This is some error");

    }
}