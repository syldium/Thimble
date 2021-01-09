package me.syldium.thimble.common.config;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public interface ConfigNode {

    void setValue(@NotNull @NodePath String path, @NotNull Object value);

    int getInt(@NotNull @NodePath String path, int def);

    double getDouble(@NotNull @NodePath String path, double def);

    float getFloat(@NotNull @NodePath String path, float def);

    boolean getBool(@NotNull @NodePath String path, boolean def);

    @Contract("_, !null -> !null")
    @Nullable String getString(@NotNull @NodePath String path, @Nullable String def);

    default @Nullable String getString(@NotNull @NodePath String path) {
        return this.getString(path, null);
    }

    @NotNull List<@NotNull String> getStringList(@NotNull @NodePath String path);

    @Nullable ConfigNode getNode(@NotNull String... path);

    @NotNull ConfigNode getOrCreateNode(@NotNull String... path);

    @NotNull ConfigNode createNode(@NotNull String... path);

    @NotNull Iterable<@NotNull NodeEntry> getChildren();

    default @Nullable Key getKey(@NotNull @NodePath String path) {
        String raw = this.getString(path, null);
        return raw == null ? null : Key.key(raw);
    }

    default void setValue(@NotNull @NodePath String path, @NotNull Key key) {
        this.setValue(path, key.namespace().equals(Key.MINECRAFT_NAMESPACE) ? key.value() : key.asString());
    }

    default void hydrateLocation(@NotNull @NodePath String path, @NotNull Consumer<@NotNull Location> consumer) {
        ConfigNode node = this.getNode(path);
        if (node == null) return;
        consumer.accept(new Location(
                requireNonNull(node.getKey("world"), "world"),
                node.getDouble("x", 0),
                node.getDouble("y", 0),
                node.getDouble("z", 0),
                node.getFloat("pitch", 0),
                node.getFloat("yaw", 0)
        ));
    }

    default void setLocation(@NotNull @NodePath String path, @Nullable Location location) {
        if (location == null) return;
        ConfigNode node = this.getOrCreateNode(path);
        node.setValue("world", location.worldKey());
        node.setValue("x", location.x());
        node.setValue("y", location.y());
        node.setValue("z", location.z());
        node.setValue("pitch", location.pitch());
        node.setValue("yaw", location.yaw());
    }

    default void hydrateBlockVector(@NotNull @NodePath String path, @NotNull Consumer<@NotNull BlockVector> consumer) {
        ConfigNode node = this.getNode(path);
        if (node == null) return;
        consumer.accept(new BlockVector(
                node.getInt("x", 0),
                node.getInt("y", 0),
                node.getInt("z", 0)
        ));
    }

    default void setBlockVector(@NotNull @NodePath String path, @Nullable BlockVector blockVector) {
        if (blockVector == null) return;
        ConfigNode node = this.getOrCreateNode(path);
        node.setValue("x", blockVector.x());
        node.setValue("y", blockVector.y());
        node.setValue("z", blockVector.z());
    }

    default @Nullable BlockPos getBlockPos(@NotNull @NodePath String path) {
        ConfigNode node = this.getNode(path);
        return node == null ? null : node.asBlockPos();
    }

    default @Nullable BlockPos asBlockPos() {
        return new BlockPos(
                requireNonNull(this.getKey("world"), "world"),
                this.getInt("x", 0),
                this.getInt("y", 0),
                this.getInt("z", 0)
        );
    }

    default void setBlockPos(@NotNull @NodePath String path, @NotNull BlockPos position) {
        ConfigNode node = this.getOrCreateNode(path);
        node.setValue("world", position.worldKey());
        node.setValue("x", position.x());
        node.setValue("y", position.y());
        node.setValue("z", position.z());
    }

    @Contract("_, _, !null -> !null")
    default <E> E getIndexable(@NotNull @NodePath String path, @NotNull Index<String, E> index, @Nullable E def) {
        String raw = this.getString(path);
        if (raw == null) return def;
        E e = index.value(raw);
        return e == null ? def : e;
    }
}
