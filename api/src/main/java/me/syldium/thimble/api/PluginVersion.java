package me.syldium.thimble.api;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

class PluginVersion implements Comparable<PluginVersion> {

    private final boolean release;
    private final int[] version;

    PluginVersion(@NotNull String versionString) {
        String[] parts = versionString.split("-");
        this.release = parts.length == 1;
        this.version = Arrays.stream(parts[0].split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    PluginVersion(int major, int minor, int patch) {
        this.release = true;
        this.version = new int[]{major, minor, patch};
    }

    @Override
    public int compareTo(@NotNull PluginVersion pluginVersion) {
        int length = Math.max(this.version.length, pluginVersion.version.length);
        for(int i = 0; i < length; i++) {
            int diff = Integer.compare(
                    this.version.length > i ? this.version[i] : 0,
                    pluginVersion.version.length > i ? pluginVersion.version[i] : 0
            );
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }

    public boolean isRelease() {
        return this.release;
    }
}
