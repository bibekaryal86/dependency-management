package dep.mgmt.config;

import dep.mgmt.model.schedule.SchedulerJobs;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleConfig {
  private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);

  private static final Scheduler scheduler;

  static {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
    } catch (SchedulerException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void init() throws SchedulerException {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Shutting down scheduler...");
                  try {
                    scheduler.shutdown(Boolean.FALSE);
                  } catch (SchedulerException ignored) {
                  }
                }));

    scheduler.start();

    final JobDetail jobDetailScheduledUpdate = jobDetailScheduledUpdate();
    final Trigger triggerScheduledUpdate = triggerScheduledUpdate(jobDetailScheduledUpdate);
    scheduler.scheduleJob(jobDetailScheduledUpdate, triggerScheduledUpdate);

    final JobDetail jobDetailMongoRepoUpdateAm = jobDetailScheduledMongoRepoUpdateAm();
    final Trigger triggerMongoRepoUpdateAm =
        triggerScheduledMongoRepoUpdateAm(jobDetailMongoRepoUpdateAm);
    scheduler.scheduleJob(jobDetailMongoRepoUpdateAm, triggerMongoRepoUpdateAm);

    final JobDetail jobDetailMongoRepoUpdatePm = jobDetailScheduledMongoRepoUpdatePm();
    final Trigger triggerMongoRepoUpdatePm =
        triggerScheduledMongoRepoUpdatePm(jobDetailMongoRepoUpdatePm);
    scheduler.scheduleJob(jobDetailMongoRepoUpdatePm, triggerMongoRepoUpdatePm);

    final JobDetail jobDetailMongoRepoCleanup = jobDetailScheduledMongoRepoCleanup();
    final Trigger triggerMongoRepoCleanup =
        triggerScheduledMongoRepoCleanup(jobDetailMongoRepoCleanup);
    scheduler.scheduleJob(jobDetailMongoRepoCleanup, triggerMongoRepoCleanup);
  }

  private static JobDetail jobDetailScheduledUpdate() {
    return JobBuilder.newJob(SchedulerJobs.ScheduledUpdateJob.class)
        .withIdentity("Job_ScheduledUpdate")
        .build();
  }

  private static Trigger triggerScheduledUpdate(final JobDetail jobDetail) {
    return getTrigger("Trigger_ScheduledUpdate", "0 0 20 * * ?", jobDetail);
  }

  private static JobDetail jobDetailScheduledMongoRepoUpdateAm() {
    return JobBuilder.newJob(SchedulerJobs.ScheduledMongoRepoUpdateJob.class)
        .withIdentity("Job_ScheduledMongoRepoUpdateAm")
        .build();
  }

  private static Trigger triggerScheduledMongoRepoUpdateAm(final JobDetail jobDetail) {
    return getTrigger("Trigger_ScheduledMongoRepoUpdateAm", "0 0 6 * * ?", jobDetail);
  }

  private static JobDetail jobDetailScheduledMongoRepoUpdatePm() {
    return JobBuilder.newJob(SchedulerJobs.ScheduledMongoRepoUpdateJob.class)
        .withIdentity("Job_ScheduledMongoRepoUpdatePm")
        .build();
  }

  private static Trigger triggerScheduledMongoRepoUpdatePm(final JobDetail jobDetail) {
    return getTrigger("Trigger_ScheduledMongoRepoUpdatePm", "0 0 18 * * ?", jobDetail);
  }

  private static JobDetail jobDetailScheduledMongoRepoCleanup() {
    return JobBuilder.newJob(SchedulerJobs.ScheduledMongoRepoCleanupJob.class)
        .withIdentity("Job_ScheduledMongoRepoCleanup")
        .build();
  }

  private static Trigger triggerScheduledMongoRepoCleanup(final JobDetail jobDetail) {
    return getTrigger("Trigger_ScheduledMongoRepoCleanup", "0 0 19 * * ?", jobDetail);
  }

  private static Trigger getTrigger(final String id, final String ce, final JobDetail jd) {
    return TriggerBuilder.newTrigger()
        .withIdentity(id)
        .withSchedule(CronScheduleBuilder.cronSchedule(ce))
        .forJob(jd)
        .build();
  }

  public static Map<String, LocalDateTime> nextRunTimes() {
    final Map<String, LocalDateTime> jobsNextRunTimes = new HashMap<>();

    try {
      for (final String jobGroupName : scheduler.getJobGroupNames()) {
        for (final JobKey jobKey :
            scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName))) {
          final List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
          for (final Trigger t : triggers) {
            final LocalDateTime nextFireTime =
                LocalDateTime.ofInstant(t.getNextFireTime().toInstant(), ZoneId.systemDefault());
            jobsNextRunTimes.put(jobKey.getName(), nextFireTime);
          }
        }
      }
    } catch (SchedulerException ex) {
      log.error("Error Getting Next Run Times...", ex);
    }

    // sorted by value
    return jobsNextRunTimes.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
            LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
  }
}
