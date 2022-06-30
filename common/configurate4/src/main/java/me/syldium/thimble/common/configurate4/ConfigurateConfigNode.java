package me.syldium.thimble.common.configurate4;

import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collections;
import java.util.List;

class ConfigurateConfigNode implements ConfigNode {

    protected final ConfigurationNode parent;

    ConfigurateConfigNode(@NotNull ConfigurationNode node) {
        this.parent = node;
    }

    private @NotNull ConfigurationNode getConfigurateNode(@NotNull String... path) {
        return this.parent.node((Object[]) path);
    }

    @Override
    public void setValue(@NotNull String path, @NotNull Object value) {
        try {
            this.getConfigurateNode(path).set(value);
        } catch (SerializationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return this.getConfigurateNode(path).getInt(def);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return this.getConfigurateNode(path).getDouble(def);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        return this.getConfigurateNode(path).getFloat(def);
    }

    @Override
    public boolean getBool(@NotNull String path, boolean def) {
        return this.getConfigurateNode(path).getBoolean(def);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        if (def == null) {
            return this.getConfigurateNode(path).getString();
        }
        return this.getConfigurateNode(path).getString(def);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        try {
            List<String> lst = this.getConfigurateNode(path).getList(String.class);
            return lst == null ? Collections.emptyList() : lst;
        } catch (SerializationException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public @NotNull ConfigNode getNode(@NotNull String... path) {
        return this.getOrCreateNode(path);
    }

    @Override
    public @NotNull ConfigNode getOrCreateNode(@NotNull String... path) {
        return new ConfigurateConfigNode(this.getConfigurateNode(path));
    }

    @Override
    public @NotNull ConfigNode createNode(@NotNull String... path) {
        try {
            return new ConfigurateConfigNode(this.getConfigurateNode(path).set(null));
        } catch (SerializationException ex) {
            return new ConfigurateConfigNode(this.getConfigurateNode(path));
        }
    }

    @Override
    public @NotNull Iterable<NodeEntry> getChildren() {
        return () -> new ConfigurateNodeIterator(this.parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurateConfigNode that = (ConfigurateConfigNode) o;
        return this.parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        return this.parent.hashCode();
    }

    @Override
    public String toString() {
        return this.parent.key().toString();
    }
}
