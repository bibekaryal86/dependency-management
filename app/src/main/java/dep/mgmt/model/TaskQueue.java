package dep.mgmt.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {

    private final String name;
    private final BlockingQueue<OneTask> queue = new LinkedBlockingQueue<>();

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
        return queue.poll();
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

        // for methods that return value
        public OneTask(final String name, final Callable<Object> action) {
            this.name = name;
            this.action = action;
        }

        // for methods that are void
        public OneTask(final String name, final Runnable action) {
            this.name = name;
            this.action = () -> {
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
