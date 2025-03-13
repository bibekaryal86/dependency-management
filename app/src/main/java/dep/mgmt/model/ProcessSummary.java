package dep.mgmt.model;

import dep.mgmt.model.entity.ProcessSummaryEntity;
import java.io.Serializable;
import java.util.List;

public class ProcessSummary implements Serializable {
  private final List<ProcessSummaryEntity> processSummaries;
  private final Integer currentPage;
  private final Integer totalPages;
  private final Integer totalElements;
  private final Integer pageSize;

  public ProcessSummary(List<ProcessSummaryEntity> processSummaries) {
    this.processSummaries = processSummaries;
    this.currentPage = 1;
    this.totalPages = 0;
    this.totalElements = 0;
    this.pageSize = 100;
  }

  public ProcessSummary(
      List<ProcessSummaryEntity> processSummaries,
      Integer currentPage,
      Integer totalPages,
      Integer totalElements,
      Integer pageSize) {
    this.processSummaries = processSummaries;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.pageSize = pageSize;
  }
}
