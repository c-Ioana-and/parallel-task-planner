/* Implement this class. */

import java.util.List;

import static java.lang.Math.round;

public class MyDispatcher extends Dispatcher {
    private static int ID = -1;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            ID = (ID + 1) % hosts.size();
            hosts.get(ID).addTask(task);
        }
        else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            // search for the host with the shortest queue + current task
            int min = Integer.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < hosts.size(); i++) {
                int size = hosts.get(i).getQueueSize();
                if (((MyHost)hosts.get(i)).getCurrentTask() != null) {
                    size++;
                }
                if (size < min) {
                    min = size;
                    index = i;
                }
            }

            hosts.get(index).addTask(task);
        }
        else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            // assign tasks to hosts based on their type: SHORT -> MEDIUM -> LONG
            switch (task.getType()) {
                case SHORT -> hosts.get(0).addTask(task);
                case MEDIUM -> hosts.get(1).addTask(task);
                case LONG -> hosts.get(2).addTask(task);
            }
        }
        else if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            // search for the host with the least work left
            long min = Long.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < hosts.size(); i++) {
                long work = round(hosts.get(i).getWorkLeft());
                Task currTask = ((MyHost)hosts.get(i)).getCurrentTask();
                if (currTask != null) {
                    long elapsed = round(Timer.getTimeDouble() - ((MyHost)hosts.get(i)).getLastStart());
                    work += currTask.getDuration() / 1000L - elapsed;
                }
                if (work < min) {
                    min = work;
                    index = i;
                }
            }
            hosts.get(index).addTask(task);
        }
    }
}
