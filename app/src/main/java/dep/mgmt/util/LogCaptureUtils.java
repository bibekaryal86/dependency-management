package dep.mgmt.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogCaptureUtils {

  private static final Logger ROOT_LOGGER = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
  private static final ListAppender<ILoggingEvent> LIST_APPENDER = new ListAppender<>();
  private static boolean isStarted = false;

  public static void start(final boolean isIncludeDebugLogs) {
    if (!isStarted) {
      if (isIncludeDebugLogs) {
        ROOT_LOGGER.setLevel(Level.DEBUG);
      }
      LIST_APPENDER.start();
      ROOT_LOGGER.addAppender(LIST_APPENDER);
      isStarted = true;
    }
  }

  public static void stop() {
    if (isStarted) {
      ROOT_LOGGER.detachAppender(LIST_APPENDER);
      LIST_APPENDER.stop();
      isStarted = false;
      ROOT_LOGGER.setLevel(Level.INFO);
    }
  }

  public static List<String> getCapturedLogs() {
    return LIST_APPENDER.list.stream()
            .map(ILoggingEvent::getFormattedMessage)
            .toList();
  }

  public static void clear() {
    LIST_APPENDER.list.clear();
  }
}
