package dep.mgmt.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.LoggerFactory;

public class LogCaptureUtils {

  private static final Logger ROOT_LOGGER =
      (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
  private static boolean isStarted = false;
  private static ListAppender<ILoggingEvent> listAppender;

  public static void start(final boolean isIncludeDebugLogs) {
    if (!isStarted) {
      if (isIncludeDebugLogs) {
        ROOT_LOGGER.setLevel(Level.DEBUG);
      }

      listAppender = new ListAppender<>();
      listAppender.start();
      ROOT_LOGGER.addAppender(listAppender);

      isStarted = true;
    }
  }

  public static void stop() {
    if (isStarted) {
      ROOT_LOGGER.detachAppender(listAppender);
      listAppender.stop();
      isStarted = false;
      ROOT_LOGGER.setLevel(Level.INFO);
    }
  }

  public static String getCapturedLogs() {
    if (listAppender == null || !listAppender.isStarted()) {
      return null;
    }

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    List<String> logs =
        listAppender.list.stream()
            .map(
                event ->
                    String.format(
                        "[%s][%s][%s][%s][%s] --- %s",
                        formatter.format(Instant.ofEpochMilli(event.getTimeStamp())),
                        event.getLoggerContextVO().getName(),
                        event.getThreadName(),
                        event.getLevel(),
                        event.getLoggerName(),
                        event.getFormattedMessage()))
            .toList();
    return String.join(System.lineSeparator(), logs);
  }

  public static void clear() {
    listAppender.list.clear();
  }
}
