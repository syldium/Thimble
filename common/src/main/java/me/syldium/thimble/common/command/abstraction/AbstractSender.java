package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractSender<S> implements Sender {

    private final ThimblePlugin plugin;
    private final Audience audience;
    protected final S handle;

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
    public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
        this.audience.sendMessage(source, message, type);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.audience.sendMessage(source, message, type);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        this.audience.sendActionBar(message);
    }

    @Override
    public void showTitle(@NotNull Title title) {
        this.audience.showTitle(title);
    }

    @Override
    public void clearTitle() {
        this.audience.clearTitle();
    }

    @Override
    public void resetTitle() {
        this.audience.resetTitle();
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        this.audience.showBossBar(bar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        this.audience.hideBossBar(bar);
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        this.audience.playSound(sound);
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        this.audience.playSound(sound, x, y, z);
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        this.audience.stopSound(stop);
    }

    @Override
    public void openBook(@NotNull Book book) {
        this.audience.openBook(book);
    }

    private @NotNull MessageService getMessageService() {
        return this.plugin.getMessageService();
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
