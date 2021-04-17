package me.syldium.thimble.mock;

import me.syldium.thimble.common.util.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TickScheduler {

    private final List<TaskMock> tasks = new ArrayList<>();
    private @Nullable Iterator<TaskMock> iterator;

    public TaskMock schedule(@NotNull Runnable runnable) {
        TaskMock task = new TaskMock(this, runnable);
        this.tasks.add(task);
        return task;
    }

    public void nextTick() {
        this.iterator = this.tasks.iterator();
        while (this.iterator.hasNext()) {
            TaskMock task = this.iterator.next();
            task.runnable.run();
        }
        this.iterator = null;
    }

    public void nextTicks(int ticks) {
        for (int i = 0; i < ticks; i++) {
            this.nextTick();
        }
    }

    public void nextSecond() {
        this.nextTicks(Task.GAME_TICKS_PER_SECOND);
    }

    void cancel(@NotNull TaskMock task) {
        if (this.iterator == null) {
            this.tasks.remove(task);
        } else {
            this.iterator.remove();
        }
    }

    public void cancelAllTasks() {
        this.tasks.clear();
    }

    public void assertScheduled() {
        if (this.tasks.isEmpty()) {
            fail("Nothing is scheduled.");
        }
    }

    public void assertNothingScheduled() {
        if (!this.tasks.isEmpty()) {
            fail("Something is scheduled.");
        }
    }

    static class TaskMock implements Task {

        private final TickScheduler scheduler;
        private final Runnable runnable;

        TaskMock(@NotNull TickScheduler scheduler, @NotNull Runnable runnable) {
            this.scheduler = scheduler;
            this.runnable = runnable;
        }

        @Override
        public void cancel() {
            this.scheduler.cancel(this);
        }
    }
}
