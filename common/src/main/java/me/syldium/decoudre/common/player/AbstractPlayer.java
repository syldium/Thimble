package me.syldium.decoudre.common.player;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.AbstractSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayer<P> extends AbstractSender<P> implements Player {

    public AbstractPlayer(@NotNull DeCoudrePlugin plugin, @NotNull P handle, @NotNull Audience audience) {
        super(plugin, handle, audience);
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(this.uuid());
    }
}
