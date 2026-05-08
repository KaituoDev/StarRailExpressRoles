package fun.kaituo.starrailexpressroles.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ServerTaskScheduler {

    private static final List<DelayedTask> delayedTasks = new ArrayList<>();
    private static final List<LoopedTask> loopedTasks = new ArrayList<>();

    private static final List<DelayedTask> delayedTaskBuffer = new Vector<>();
    private static final List<LoopedTask> loopedTaskBuffer = new Vector<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            delayedTasks.addAll(delayedTaskBuffer);
            delayedTaskBuffer.clear();
            Iterator<DelayedTask> dt = delayedTasks.iterator();
            while (dt.hasNext()) {
                DelayedTask task = dt.next();
                if (--task.ticksLeft <= 0) {
                    task.task.run();
                    dt.remove();
                }
            }

            loopedTasks.addAll(loopedTaskBuffer);
            loopedTaskBuffer.clear();
            Iterator<LoopedTask> lt = loopedTasks.iterator();
            while (lt.hasNext()) {
                LoopedTask task = lt.next();

                if (task.intervalTicks <= 0 || --task.intervalTickCountdown <= 0) {
                    task.intervalTickCountdown = task.intervalTicks + 1;
                    task.task.run();

                    if (task.isLimited) {
                        if (--task.cyclesLeft <= 0) {
                            lt.remove();
                        }
                    }
                }
            }
        });
    }

    public static void runTaskLater(Runnable task, int delayTicks) {
        if (delayTicks <= 0) {
            task.run();
        } else {
            delayedTaskBuffer.add(new DelayedTask(task, delayTicks));
        }
    }

    public static void runTaskTimer(Runnable task, int intervalTicks) {
        loopedTaskBuffer.add(new LoopedTask(task, intervalTicks, false, 0));
    }

    public static void runTaskLoop(Runnable task, int intervalTicks, int cycleNum) {
        loopedTaskBuffer.add(new LoopedTask(task, intervalTicks, true, cycleNum));
    }

    private static class DelayedTask {
        Runnable task;
        int ticksLeft;

        public DelayedTask(Runnable task, int delayTicks) {
            this.task = task;
            this.ticksLeft = delayTicks;
        }
    }

    private static class LoopedTask {
        Runnable task;
        int intervalTicks;
        boolean isLimited;
        int cyclesLeft;
        int intervalTickCountdown;

        public LoopedTask(Runnable task, int intervalTicks, boolean isLimited, int cyclesLeft) {
            this.task = task;
            this.intervalTicks = intervalTicks;
            this.isLimited = isLimited;
            this.cyclesLeft = cyclesLeft;
            intervalTickCountdown = 1;
        }
    }
}
