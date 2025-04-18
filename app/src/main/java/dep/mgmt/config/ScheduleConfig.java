package dep.mgmt.config;

import dep.mgmt.service.ProcessSummaryService;
import dep.mgmt.service.UpdateRepoService;
import dep.mgmt.util.ConstantUtils;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** simple scheduler without using any third party library */
public class ScheduleConfig {
  private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);
  private static final ScheduledExecutorService scheduler =
      Executors.newScheduledThreadPool(
          1,
          runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); // Allow JVM to exit if this is the only thread left
            return thread;
          });

  private static final UpdateRepoService updateRepoService = new UpdateRepoService();
  private static final ProcessSummaryService processSummaryService = new ProcessSummaryService();

  public static void init() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Shutting down scheduler...");
                  scheduler.shutdown();
                  try {
                    if (!scheduler.awaitTermination(
                        ConstantUtils.SCHEDULER_TIMEOUT, TimeUnit.SECONDS)) {
                      scheduler.shutdownNow();
                    }
                  } catch (InterruptedException ignored) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                  }
                }));

    updateReposSchedule();
  }

  private static void updateReposSchedule() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
    ZonedDateTime executionTime =
        ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(ConstantUtils.SCHEDULER_START_HOUR_UPDATE_REPO)
            .withMinute(ConstantUtils.SCHEDULER_START_MINUTE)
            .withSecond(ConstantUtils.SCHEDULER_START_SECOND);

    if (now.isAfter(executionTime)) {
      executionTime = executionTime.plusDays(1);
    }

    final long initialDelay = Duration.between(now, executionTime).toMillis();
    final long period = TimeUnit.DAYS.toMillis(1);

    scheduler.scheduleAtFixedRate(
        () -> {
          log.info("Starting Scheduler to Update Repos...");
          updateRepoService.scheduledUpdate();
        },
        initialDelay,
        period,
        TimeUnit.MILLISECONDS);
  }
}
