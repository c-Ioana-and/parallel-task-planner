/* Implement this class. */
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

import static java.lang.Math.round;

// create comparator for tasks
class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getPriority() == t2.getPriority()) {
            return t1.getId() - t2.getId();
        }
        return t2.getPriority() - t1.getPriority();
    }
}

public class MyHost extends Host {
    private final PriorityQueue<Task> queue = new PriorityQueue<>(new TaskComparator());
    private Task currentTask;
    private long workLeft;
    private long last_start;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (currentTask == null) {
                if (!queue.isEmpty()) {
                    // get the next task from the queue, and update the work left
                    // the time left for the current task will be calculated in getWorkLeft()
                    currentTask = queue.poll();
                    workLeft -= currentTask.getLeft();

                    // save the time when the task started, relevant for getWorkLeft()
                    last_start = round(Timer.getTimeDouble());
                }
            }
            else {
                // measure the time it takes for the task to finish
                double start = Timer.getTimeDouble();
                synchronized (this){
                    try{
                        wait(currentTask.getLeft());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                double finish = Timer.getTimeDouble();
                //  if the task is preemptible, and it hasn't finished, it was preempted
                long left = round((float)currentTask.getLeft()/ 1000L - (finish - start));
                if (currentTask.isPreemptible() && left > 0) {
                    currentTask.setLeft(left * 1000L);
                    workLeft += currentTask.getLeft();
                    if (currentTask.getLeft() > 0) {
                        queue.add(currentTask);
                    }
                }
                else {
                    // task was finished, if the task was at one point preemepted, update the work left
                    currentTask.finish();
                }
                // time for a new task
                currentTask = null;
            }
        }
    }

    @Override
    public void addTask(Task task) {
        queue.add(task);
        workLeft += task.getDuration();
        if (currentTask != null) {
            if (currentTask.isPreemptible() && task.getPriority() > currentTask.getPriority()) {
                synchronized (this){
                    notify();
                }
            }
        }
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public long getWorkLeft() {
        return round((float)workLeft / 1000L);
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public long getLastStart() {
        return last_start;
    }

    @Override
    public void shutdown() {
        interrupt();
    }
}
