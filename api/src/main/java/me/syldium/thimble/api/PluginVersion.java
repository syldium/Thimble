package me.syldium.thimble.api;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class PluginVersion implements Comparable<PluginVersion> {

    private static final int VERSION_MASK = 0XFF;
    private static final Pattern VERSION_SEPARATOR = Pattern.compile("\\.");
    private static final int SNAPSHOT = 1 << 24;

    private final boolean release;
    private final int[] version;

    PluginVersion(@NotNull String version) {
        int sep = version.indexOf('-');
        String v = sep < 0 ? version : version.substring(0, sep);
        this.release = sep < 0;
        this.version = Arrays.stream(VERSION_SEPARATOR.split(v))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    PluginVersion(int... version) {
        this.release = true;
        this.version = version;
    }

    @Override
    public int compareTo(@NotNull PluginVersion pluginVersion) {
        int length = Math.max(this.version.length, pluginVersion.version.length);
        for (int i = 0; i < length; i++) {
            int diff = Integer.compare(
                    this.version.length > i ? this.version[i] : 0,
                    pluginVersion.version.length > i ? pluginVersion.version[i] : 0
            );
            if (diff != 0) {
                return diff;
            }
        }
        return Boolean.compare(this.release, pluginVersion.release);
    }

    public boolean isRelease() {
        return this.release;
    }

    public int[] version() {
        return this.version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.compareTo((PluginVersion) o) == 0;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (this.version.length > 2) {
            result |= this.version[2] & VERSION_MASK;
        }
        if (this.version.length > 1) {
            result |= (this.version[1] & VERSION_MASK) << 8;
        }
        if (this.version.length > 0) {
            result |= (this.version[0] & VERSION_MASK) << 16;
        }
        return this.release ? result : result | SNAPSHOT;
    }

    @Override
    public String toString() {
        String result = Arrays.stream(this.version)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining("."));
        return this.release ? result : result + "-SNAPSHOT";
    }
}
