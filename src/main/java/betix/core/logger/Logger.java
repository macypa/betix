package betix.core.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Logger
        implements org.slf4j.Logger, LocationAwareLogger, AppenderAttachable<ILoggingEvent>, Serializable {

    ch.qos.logback.classic.Logger wrappedLogger = null;

    private static final File logDir = new File("./log/");

    static {
        logDir.mkdirs();
    }

    public Logger(org.slf4j.Logger wrappedLogger) {
        this.wrappedLogger = (ch.qos.logback.classic.Logger) wrappedLogger;
    }

    @Override
    public void addAppender(Appender<ILoggingEvent> newAppender) {
        wrappedLogger.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return wrappedLogger.iteratorForAppenders();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((wrappedLogger == null) ? 0 : wrappedLogger.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Logger other = (Logger) obj;
        if (wrappedLogger == null) {
            if (other.wrappedLogger != null)
                return false;
        } else if (!wrappedLogger.equals(other.wrappedLogger))
            return false;
        return true;
    }

    public Level getEffectiveLevel() {
        return wrappedLogger.getEffectiveLevel();
    }

    public Level getLevel() {
        return wrappedLogger.getLevel();
    }

    public String getName() {
        return wrappedLogger.getName();
    }

    public void setLevel(Level newLevel) {
        wrappedLogger.setLevel(newLevel);
    }

    public void detachAndStopAllAppenders() {
        wrappedLogger.detachAndStopAllAppenders();
    }

    public boolean detachAppender(String name) {
        return wrappedLogger.detachAppender(name);
    }

    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return wrappedLogger.isAttached(appender);
    }

    public Appender<ILoggingEvent> getAppender(String name) {
        return wrappedLogger.getAppender(name);
    }

    public void callAppenders(ILoggingEvent event) {
        wrappedLogger.callAppenders(event);
    }

    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return wrappedLogger.detachAppender(appender);
    }

    public void trace(String msg) {
        wrappedLogger.trace(msg);
    }

    public void trace(String format, Object arg) {
        wrappedLogger.trace(format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        wrappedLogger.trace(format, arg1, arg2);
    }

    public void trace(String format, Object... argArray) {
        wrappedLogger.trace(format, argArray);
    }

    public void trace(String msg, Throwable t) {
        wrappedLogger.trace(msg, t);
    }

    public void trace(Marker marker, String msg) {
        wrappedLogger.trace(marker, msg);
    }

    public void trace(Marker marker, String format, Object arg) {
        wrappedLogger.trace(marker, format, arg);
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.trace(marker, format, arg1, arg2);
    }

    public void trace(Marker marker, String format, Object... argArray) {
        wrappedLogger.trace(marker, format, argArray);
    }

    public void trace(Marker marker, String msg, Throwable t) {
        wrappedLogger.trace(marker, msg, t);
    }

    public void trace(String format, Throwable t, Object... argArray) {
        if (wrappedLogger.isTraceEnabled()) {
            List arr = Arrays.asList(argArray);
            arr.add(t.getClass());
            arr.add(t.getLocalizedMessage());
            arr.add(t.getStackTrace());
            wrappedLogger.trace(format + "\n{}:{}\n{}", arr.toArray());
        }
    }

    public boolean isDebugEnabled() {
        return wrappedLogger.isDebugEnabled();
    }

    public boolean isDebugEnabled(Marker marker) {
        return wrappedLogger.isDebugEnabled(marker);
    }

    public void debug(String msg) {
        wrappedLogger.debug(msg);
    }

    public void debug(String format, Object arg) {
        wrappedLogger.debug(format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        wrappedLogger.debug(format, arg1, arg2);
    }

    public void debug(String format, Object... argArray) {
        wrappedLogger.debug(format, argArray);
    }

    public void debug(String msg, Throwable t) {
        wrappedLogger.debug(msg, t);
    }

    public void debug(Marker marker, String msg) {
        wrappedLogger.debug(marker, msg);
    }

    public void debug(Marker marker, String format, Object arg) {
        wrappedLogger.debug(marker, format, arg);
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.debug(marker, format, arg1, arg2);
    }

    public void debug(Marker marker, String format, Object... argArray) {
        wrappedLogger.debug(marker, format, argArray);
    }

    public void debug(Marker marker, String msg, Throwable t) {
        wrappedLogger.debug(marker, msg, t);
    }

    public void debug(String format, Throwable t, Object... argArray) {
        if (wrappedLogger.isDebugEnabled()) {
            List arr = Arrays.asList(argArray);
            arr.add(t.getClass());
            arr.add(t.getLocalizedMessage());
            arr.add(t.getStackTrace());
            wrappedLogger.debug("{}\n{}:{}\n{}", format, arr.toArray());
        }
    }

    public void error(String msg) {
        msg = String.format("%s Screenshot filename %s", msg, takePicture());
        wrappedLogger.error(msg);
    }

    public void error(String format, Object arg) {
        if (arg instanceof Throwable) {
            error(format, (Throwable) arg);
        }

        format = String.format("%s Screenshot filename %s", format, takePicture());
        wrappedLogger.error(format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        if (arg2 instanceof Throwable) {
            String msg = format.replace("{}", arg1.toString());
            error(msg, (Throwable) arg2);
        }

        format = String.format("%s Screenshot filename %s", format, takePicture());
        wrappedLogger.error(format, arg1, arg2);
    }

    public void error(String format, Object... argArray) {
        format = String.format("%s Screenshot filename %s", format, takePicture());
        wrappedLogger.error(format, argArray);
    }

    public void error(String msg, Throwable t) {
        msg = String.format("%s Screenshot filename %s", msg, takePicture());
        wrappedLogger.error(msg, t);
    }

    public void error(Marker marker, String msg) {
        wrappedLogger.error(marker, msg);
    }

    public void error(Marker marker, String format, Object arg) {
        wrappedLogger.error(marker, format, arg);
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.error(marker, format, arg1, arg2);
    }

    public void error(Marker marker, String format, Object... argArray) {
        wrappedLogger.error(marker, format, argArray);
    }

    public void error(Marker marker, String msg, Throwable t) {
        wrappedLogger.error(marker, msg, t);
    }

    public void error(String format, Throwable t, Object... argArray) {
        if (wrappedLogger.isErrorEnabled()) {
            format = String.format("%s Screenshot filename %s", format, takePicture());
            List arr = Arrays.asList(argArray);
            arr.add(t.getClass());
            arr.add(t.getLocalizedMessage());
            arr.add(t.getStackTrace());
            wrappedLogger.error("{}\n{}:{}\n{}", format, arr.toArray());
        }
    }

    public boolean isInfoEnabled() {
        return wrappedLogger.isInfoEnabled();
    }

    public boolean isInfoEnabled(Marker marker) {
        return wrappedLogger.isInfoEnabled(marker);
    }

    public void info(String msg) {
        wrappedLogger.info(msg);
    }

    public void info(String format, Object arg) {
        wrappedLogger.info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        wrappedLogger.info(format, arg1, arg2);
    }

    public void info(String format, Object... argArray) {
        wrappedLogger.info(format, argArray);
    }

    public void info(String msg, Throwable t) {
        wrappedLogger.info(msg, t);
    }

    public void info(Marker marker, String msg) {
        wrappedLogger.info(marker, msg);
    }

    public void info(Marker marker, String format, Object arg) {
        wrappedLogger.info(marker, format, arg);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.info(marker, format, arg1, arg2);
    }

    public void info(Marker marker, String format, Object... argArray) {
        wrappedLogger.info(marker, format, argArray);
    }

    public void info(Marker marker, String msg, Throwable t) {
        wrappedLogger.info(marker, msg, t);
    }

    public void info(String format, Throwable t, Object... argArray) {
        if (wrappedLogger.isInfoEnabled()) {
            List arr = Arrays.asList(argArray);
            arr.add(t.getClass());
            arr.add(t.getLocalizedMessage());
            arr.add(t.getStackTrace());
            wrappedLogger.info("{}\n{}:{}\n{}", format, arr.toArray());
        }
    }

    public boolean isTraceEnabled() {
        return wrappedLogger.isTraceEnabled();
    }

    public boolean isTraceEnabled(Marker marker) {
        return wrappedLogger.isTraceEnabled(marker);
    }

    public boolean isErrorEnabled() {
        return wrappedLogger.isErrorEnabled();
    }

    public boolean isErrorEnabled(Marker marker) {
        return wrappedLogger.isErrorEnabled(marker);
    }

    public boolean isWarnEnabled() {
        return wrappedLogger.isWarnEnabled();
    }

    public boolean isWarnEnabled(Marker marker) {
        return wrappedLogger.isWarnEnabled(marker);
    }

    public boolean isEnabledFor(Marker marker, Level level) {
        return wrappedLogger.isEnabledFor(marker, level);
    }

    public boolean isEnabledFor(Level level) {
        return wrappedLogger.isEnabledFor(level);
    }

    public void warn(String msg) {
        wrappedLogger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
        wrappedLogger.warn(msg, t);
    }

    public void warn(String format, Object arg) {
        wrappedLogger.warn(format, arg);
    }

    public void warn(String format, Object arg1, Object arg2) {
        wrappedLogger.warn(format, arg1, arg2);
    }

    public void warn(String format, Object... argArray) {
        wrappedLogger.warn(format, argArray);
    }

    public void warn(String format, Throwable t, Object... argArray) {
        if (wrappedLogger.isWarnEnabled()) {
            List arr = Arrays.asList(argArray);
            arr.add(t.getClass());
            arr.add(t.getLocalizedMessage());
            arr.add(t.getStackTrace());
            wrappedLogger.warn("{}\n{}:{}\n{}", format, arr.toArray());
        }
    }

    public void warn(Marker marker, String msg) {
        wrappedLogger.warn(marker, msg);
    }

    public void warn(Marker marker, String format, Object arg) {
        wrappedLogger.warn(marker, format, arg);
    }

    public void warn(Marker marker, String format, Object... argArray) {
        wrappedLogger.warn(marker, format, argArray);
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.warn(marker, format, arg1, arg2);
    }

    public void warn(Marker marker, String msg, Throwable t) {
        wrappedLogger.warn(marker, msg, t);
    }

    public boolean isAdditive() {
        return wrappedLogger.isAdditive();
    }

    public void setAdditive(boolean additive) {
        wrappedLogger.setAdditive(additive);
    }

    public String toString() {
        return wrappedLogger.toString();
    }

    public LoggerContext getLoggerContext() {
        return wrappedLogger.getLoggerContext();
    }

    public void log(Marker marker, String fqcn, int levelInt, String message, Object[] argArray, Throwable t) {
        wrappedLogger.log(marker, fqcn, levelInt, message, argArray, t);
    }

    public void log(Marker marker, String fqcn, int levelInt, String message, Throwable t, Object... argArray) {
        wrappedLogger.log(marker, fqcn, levelInt, message, argArray, t);
    }

    public String takePicture() {
        String name = String.valueOf(System.currentTimeMillis());
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRect);
            ImageIO.write(image, "png", new File(logDir, name + ".png"));
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
}
