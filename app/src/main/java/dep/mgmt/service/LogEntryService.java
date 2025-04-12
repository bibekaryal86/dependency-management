package dep.mgmt.service;

import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.LogEntryEntity;
import dep.mgmt.repository.LogEntryRepository;
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
    CompletableFuture.runAsync(
            () -> logEntryRepository.insert(new LogEntryEntity(null, LocalDateTime.now(), logs)))
        .exceptionally(
            ex -> {
              log.error("Error Saving Log Entry...", ex);
              return null;
            });
  }

  public List<LogEntryEntity> getLogEntries(final LocalDate logDate) {
    log.debug("Get Log Entries: [{}]", logDate);
    return logEntryRepository.getLogEntriesByDate(logDate);
  }
}
