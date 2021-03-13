package me.syldium.thimble.scoreboard;

import me.syldium.thimble.common.player.media.Scoreboard;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.fail;

class ChangeObserver implements Scoreboard.Listener {

    int title, add, update, remove;

    public void titleChanged(@NotNull Scoreboard scoreboard, @NotNull Component oldTitle, @NotNull Component newTitle) {
        this.title++;
    }

    public void lineAdded(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
        this.add++;
    }

    public void lineUpdated(@NotNull Scoreboard scoreboard, @NotNull Component oldLine, @NotNull Component newLine, int line) {
        this.update++;
    }

    public void lineRemoved(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
        this.remove++;
    }

    public void assertAllZeroExceptOne() {
        boolean change = false;
        for (int n : new int[]{this.title, this.add, this.update, this.remove}) {
            if (n < 1) continue;
            if (change) {
                fail();
            } else {
                change = true;
            }
        }
        if (!change) {
            fail();
        }
    }
}
