package me.syldium.thimble.mock.player;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.mock.util.BlockDataMock;
import me.syldium.thimble.mock.util.BlockMock;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public class PlayerMock implements Player {

    private final String name;
    private final UUID uniqueId;
    private final Set<String> unsetPermissions = new HashSet<>();
    private final PluginMock plugin;

    private Location location = new Location(UUID.randomUUID(), 0, 0, 0);
    private boolean clearedInventory, spectator, vanished;

    public PlayerMock(@NotNull PluginMock plugin, @NotNull String name, @NotNull UUID uniqueId) {
        this.plugin = plugin;
        this.name = name;
        this.uniqueId = uniqueId;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return !this.unsetPermissions.contains(requireNonNull(permission, "permission"));
    }

    @Override
    public void sendFeedback(@NotNull CommandResult feedback) {

    }

    @Override
    public void sendMessage(@NotNull MessageKey key, Template... templates) {

    }

    @Override
    public void sendActionBar(@NotNull MessageKey key, Template... templates) {

    }

    @Override
    public @NotNull PluginMock getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> teleport(@NotNull Location location) {
        this.location = requireNonNull(location, "location");
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public @NotNull BlockMock getFirstLiquidBlock() {
        return new BlockMock(this.plugin, this.getLocation().asBlockPosition());
    }

    public void restoreInventory() {
        this.clearedInventory = false;
    }

    @Override
    public boolean isInWater() {
        return this.plugin.getBlockData(this.location.asBlockPosition()) == BlockDataMock.WATER;
    }

    @Override
    public void setMiniGameMode() {
        this.spectator = false;
        this.clearedInventory = true;
    }

    @Override
    public void spectate() {
        this.spectator = true;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    @Override
    public boolean isVanished() {
        return this.vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }

    @Override
    public void sendExperienceChange(float percent, int level) {

    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(this.uniqueId);
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uniqueId;
    }
}
