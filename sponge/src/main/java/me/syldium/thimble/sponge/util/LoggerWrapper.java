package me.syldium.thimble.sponge.util;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class LoggerWrapper extends java.util.logging.Logger {
    private final Logger logger;

    public LoggerWrapper(final Logger logger) {
        super("logger", null);
        this.logger = logger;
    }

    @Override
    public void log(final LogRecord record) {
        this.log(record.getLevel(), record.getMessage(), record.getThrown());
    }

    @Override
    public void log(@NotNull Level level, @NotNull String msg, @Nullable Throwable throwable) {
        if (level == Level.FINE)
            this.logger.debug(msg, throwable);
        else if (level == Level.WARNING)
            this.logger.warn(msg, throwable);
        else if (level == Level.SEVERE)
            this.logger.error(msg, throwable);
        else if (level == Level.INFO)
            this.logger.info(msg, throwable);
        else
            this.logger.trace(msg, throwable);
    }
}
