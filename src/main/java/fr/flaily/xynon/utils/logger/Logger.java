package fr.flaily.xynon.utils.logger;

import fr.flaily.xynon.Xynon;

public class Logger {
    public String loggerName;

    public Logger(String loggerName) {
        this.loggerName = loggerName;
    }

    public void sendLog(String s) {
        throw new RuntimeException("Cannot use super class Logger");
    }

    public void defaultLog(String s) {
        Xynon.INSTANCE.log.info("["+loggerName+"] " + s);
    }
}
