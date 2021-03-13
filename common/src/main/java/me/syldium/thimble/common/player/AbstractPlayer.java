package me.syldium.thimble.common.player;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.AbstractSender;
import me.syldium.thimble.common.player.media.Scoreboard;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayer<P> extends AbstractSender<P> implements Player {

    public AbstractPlayer(@NotNull ThimblePlugin plugin, @NotNull P handle, @NotNull Audience audience) {
        super(plugin, handle, audience);
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard) {
        this.getPlugin().getPlayerAdapter().setScoreboard(scoreboard, this);
    }

    @Override
    public void hideScoreboard(@NotNull Scoreboard scoreboard) {
        this.getPlugin().getPlayerAdapter().hideScoreboard(scoreboard, this);
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(this.uuid());
    }

    @Override
    public String toString() {
        return "Player{handle=" + this.handle + '}';
    }
}
