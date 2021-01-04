package me.syldium.thimble.common.config;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
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

    default @Nullable UUID getUniqueId(@NotNull @NodePath String path) {
        String raw = this.getString(path, null);
        return raw == null ? null : UUID.fromString(raw);
    }

    default void setUniqueId(@NotNull @NodePath String path, @NotNull UUID uuid) {
        this.setValue(path, uuid.toString());
    }

    default void hydrateLocation(@NotNull @NodePath String path, @NotNull Consumer<@NotNull Location> consumer) {
        ConfigNode node = this.getNode(path);
        if (node == null) return;
        consumer.accept(new Location(
                requireNonNull(node.getUniqueId("world"), "world"),
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
        node.setValue("world", location.getWorldUUID().toString());
        node.setValue("x", location.getX());
        node.setValue("y", location.getY());
        node.setValue("z", location.getZ());
        node.setValue("pitch", location.getPitch());
        node.setValue("yaw", location.getYaw());
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
        node.setValue("x", blockVector.getX());
        node.setValue("y", blockVector.getY());
        node.setValue("z", blockVector.getZ());
    }

    default @Nullable BlockPos getBlockPos(@NotNull @NodePath String path) {
        ConfigNode node = this.getNode(path);
        return node == null ? null : node.asBlockPos();
    }

    default @Nullable BlockPos asBlockPos() {
        return new BlockPos(
                requireNonNull(this.getUniqueId("world"), "world"),
                this.getInt("x", 0),
                this.getInt("y", 0),
                this.getInt("z", 0)
        );
    }

    default void setBlockPos(@NotNull @NodePath String path, @NotNull BlockPos position) {
        ConfigNode node = this.getOrCreateNode(path);
        node.setUniqueId("world", position.getWorldUUID());
        node.setValue("x", position.getX());
        node.setValue("y", position.getY());
        node.setValue("z", position.getZ());
    }

    @Contract("_, _, !null -> !null")
    default <E> E getIndexable(@NotNull @NodePath String path, @NotNull Index<String, E> index, @Nullable E def) {
        String raw = this.getString(path);
        if (raw == null) return def;
        E e = index.value(raw);
        return e == null ? def : e;
    }
}
