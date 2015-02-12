package betix.core.logger;

import org.slf4j.ILoggerFactory;

public class LoggerFactory {

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String clazzName) {
        org.slf4j.Logger wrappedLogger = org.slf4j.LoggerFactory.getLogger(clazzName);
        return new Logger(wrappedLogger);
    }

    public static ILoggerFactory getILoggerFactory() {
        return org.slf4j.LoggerFactory.getILoggerFactory();
    }

}
