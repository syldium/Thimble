package me.syldium.decoudre.common.player;

import me.syldium.decoudre.common.command.abstraction.AbstractSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayer<P> extends AbstractSender<P> implements Player {

    public AbstractPlayer(@NotNull P handle, @NotNull Audience audience) {
        super(handle, audience);
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(this.getUuid());
    }
}
