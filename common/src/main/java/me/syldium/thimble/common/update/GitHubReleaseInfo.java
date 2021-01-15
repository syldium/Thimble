package me.syldium.thimble.common.update;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GitHubReleaseInfo implements ComponentLike {

    @SerializedName("tag_name")
    String tagName;

    boolean prerelease;

    GitHubAssetInfo platformAsset;

    List<GitHubAssetInfo> assets;

    @Contract(pure = true)
    public @Nullable GitHubAssetInfo latestPlatformAsset() {
        return this.platformAsset;
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.text(this.tagName);
    }
}
