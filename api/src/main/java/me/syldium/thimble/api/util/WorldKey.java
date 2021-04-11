package me.syldium.thimble.api.util;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * A key implementation without any constraints, used for world identifiers.
 *
 * <p>The adventure library is relocated, so using the Key type will not work.</p>
 */
public final class WorldKey implements Key, Serializable {

    private static final transient long serialVersionUID = -1681012206529286329L;

    /** A name used to identify a group. */
    private final String namespace;

    /** The key value. */
    private final String value;

    /**
     * Creates a non-constrained key.
     *
     * @param namespace The namespace.
     * @param value The value.
     * @throws IllegalArgumentException If empty.
     */
    public WorldKey(@NotNull String namespace, @NotNull String value) {
        if (namespace.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Empty key");
        }
        this.namespace = namespace;
        this.value = value;
    }

    /**
     * Creates a non-constrained key.
     *
     * @param string The string.
     * @throws IllegalArgumentException If empty.
     */
    public WorldKey(@NotNull String string) {
        int index = string.indexOf(':');
        if (index == 0 || index == string.length() - 1) {
            throw new IllegalArgumentException("Empty key");
        }
        this.namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        this.value = index >= 0 ? string.substring(index + 1) : string;
    }

    @Override
    public @NotNull String namespace() {
        return this.namespace;
    }

    @Override
    public @NotNull String value() {
        return this.value;
    }

    @Override
    public @NotNull String asString() {
        return this.namespace + ':' + this.value;
    }

    @Override
    public @NotNull String toString() {
        return this.asString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Key)) return false;
        Key that = (Key) other;
        return Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value());
    }

    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = (31 * result) + this.value.hashCode();
        return result;
    }
}
