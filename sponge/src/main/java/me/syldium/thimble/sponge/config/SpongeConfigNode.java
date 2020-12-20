package me.syldium.thimble.sponge.config;

import com.google.common.reflect.TypeToken;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

class SpongeConfigNode implements ConfigNode {

    protected final ConfigurationNode parent;

    SpongeConfigNode(@NotNull ConfigurationNode node) {
        this.parent = node;
    }

    private @NotNull ConfigurationNode getSpongeNode(@NotNull String... path) {
        return this.parent.getNode((Object[]) path);
    }

    @Override
    public void setValue(@NotNull String path, @NotNull Object value) {
        this.getSpongeNode(path).setValue(value);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return this.getSpongeNode(path).getInt(def);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return this.getSpongeNode(path).getDouble(def);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        return this.getSpongeNode(path).getFloat(def);
    }

    @Override
    public boolean getBool(@NotNull String path, boolean def) {
        return this.getSpongeNode(path).getBoolean(def);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return this.getSpongeNode(path).getString(def);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        try {
            return this.getSpongeNode(path).getList(TypeToken.of(String.class));
        } catch (ObjectMappingException ex) {
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
        return new SpongeConfigNode(this.getSpongeNode(path));
    }

    @Override
    public @NotNull ConfigNode createNode(@NotNull String... path) {
        return new SpongeConfigNode(this.getSpongeNode(path).setValue(null));
    }

    @Override
    public @NotNull Iterable<NodeEntry> getChildren() {
        return () -> new SpongeNodeIterator(this.parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongeConfigNode that = (SpongeConfigNode) o;
        return this.parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        return this.parent.hashCode();
    }

    @Override
    public String toString() {
        return this.parent.getKey() + "->" + this.parent.getValue();
    }
}
