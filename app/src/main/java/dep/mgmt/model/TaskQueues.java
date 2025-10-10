package dep.mgmt.model;

import dep.mgmt.util.ProcessUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskQueues {
  private static final Logger log = LoggerFactory.getLogger(TaskQueues.class);

  private final BlockingQueue<TaskQueue> queueOfQueues = new LinkedBlockingQueue<>();
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private final AtomicLong nonEmptyQueueCount = new AtomicLong(0);
  private final AtomicBoolean isProcessing = new AtomicBoolean(Boolean.FALSE);

  public void addQueue(TaskQueue taskQueue) {
    boolean added = queueOfQueues.offer(taskQueue);
    if (added && !taskQueue.isEmpty()) {
      nonEmptyQueueCount.incrementAndGet();
    }
  }

  public List<TaskQueue> getQueueOfQueues() {
    return new ArrayList<>(queueOfQueues);
  }

  public void clearQueue() {
    nonEmptyQueueCount.set(0);
    queueOfQueues.forEach(TaskQueue::clearQueue);
    queueOfQueues.clear();
  }

  public TaskQueue getQueueByName(String name) {
    for (TaskQueue queue : queueOfQueues) {
      if (queue.getName().equals(name)) {
        return queue;
      }
    }
    return null;
  }

  public void updateOneTaskDelay(
      final String queueName, final String taskName, final long newDelayMillis) {
    for (final TaskQueue taskQueue : queueOfQueues) {
      if (taskQueue.getName().equals(queueName)) {
        for (final TaskQueue.OneTask task : taskQueue.getTaskQueue()) {
          if (task.getName().equals(taskName)) {
            task.setDelayMillis(newDelayMillis);
            log.info(
                "Updated Delay for Queue=[{}] Task=[{}] to [{}ms]",
                queueName,
                taskName,
                newDelayMillis);
            ProcessUtils.updateProcessedTasksDelayMillis(taskName, newDelayMillis);
          }
        }
      }
    }
  }

  public Future<String> processQueues() {
    log.debug("Process Queues...");
    if (isProcessing()) {
      throw new IllegalStateException("Queues are already being processed...");
    }

    if (executor.isShutdown() || executor.isTerminated()) {
      executor = Executors.newSingleThreadExecutor();
    }

    isProcessing.set(true);
    return executor.submit(
        () -> {
          StringBuilder result = new StringBuilder();
          while (!Thread.currentThread().isInterrupted()) {
            try {
              TaskQueue taskQueue = queueOfQueues.poll(1, TimeUnit.SECONDS);

              if (taskQueue == null && areAllQueuesEmpty()) {
                break;
              }

              if (taskQueue != null) {
                while (!taskQueue.isEmpty()) {
                  TaskQueue.OneTask oneTask = taskQueue.pollTask();
                  if (oneTask != null) {
                    Object object = oneTask.execute();
                    if (object != null) {
                      result.append(object);
                    }
                  }
                }
                nonEmptyQueueCount.decrementAndGet();
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
            }
          }
          isProcessing.set(false);
          return result.toString();
        });
  }

  private boolean areAllQueuesEmpty() {
    return nonEmptyQueueCount.get() == 0;
  }

  public boolean isProcessing() {
    return isProcessing.get();
  }

  public void restartExecutor() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }

    // Reset queue state
    clearQueue();

    // Create a new executor instance
    executor = Executors.newSingleThreadExecutor();
  }

  public static class TaskQueue {
    private final String name;
    private final BlockingQueue<OneTask> queue = new LinkedBlockingQueue<>();

    public TaskQueue(final String name) {
      this.name = name;
    }

    public List<OneTask> getTaskQueue() {
      return new ArrayList<>(queue);
    }

    public String getName() {
      return name;
    }

    public void addTask(OneTask task) {
      ProcessUtils.addProcessedTasks(name, task.getName(), task.getDelayMillis());
      queue.offer(task);
    }

    public OneTask pollTask() {
      try {
        return queue.poll(100, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return null;
    }

    public void clearQueue() {
      queue.clear();
    }

    public boolean isEmpty() {
      return queue.isEmpty();
    }

    public static class OneTask {
      private final String name;
      private final Callable<Object> action;
      private final AtomicLong delayMillis = new AtomicLong(0);
      private final ExecutorService singleTaskExecutor;

      // for methods that return value
      public OneTask(final String name, final Callable<Object> action, final long delayMillis) {
        this.name = name;
        this.action = action;
        this.delayMillis.set(delayMillis);
        this.singleTaskExecutor = Executors.newSingleThreadExecutor();
      }

      // for methods that are void
      public OneTask(final String name, final Runnable action, final long delayMillis) {
        this(name, Executors.callable(action), delayMillis);
      }

      public String getName() {
        return name;
      }

      public long getDelayMillis() {
        return delayMillis.get();
      }

      public void setDelayMillis(final long newDelayMillis) {
        delayMillis.set(newDelayMillis);
      }

      public Object execute() {
        log.debug("Update Processed Task Started: [{}]", name);
        ProcessUtils.updateProcessedTasksStarted(name);
        Object result = null;
        Future<Object> future = null;
        final long delayMillisGet = delayMillis.get();
        final long timeoutMillis = delayMillisGet > 0 ? delayMillisGet + 300_000 : 300_000;

        try {
          future =
              singleTaskExecutor.submit(
                  () -> {
                    if (delayMillisGet > 0) {
                      Thread.sleep(delayMillisGet);
                    }
                    return action.call();
                  });

          result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
          future.cancel(true); // attempt to interrupt
          String msg = String.format("OneTask [%s] timed out after %d ms", name, timeoutMillis);
          log.warn(msg);
          result = msg;
          log.debug("Update Processed Task Timed Out: [{}]", name);
          ProcessUtils.updateProcessedTasksTimedOut(name);
        } catch (Exception e) {
          final String message =
              String.format(
                  "OneTask: [%s] [%s] : %s",
                  this.name, e.getClass().getSimpleName(), e.getMessage());
          log.error(message);
          result = message;
        } finally {
          log.debug("Update Processed Task Ended: [{}]", name);
          ProcessUtils.updateProcessedTasksEnded(name);
          singleTaskExecutor.shutdownNow();
        }
        return result;
      }

      public void shutdownTaskExec() {
        singleTaskExecutor.shutdownNow();
      }
    }
  }
}
