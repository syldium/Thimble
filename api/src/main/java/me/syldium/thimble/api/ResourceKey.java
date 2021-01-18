package me.syldium.thimble.api;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A key implementation without any constraints, used for world identifiers.
 */
final class ResourceKey implements Key {

    private final String namespace;
    private final String value;

    ResourceKey(@NotNull String namespace, @NotNull String value) {
        if (namespace.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Empty key");
        }
        this.namespace = namespace;
        this.value = value;
    }

    ResourceKey(@NotNull String string) {
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
