package dep.mgmt.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TaskQueues {
  private final BlockingQueue<TaskQueue> queueOfQueues = new LinkedBlockingQueue<>();
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final AtomicLong nonEmptyQueueCount = new AtomicLong(0);
  private final AtomicBoolean isProcessing = new AtomicBoolean(Boolean.FALSE);

  public boolean addQueue(TaskQueue taskQueue) {
    if (!isExecutorRunning()) {
      return false;
    }
    boolean added = queueOfQueues.offer(taskQueue);
    if (added && !taskQueue.isEmpty()) {
      nonEmptyQueueCount.incrementAndGet();
    }
    return added;
  }

  public Future<String> processQueues() {
    if (isProcessing.get()) {
      throw new IllegalStateException("Queues are already being processed...");
    }

    isProcessing.set(true);
    return executor.submit(() -> {
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

  public boolean isExecutorRunning() {
    return !executor.isShutdown() && !executor.isTerminated();
  }

  public void shutdown() {
    nonEmptyQueueCount.set(0);
    queueOfQueues.clear();
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public static class TaskQueue {
    private final String name;
    private final BlockingQueue<OneTask> queue = new LinkedBlockingQueue<>();
    private final AtomicLong delayMillis = new AtomicLong(0);

    public TaskQueue(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public boolean addTask(OneTask task) {
      return queue.offer(task);
    }

    public OneTask pollTask() {
      try {
        if (delayMillis.get() > 0) {
          Thread.sleep(delayMillis.get());
        }
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

    public void setDelay(long millis) {
      delayMillis.set(millis);
    }

    public void resetDelay() {
      delayMillis.set(0);
    }

    public static class OneTask {
      private final String name;
      private final Callable<Object> action;

      // for methods that return value
      public OneTask(final String name, final Callable<Object> action) {
        this.name = name;
        this.action = action;
      }

      // for methods that are void
      public OneTask(final String name, final Runnable action) {
        this.name = name;
        this.action =
                () -> {
                  action.run();
                  return null;
                };
      }

      public String getName() {
        return name;
      }

      public Object execute() {
        try {
          return action.call();
        } catch (Exception e) {
          return String.format("OneTask: [%s] [%s] : %s", this.name, e.getClass().getSimpleName(), e.getMessage());
        }
      }
    }
  }
}
