package me.syldium.thimble.sponge.util;

import me.syldium.thimble.common.util.Task;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.scheduler.ScheduledTask;

public class SpongeTask implements Task {

    private final ScheduledTask task;

    public SpongeTask(@NotNull ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}
