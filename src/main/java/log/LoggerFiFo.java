package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerFiFo {
    private static LoggerFiFo ourInstance = new LoggerFiFo();
    private static final Logger rootLogger = LogManager.getRootLogger();

    public static LoggerFiFo getInstance() {
        return ourInstance;
    }

    private LoggerFiFo() {
    }
    public Logger getRootLogger() {
        return rootLogger;
    }
}
