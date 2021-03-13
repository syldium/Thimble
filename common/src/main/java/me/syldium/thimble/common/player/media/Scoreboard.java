package me.syldium.thimble.common.player.media;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an in-game scoreboard which can be shown to the client.
 *
 * <p>This interface focuses on displaying ordered lines, by defining an arbitrary score.</p>
 */
public interface Scoreboard {

    /**
     * The maximum number of lines before the last lines are truncated.
     */
    int MAX_LINES = 15;

    /**
     * Creates a scoreboard.
     *
     * @return A new scoreboard.
     */
    @Contract(" -> new")
    static @NotNull Scoreboard scoreboard() {
        return scoreboard(Component.empty(), Collections.emptyList());
    }

    /**
     * Creates a scoreboard.
     *
     * @param title The title.
     * @return A new scoreboard.
     */
    @Contract("_ -> new")
    static @NotNull Scoreboard scoreboard(@NotNull Component title) {
        return scoreboard(title, Collections.emptyList());
    }

    /**
     * Creates a scoreboard.
     *
     * @param title The title.
     * @param lines The lines to display, from top to bottom.
     * @return A new scoreboard.
     */
    @Contract("_, _ -> new")
    static @NotNull Scoreboard scoreboard(@NotNull Component title, @NotNull List<Component> lines) {
        return new ScoreboardImpl(title, new ArrayList<>(lines));
    }

    /**
     * Gets the scoreboard title.
     *
     * @return The scoreboard title.
     */
    @NotNull Component title();

    /**
     * Updates the scoreboard title.
     *
     * @param title The new scoreboard title.
     */
    @Contract("_ -> this")
    @NotNull Scoreboard title(@NotNull Component title);

    /**
     * Gets the scoreboard lines.
     *
     * @return The scoreboard lines.
     */
    @NotNull @UnmodifiableView List<@NotNull Component> lines();

    /**
     * Updates all the scoreboard lines.
     *
     * @param lines The new lines.
     */
    @Contract("_ -> this")
    default @NotNull Scoreboard lines(@NotNull Component... lines) {
        return this.lines(Arrays.asList(lines));
    }

    /**
     * Updates all the scoreboard lines.
     *
     * @param lines The new lines.
     */
    @Contract("_ -> this")
    @NotNull Scoreboard lines(@NotNull List<Component> lines);

    /**
     * Get the specified scoreboard line.
     *
     * @param line The line index.
     * @throws IndexOutOfBoundsException if the line is higher than {@link #size()}
     */
    @NotNull Component line(int line);

    /**
     * Get the specified scoreboard line from the score.
     *
     * @param score The score.
     */
    default @NotNull Component lineByScore(int score) {
        return this.line(this.reverseIndex(score));
    }

    /**
     * Gets the number of lines.
     *
     * @return The size.
     */
    int size();

    /**
     * Converts the index of a line into a score and vice versa.
     *
     * @param index The line index/score.
     * @return A score/index.
     */
    default int reverseIndex(int index) {
        return this.size() - index - 1;
    }

    @Contract(value = "_ -> this")
    @NotNull Scoreboard insertLine(@NotNull Component component);

    @Contract(value = "_, _ -> this")
    @NotNull Scoreboard updateLine(int line, @NotNull Component component);

    @Contract(value = "_ -> this")
    @NotNull Scoreboard removeLine(int line);

    /**
     * Adds a listener.
     *
     * @param listener A listener.
     * @return The scoreboard.
     */
    @Contract(value = "_ -> this")
    @NotNull Scoreboard addListener(@NotNull Listener listener);

    /**
     * A listener for changes that happen on a {@link Scoreboard}.
     *
     * <p>These methods are always called after the modification has been done.</p>
     */
    interface Listener {

        /**
         * Scoreboard title changed.
         *
         * @param scoreboard The scoreboard.
         * @param oldTitle The old title.
         * @param newTitle The new title.
         */
        void titleChanged(@NotNull Scoreboard scoreboard, @NotNull Component oldTitle, @NotNull Component newTitle);

        /**
         * A new line has been added.
         *
         * @param scoreboard The scoreboard.
         * @param content The content of the new line.
         * @param line The index of this line.
         */
        void lineAdded(@NotNull Scoreboard scoreboard, @NotNull Component content, int line);

        /**
         * A line has been updated.
         *
         * @param scoreboard The scoreboard.
         * @param oldLine The content of the old line.
         * @param newLine The content of the new line.
         * @param line The index of this line.
         */
        void lineUpdated(@NotNull Scoreboard scoreboard, @NotNull Component oldLine, @NotNull Component newLine, int line);

        void lineRemoved(@NotNull Scoreboard scoreboard, @NotNull Component content, int line);
    }

    /**
     * A scoreboard audience.
     */
    interface Holder {

        /**
         * Shows a scoreboard.
         *
         * @param scoreboard A scoreboard.
         */
        void setScoreboard(@NotNull Scoreboard scoreboard);

        /**
         * Hides the scoreboard.
         *
         * @param scoreboard A scoreboard.
         */
        void hideScoreboard(@NotNull Scoreboard scoreboard);
    }
}
