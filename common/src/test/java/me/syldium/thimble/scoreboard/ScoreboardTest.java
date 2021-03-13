package me.syldium.thimble.scoreboard;

import me.syldium.thimble.common.player.media.Scoreboard;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ScoreboardTest {

    private final List<Component> text = Arrays.stream("A paper plane flies through the air.".split(" ")).map(Component::text).collect(Collectors.toList());
    private final Component things = text("things");
    private final Component title = text("adventure is cool");

    @Test
    public void testInitWithoutParams() {
        Scoreboard scoreboard = Scoreboard.scoreboard();
        assertEquals(Component.empty(), scoreboard.title());
        assertEquals(Collections.emptyList(), scoreboard.lines());
    }

    @Test
    public void testInit() {
        List<Component> exceptedLines = this.someLines(3);
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, exceptedLines);
        assertEquals(this.title, scoreboard.title());
        assertEquals(exceptedLines, scoreboard.lines());
    }

    @Test @SuppressWarnings("ConstantConditions")
    public void testUnmodifiableLines() {
        List<Component> lines = this.someLines(5);
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, lines);
        assertThrows(UnsupportedOperationException.class, () -> scoreboard.lines().clear());
        assertThrows(UnsupportedOperationException.class, () -> scoreboard.lines().add(space()));
        lines.add(space());
        assertEquals(5, scoreboard.lines().size());
    }

    @Test
    public void testInsertLine() {
        ChangeObserver observer = new ChangeObserver();
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, this.someLines(2)).addListener(observer);
        scoreboard.insertLine(this.things);
        assertEquals(1, observer.add);
        observer.assertAllZeroExceptOne();
        assertEquals(3, scoreboard.size());
        assertEquals(this.things, scoreboard.lines().get(2));
    }

    @Test
    public void testUpdateLine() {
        List<Component> lines = this.someLines(6);
        ChangeObserver observer = new ChangeObserver();
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, lines).addListener(observer);

        scoreboard.updateLine(5, this.things);
        assertEquals(1, observer.update);
        observer.assertAllZeroExceptOne();
        assertEquals(lines.size(), scoreboard.size());
        assertEquals(lines.subList(0, 4), scoreboard.lines().subList(0, 4));
        assertEquals(this.things, scoreboard.lines().get(5));
    }

    @Test
    public void testInsertWithLines() {
        List<Component> lines = this.someLines(3);
        List<Component> toAdd = List.of(this.things, this.text.get(this.text.size() - 1));
        ChangeObserver observer = new ChangeObserver() {
            @Override
            public void lineAdded(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
                if (!toAdd.get(this.add).equals(content)) fail();
                if ((this.add + 3) != line) fail();
                super.lineAdded(scoreboard, content, line);
            }
        };
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, lines).addListener(observer);

        lines.addAll(toAdd);
        scoreboard.lines(lines);
        assertEquals(lines, scoreboard.lines());
        assertEquals(2, observer.add);
        observer.assertAllZeroExceptOne();
    }

    @Test
    public void testUpdateWithLines() {
        List<Component> lines = this.someLines(6);
        ChangeObserver observer = new ChangeObserver() {
            @Override
            public void lineUpdated(@NotNull Scoreboard scoreboard, @NotNull Component oldLine, @NotNull Component newLine, int line) {
                super.lineUpdated(scoreboard, oldLine, newLine, line);
                if (line != 4 || !ScoreboardTest.this.things.equals(newLine)) fail();
            }
        };
        Scoreboard scoreboard = Scoreboard.scoreboard(this.title, lines).addListener(observer);

        lines.set(4, this.things);
        scoreboard.lines(lines);
        assertEquals(lines, scoreboard.lines());
        assertEquals(1, observer.update);
        observer.assertAllZeroExceptOne();
    }

    private @NotNull List<Component> someLines(int n) {
        List<Component> lines = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            lines.add(this.text.get(i % this.text.size()));
        }
        return lines;
    }
}
