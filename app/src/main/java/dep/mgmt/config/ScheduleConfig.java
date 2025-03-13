package dep.mgmt.config;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dep.mgmt.util.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** simple scheduler without using any third party library */
public class ScheduleConfig {
  private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public static void init() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Shutting down scheduler...");
                  scheduler.shutdown();
                  try {
                    if (!scheduler.awaitTermination(ConstantUtils.SCHEDULER_TIMEOUT, TimeUnit.SECONDS)) {
                      scheduler.shutdownNow();
                    }
                  } catch (InterruptedException ignored) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                  }
                }));

    recreateAppCaches();
  }

  private static void recreateAppCaches() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
    ZonedDateTime executionTime = ZonedDateTime.now(ZoneId.systemDefault()).withHour(ConstantUtils.SCHEDULER_START_HOUR).withMinute(ConstantUtils.SCHEDULER_START_MINUTE).withSecond(ConstantUtils.SCHEDULER_START_SECOND);

    // If the scheduled time has already passed today, schedule for the next day
    if (now.isAfter(executionTime)) {
      executionTime = executionTime.plusDays(1);
    }

    final long initialDelay = Duration.between(now, executionTime).toMillis();

    scheduler.schedule(
        () -> {
          log.info("Starting Scheduler to Update Repos...");

          // TODO -> what needs to be scheduled?
          // Reschedule the next execution
          recreateAppCaches();
        },
        initialDelay,
        TimeUnit.MILLISECONDS);
  }
}
