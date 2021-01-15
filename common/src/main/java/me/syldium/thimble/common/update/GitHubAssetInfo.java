package me.syldium.thimble.common.update;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class GitHubAssetInfo implements ComponentLike {

    String url;

    int id;

    String name;

    @SerializedName("browser_download_url")
    String browserDownloadUrl;

    public @NotNull String name() {
        return this.name;
    }

    public @NotNull String browserDownloadUrl() {
        return this.browserDownloadUrl;
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.text(this.name).clickEvent(ClickEvent.openUrl(this.browserDownloadUrl));
    }
}
