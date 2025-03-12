package dep.mgmt.service;

import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.ProcessSummary;
import dep.mgmt.model.entity.ProcessSummaryEntity;
import dep.mgmt.repository.ProcessSummaryRepository;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ProcessSummaryService {

  private static final Logger log = LoggerFactory.getLogger(ProcessSummaryService.class);

  private final ProcessSummaryRepository processSummaryRepository;

  public ProcessSummaryService() {
    this.processSummaryRepository = new ProcessSummaryRepository(MongoDbConfig.getDatabase());
  }

  // TODO add schedule
  public void cleanupOldProcessSummaries() {
    LocalDateTime cleanupBeforeDate = LocalDateTime.now().minusDays(ConstantUtils.CLEANUP_BEFORE_DAYS);
    processSummaryRepository.deleteByUpdateDateTimeBefore(cleanupBeforeDate);
    log.info("Deleted Process Summaries before: [{}]", cleanupBeforeDate);
  }

  public ProcessSummary getProcessSummaries(
      final String updateType,
      final LocalDate updateDate,
      final int pageNumber,
      final int pageSize) {
    log.debug(
        "Get Process Summaries: [ {} ] | [ {} ] | [ {} ] | [ {} ]",
        updateType,
        updateDate,
        pageNumber,
        pageSize);

    LocalDateTime startOfDay = updateDate.atStartOfDay();
    LocalDateTime endOfDay = updateDate.atTime(LocalTime.MAX);

    if (CommonUtilities.isEmpty(updateType)) {
      return processSummaryRepository.findAll(pageNumber, pageSize);
    } else {
      return processSummaryRepository.findByUpdateType(updateType, pageNumber, pageSize);
    }
  }

  public void saveProcessSummary(final ProcessSummaryEntity processSummary) {
    log.debug("Save Process Summary: [ {} ]", processSummary);
    processSummaryRepository.insert(processSummary);
  }
}
