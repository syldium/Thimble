package me.syldium.thimble.mock;

import me.syldium.thimble.common.util.Task;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TickScheduler {

    private final List<TaskMock> tasks = new LinkedList<>();

    public TaskMock schedule(@NotNull Runnable runnable) {
        TaskMock task = new TaskMock(this, runnable);
        this.tasks.add(task);
        return task;
    }

    public void nextTick() {
        this.tasks.forEach(TaskMock::run);
    }

    public void nextTicks(int ticks) {
        for (int i = 0; i < ticks; i++) {
            this.tasks.forEach(TaskMock::run);
        }
    }

    void cancel(@NotNull TaskMock task) {
        this.tasks.remove(task);
    }

    public void cancelAllTasks() {
        this.tasks.clear();
    }

    public void assertScheduled() {
        if (this.tasks.isEmpty()) {
            fail("Nothing is scheduled.");
        }
    }

    static class TaskMock implements Task {

        private final TickScheduler scheduler;
        private final Runnable runnable;

        TaskMock(@NotNull TickScheduler scheduler, @NotNull Runnable runnable) {
            this.scheduler = scheduler;
            this.runnable = runnable;
        }

        public void run() {
            this.runnable.run();
        }

        @Override
        public void cancel() {
            this.scheduler.cancel(this);
        }
    }
}
