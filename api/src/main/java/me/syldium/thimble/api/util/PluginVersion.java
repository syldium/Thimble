package me.syldium.thimble.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A comparable plugin version.
 */
public class PluginVersion implements Comparable<PluginVersion> {

    private static final int VERSION_MASK = 0XFF;
    private static final Pattern VERSION_SEPARATOR = Pattern.compile("\\.");
    private static final int SNAPSHOT = 1 << 24;

    private final boolean release;
    private final int[] version;

    /**
     * Parses a version string.
     *
     * <p>Possible formats: {@code "v1.24"}, {@code "0.9-SNAPSHOT"}, {@code "1.12"}.</p>
     *
     * @param version The version string.
     * @throws NumberFormatException If anything other than a number is found between the beginning of the string and the first hyphen or otherwise the end.
     */
    public PluginVersion(@NotNull @org.intellij.lang.annotations.Pattern("^v?\\d+(?:\\.\\d+)*(?:-.*)?$") String version) {
        int o = !version.isEmpty() && version.charAt(0) == 'v' ? 1 : 0;
        int sep = version.indexOf('-');
        String v = sep < 0 ? version.substring(o) : version.substring(o, sep);
        this.release = sep < 0;
        this.version = Arrays.stream(VERSION_SEPARATOR.split(v))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Constructs a new plugin version.
     *
     * @param version The version numbers.
     */
    public PluginVersion(int... version) {
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

    /**
     * Checks if this version is a full release.
     *
     * <p>When the version is obtained from a string, if there is no hyphen.</p>
     *
     * @return {@code true} if it's a release.
     */
    public boolean isRelease() {
        return this.release;
    }

    /**
     * Gets the version numbers.
     *
     * @return The version.
     */
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
