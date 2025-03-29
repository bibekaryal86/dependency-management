package dep.mgmt.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class TaskQueue {

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
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return queue.poll();
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
        return null;
      }
    }
  }
}
