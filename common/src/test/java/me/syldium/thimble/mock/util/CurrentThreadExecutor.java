package me.syldium.thimble.mock.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class CurrentThreadExecutor implements Executor {

    @Override
    public void execute(@NotNull Runnable runnable) {
        runnable.run();
    }
}
