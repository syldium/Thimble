package me.syldium.thimble.sponge.util;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.scheduler.Task;

public class SpongeTask implements me.syldium.thimble.common.util.Task {

    private final Task task;

    public SpongeTask(@NotNull Task task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}
