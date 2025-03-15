package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.AbstractSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;

public class SpongeCommandCause extends AbstractSender<CommandCause> {

    public SpongeCommandCause(@NotNull ThimblePlugin plugin, @NotNull CommandCause cause) {
        super(plugin, cause, cause.audience());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }
}
