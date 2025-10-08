package dep.mgmt.service;

import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.LogEntryEntity;
import dep.mgmt.repository.LogEntryRepository;
import dep.mgmt.util.LogCaptureUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEntryService {

  private static final Logger log = LoggerFactory.getLogger(LogEntryService.class);

  private final LogEntryRepository logEntryRepository;

  public LogEntryService() {
    this.logEntryRepository = new LogEntryRepository(MongoDbConfig.getDatabase());
  }

  public void saveLogEntry(final String logs) {
    log.debug("Save Log Entry: [{}]", logs);

    final String logEntries;
    if (CommonUtilities.isEmpty(logs)) {
      logEntries = LogCaptureUtils.getCapturedLogs();
    } else {
      logEntries = logs;
    }

    if (!CommonUtilities.isEmpty(logEntries)) {
      CompletableFuture.runAsync(
              () ->
                  logEntryRepository.insert(
                      new LogEntryEntity(null, LocalDateTime.now(), logEntries)))
          .exceptionally(
              ex -> {
                log.error("Error Saving Log Entry...", ex);
                return null;
              });
    }
  }

  public List<LogEntryEntity> getLogEntries(final LocalDate logDate) {
    log.debug("Get Log Entries: LogDate=[{}]", logDate);
    return logEntryRepository.getLogEntriesByDate(logDate);
  }

  public void scheduledCleanup(final LocalDateTime cleanupBeforeDate) {
    logEntryRepository.deleteByUpdateDateTimeBefore(cleanupBeforeDate);
    log.info("Deleted Log Entries before: [{}]", cleanupBeforeDate);
  }
}
