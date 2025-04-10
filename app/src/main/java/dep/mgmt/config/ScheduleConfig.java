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
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
    cleanupProcessSummariesSchedule();
  }

  private static void updateReposSchedule() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
    final ZonedDateTime executionTime =
        getExecutionTime(now, ConstantUtils.SCHEDULER_START_HOUR_UPDATE_REPO);
    final long initialDelay = Duration.between(now, executionTime).toMillis();

    scheduler.schedule(
        () -> {
          log.info("Starting Scheduler to Update Repos...");
          updateRepoService.scheduledUpdate();
          // schedule the next execution
          updateReposSchedule();
        },
        initialDelay,
        TimeUnit.MILLISECONDS);
  }

  private static void cleanupProcessSummariesSchedule() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
    final ZonedDateTime executionTime =
        getExecutionTime(now, ConstantUtils.SCHEDULER_START_HOUR_CLEANUP_PROCESS_SUMMARIES);
    final long initialDelay = Duration.between(now, executionTime).toMillis();

    scheduler.schedule(
        () -> {
          log.info("Starting Scheduler to Cleanup Process Summaries...");
          processSummaryService.scheduledCleanup();
          // Schedule the next execution
          cleanupProcessSummariesSchedule();
        },
        initialDelay,
        TimeUnit.MILLISECONDS);
  }

  private static ZonedDateTime getExecutionTime(
      final ZonedDateTime now, final int schedulerStartHour) {
    ZonedDateTime executionTime =
        ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(schedulerStartHour)
            .withMinute(ConstantUtils.SCHEDULER_START_MINUTE)
            .withSecond(ConstantUtils.SCHEDULER_START_SECOND);

    if (now.isAfter(executionTime)) {
      executionTime = executionTime.plusDays(1);
    }

    return executionTime;
  }
}
