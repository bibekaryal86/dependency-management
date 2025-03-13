package dep.mgmt.migration.entities_old;

import java.io.Serializable;
import java.util.List;

public class ProcessSummary implements Serializable {
  private String updateType;
  private int mongoPluginsToUpdate;
  private int mongoDependenciesToUpdate;
  private int mongoPackagesToUpdate;
  private int mongoNpmSkipsActive;
  private int totalPrCreatedCount;
  private int totalPrCreateErrorsCount;
  private int totalPrMergedCount;
  private List<ProcessedRepository> processedRepositories;
  private boolean isErrorsOrExceptions;

  public ProcessSummary() {}

  public ProcessSummary(
      String updateType,
      int mongoPluginsToUpdate,
      int mongoDependenciesToUpdate,
      int mongoPackagesToUpdate,
      int mongoNpmSkipsActive,
      int totalPrCreatedCount,
      int totalPrCreateErrorsCount,
      int totalPrMergedCount,
      List<ProcessedRepository> processedRepositories,
      boolean isErrorsOrExceptions) {
    this.updateType = updateType;
    this.mongoPluginsToUpdate = mongoPluginsToUpdate;
    this.mongoDependenciesToUpdate = mongoDependenciesToUpdate;
    this.mongoPackagesToUpdate = mongoPackagesToUpdate;
    this.mongoNpmSkipsActive = mongoNpmSkipsActive;
    this.totalPrCreatedCount = totalPrCreatedCount;
    this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
    this.totalPrMergedCount = totalPrMergedCount;
    this.processedRepositories = processedRepositories;
    this.isErrorsOrExceptions = isErrorsOrExceptions;
  }

  public String getUpdateType() {
    return updateType;
  }

  public void setUpdateType(String updateType) {
    this.updateType = updateType;
  }

  public int getMongoPluginsToUpdate() {
    return mongoPluginsToUpdate;
  }

  public void setMongoPluginsToUpdate(int mongoPluginsToUpdate) {
    this.mongoPluginsToUpdate = mongoPluginsToUpdate;
  }

  public int getMongoDependenciesToUpdate() {
    return mongoDependenciesToUpdate;
  }

  public void setMongoDependenciesToUpdate(int mongoDependenciesToUpdate) {
    this.mongoDependenciesToUpdate = mongoDependenciesToUpdate;
  }

  public int getMongoPackagesToUpdate() {
    return mongoPackagesToUpdate;
  }

  public void setMongoPackagesToUpdate(int mongoPackagesToUpdate) {
    this.mongoPackagesToUpdate = mongoPackagesToUpdate;
  }

  public int getMongoNpmSkipsActive() {
    return mongoNpmSkipsActive;
  }

  public void setMongoNpmSkipsActive(int mongoNpmSkipsActive) {
    this.mongoNpmSkipsActive = mongoNpmSkipsActive;
  }

  public int getTotalPrCreatedCount() {
    return totalPrCreatedCount;
  }

  public void setTotalPrCreatedCount(int totalPrCreatedCount) {
    this.totalPrCreatedCount = totalPrCreatedCount;
  }

  public int getTotalPrCreateErrorsCount() {
    return totalPrCreateErrorsCount;
  }

  public void setTotalPrCreateErrorsCount(int totalPrCreateErrorsCount) {
    this.totalPrCreateErrorsCount = totalPrCreateErrorsCount;
  }

  public int getTotalPrMergedCount() {
    return totalPrMergedCount;
  }

  public void setTotalPrMergedCount(int totalPrMergedCount) {
    this.totalPrMergedCount = totalPrMergedCount;
  }

  public List<ProcessedRepository> getProcessedRepositories() {
    return processedRepositories;
  }

  public void setProcessedRepositories(List<ProcessedRepository> processedRepositories) {
    this.processedRepositories = processedRepositories;
  }

  public boolean isErrorsOrExceptions() {
    return isErrorsOrExceptions;
  }

  public void setErrorsOrExceptions(boolean errorsOrExceptions) {
    isErrorsOrExceptions = errorsOrExceptions;
  }
}
