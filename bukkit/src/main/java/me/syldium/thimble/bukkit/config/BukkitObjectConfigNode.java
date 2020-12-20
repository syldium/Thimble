package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.bukkit.ThBootstrap;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

class BukkitObjectConfigNode implements ConfigNode {

    private final String value;

    BukkitObjectConfigNode(@Nullable String key, @NotNull Object value) {
        this.value = String.valueOf(value);
        if (key != null) {
            ThBootstrap.getPlugin(ThBootstrap.class).getLogger()
                    .severe("Found a literal node in an unexpected location (" + key + "), your configuration file may be corrupted or outdated.");
        }
    }

    @Override
    public void setValue(@NotNull String path, @NotNull Object value) {
        // ...
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        try {
            return Integer.parseInt(this.value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        try {
            return Double.parseDouble(this.value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        try {
            return Float.parseFloat(this.value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    @Override
    public boolean getBool(@NotNull String path, boolean def) {
        try {
            return Boolean.parseBoolean(this.value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return this.value;
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable ConfigNode getNode(@NotNull String... path) {
        return null;
    }

    @Override
    public @NotNull ConfigNode getOrCreateNode(@NotNull String... path) {
        return new BukkitObjectConfigNode(null, this.value);
    }

    @Override
    public @NotNull ConfigNode createNode(@NotNull String... path) {
        return new BukkitObjectConfigNode(null, this.value);
    }

    @Override
    public @NotNull Iterable<@NotNull NodeEntry> getChildren() {
        return Collections::emptyIterator;
    }
}
