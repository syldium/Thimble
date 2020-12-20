package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class BukkitConfigNode implements ConfigNode {

    protected final ConfigurationSection section;

    BukkitConfigNode(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    private @NotNull String getPath(@NotNull String... path) {
        return String.join(".", path);
    }

    @Override
    public final void setValue(@NotNull String path, @NotNull Object value) {
        this.section.set(path, value);
    }

    @Override
    public final int getInt(@NotNull String path, int def) {
        return this.section.getInt(path, def);
    }

    @Override
    public final double getDouble(@NotNull String path, double def) {
        return this.section.getDouble(path, def);
    }

    @Override
    public final float getFloat(@NotNull String path, float def) {
        return (float) this.section.getDouble(path, def);
    }

    @Override
    public final boolean getBool(@NotNull String path, boolean def) {
        return this.section.getBoolean(path, def);
    }

    @Override
    public final @Nullable String getString(@NotNull String path, @Nullable String def) {
        return this.section.getString(path, def);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        return this.section.getStringList(path);
    }

    @Override
    public final @Nullable ConfigNode getNode(@NotNull String... path) {
        if (path.length < 1) {
            return this;
        }
        ConfigurationSection section = this.section.getConfigurationSection(this.getPath(path));
        return section == null ? null : new BukkitConfigNode(section);
    }

    @Override
    public final @NotNull ConfigNode getOrCreateNode(@NotNull String... path) {
        if (path.length < 1) {
            return this;
        }
        String bukkitPath = this.getPath(path);
        ConfigurationSection section = this.section.getConfigurationSection(bukkitPath);
        if (section == null) {
            return new BukkitConfigNode(this.section.createSection(bukkitPath));
        }
        return new BukkitConfigNode(section);
    }

    @Override
    public final @NotNull ConfigNode createNode(@NotNull String... path) {
        return new BukkitConfigNode(this.section.createSection(this.getPath(path)));
    }

    @Override
    public final @NotNull Iterable<NodeEntry> getChildren() {
        return () -> new BukkitNodeIterator(this.section);
    }

    @Override
    public String toString() {
        return this.section.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitConfigNode that = (BukkitConfigNode) o;
        return this.section.equals(that.section);
    }

    @Override
    public int hashCode() {
        return this.section.hashCode();
    }
}
