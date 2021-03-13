package me.syldium.thimble.common.player.media;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

final class ScoreboardImpl implements Scoreboard {

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    private Component title;
    private List<Component> lines;

    ScoreboardImpl(@NotNull Component title, @NotNull List<@NotNull Component> lines) {
        this.title = title;
        this.lines = lines;
    }

    @Override
    public @NotNull Component title() {
        return this.title;
    }

    @Override
    public @NotNull Scoreboard title(@NotNull Component newTitle) {
        requireNonNull(newTitle, "title");
        if (!Objects.equals(this.title, newTitle)) {
            for (Listener listener : this.listeners) {
                listener.titleChanged(this, this.title, newTitle);
            }
            this.title = newTitle;
        }
        return this;
    }

    @Override
    public @NotNull List<@NotNull Component> lines() {
        return Collections.unmodifiableList(this.lines);
    }

    @Override
    public @NotNull Scoreboard lines(@NotNull List<Component> newLines) {
        requireNonNull(newLines, "lines");
        List<Component> oldLines = this.lines;
        this.lines = new ArrayList<>(newLines);

        if (this.listeners.isEmpty()) {
            return this;
        }

        int max = Math.max(oldLines.size(), newLines.size());
        for (int i = 0; i < max; i++) {
            if (i >= oldLines.size()) {
                for (Listener listener : this.listeners) {
                    listener.lineAdded(this, newLines.get(i), i);
                }
            } else if (i >= newLines.size()) {
                for (Listener listener : this.listeners) {
                    listener.lineRemoved(this, oldLines.get(i), i);
                }
            } else {
                Component oldLine = oldLines.get(i);
                Component newLine = newLines.get(i);
                if (!Objects.equals(oldLine, newLine)) {
                    for (Listener listener : this.listeners) {
                        listener.lineUpdated(this, oldLine, newLine, i);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public @NotNull Component line(int line) {
        return this.lines.get(line);
    }

    @Override
    public int size() {
        return this.lines.size();
    }

    @Override
    public @NotNull Scoreboard insertLine(@NotNull Component component) {
        requireNonNull(component, "line");
        this.lines.add(component);
        for (Listener listener : this.listeners) {
            listener.lineAdded(this, component, this.lines.size());
        }
        return this;
    }

    @Override
    public @NotNull Scoreboard updateLine(int line, @NotNull Component newLine) {
        requireNonNull(newLine, "line");
        Component oldLine = this.lines.set(line, newLine);
        if (!Objects.equals(oldLine, newLine)) {
            for (Listener listener : this.listeners) {
                listener.lineUpdated(this, oldLine, newLine, line);
            }
        }
        return this;
    }

    @Override
    public @NotNull Scoreboard removeLine(int line) {
        Component oldLine = this.lines.remove(line);
        for (Listener listener : this.listeners) {
            listener.lineRemoved(this, oldLine, line);
        }
        return this;
    }

    @Override
    public @NotNull Scoreboard addListener(@NotNull Listener listener) {
        this.listeners.add(requireNonNull(listener, "listener"));
        return this;
    }
}
