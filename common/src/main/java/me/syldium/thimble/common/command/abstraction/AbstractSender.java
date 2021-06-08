package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractSender<S> implements Sender, ForwardingAudience.Single {

    private final ThimblePlugin plugin;
    private final Audience audience;
    protected final S handle;
    private Pointers pointers;

    public AbstractSender(@NotNull ThimblePlugin plugin, @NotNull S handle, @NotNull Audience audience) {
        this.plugin = plugin;
        this.handle = handle;
        this.audience = audience;
    }

    @Override
    public @NotNull UUID uuid() {
        return CONSOLE_UUID;
    }

    @Override
    public @NotNull String name() {
        return CONSOLE_NAME;
    }

    public @NotNull S getHandle() {
        return this.handle;
    }

    @Override
    public void sendFeedback(@NotNull CommandResult feedback) {
        if (feedback.getMessageKey() == null) {
            return;
        }
        Component component = this.getMessageService().formatMessage(feedback);
        this.audience.sendMessage(this.getMessageService().prefix().append(component));
    }

    @Override
    public void sendMessage(@NotNull MessageKey key, Template... templates) {
        this.audience.sendMessage(this.getMessageService().formatMessage(key, templates));
    }

    @Override
    public void sendActionBar(@NotNull MessageKey key, Template... templates) {
        this.audience.sendActionBar(this.getMessageService().formatMessage(key, templates));
    }

    @Override
    public @NotNull ThimblePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull Audience audience() {
        return this.audience;
    }

    private @NotNull MessageService getMessageService() {
        return this.plugin.getMessageService();
    }

    @Override
    public @NotNull Pointers pointers() {
        if (this.pointers == null) {
            this.pointers = Pointers.builder()
                    .withStatic(Identity.UUID, this.uuid())
                    .withStatic(Identity.NAME, this.name())
                    .withStatic(PermissionChecker.POINTER, permission -> TriState.byBoolean(this.hasPermission(permission)))
                    .build();
        }
        return this.pointers;
    }

    @Override
    public String toString() {
        return "Sender{handle=" + this.handle + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSender<?> that = (AbstractSender<?>) o;
        return this.handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }
}
