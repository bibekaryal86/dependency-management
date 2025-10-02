package dep.mgmt.model.schedule;

import dep.mgmt.model.RequestMetadata;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.service.UpdateRepoService;
import dep.mgmt.util.ConstantUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SchedulerJobs {
  private static final UpdateRepoService updateRepoService = new UpdateRepoService();

  public static class ScheduledUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      updateRepoService.scheduledUpdate(
          new RequestMetadata(
              RequestParams.UpdateType.ALL,
              Boolean.TRUE,
              Boolean.FALSE,
              Boolean.FALSE,
              Boolean.FALSE,
              Boolean.TRUE,
              Boolean.TRUE,
              Boolean.FALSE,
              LocalDate.now(),
              null));
    }
  }

  public static class ScheduledMongoRepoUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      updateRepoService.scheduledMongoUpdate();
    }
  }

  public static class ScheduledMongoRepoCleanupJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      updateRepoService.scheduledCleanup(
          LocalDateTime.now().minusDays(ConstantUtils.CLEANUP_BEFORE_DAYS));
    }
  }
}
