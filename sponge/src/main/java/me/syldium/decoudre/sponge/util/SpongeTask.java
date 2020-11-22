package me.syldium.decoudre.sponge.util;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.scheduler.Task;

public class SpongeTask implements me.syldium.decoudre.common.util.Task {

    private final Task task;

    public SpongeTask(@NotNull Task task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}
