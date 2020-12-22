package me.syldium.thimble.common.config;

import me.syldium.thimble.common.player.media.TimedMedia;
import me.syldium.thimble.common.service.DataService;
import me.syldium.thimble.common.util.EnumUtil;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class MainConfig {

    private final Locale locale;
    private final ConfigNode display;
    private final ConfigNode game;
    private final ConfigNode storage;
    private final List<String> integrations;

    public MainConfig(@NotNull ConfigNode config) {
        String[] parts = config.getString("locale", "en").split("[_\\.]");
        if (parts.length == 2) {
            this.locale = new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            this.locale = new Locale(parts[0], parts[1], parts[2]);
        } else {
            this.locale = new Locale(parts.length > 0 ? parts[0]: "en");
        }
        this.display = config.getNode("display");
        this.game = config.getNode("game");
        this.storage = config.getNode("storage");
        this.integrations = config.getStringList("integrations");
    }

    public @NotNull DataService.Type getDataStorageMethod() {
        return EnumUtil.valueOf(DataService.Type.class, this.storage.getString("storage-method", "sqlite"), DataService.Type.SQLITE);
    }

    public @NotNull String getJdbcUrl() {
        return String.format(
                "jdbc:%s://%s:%d/%s",
                this.getDataStorageMethod().getDriverName(),
                this.storage.getString("host"),
                this.storage.getInt("port", 3306),
                this.storage.getString("database", "thimble")
        );
    }

    public @NotNull String getSqliteFilename() {
        return this.storage.getString("file", "database.db");
    }

    public @Nullable String getJdbcUsername() {
        return this.storage.getString("username");
    }

    public @Nullable String getJdbcPassword() {
        return this.storage.getString("password");
    }

    public boolean doesTeleportAtEnd() {
        return this.game.getBool("teleport-at-end", true);
    }

    public boolean doesCountFailsInConcurrent() {
        return this.game.getBool("count-fails-concurrent", true);
    }

    public boolean doesSaveStatesInFile() {
        return this.game.getBool("save-states-in-file", true);
    }

    public int getGameInt(@NotNull @NodePath String path, int def) {
        return this.game.getInt(path, def);
    }

    public @NotNull TimedMedia.Type getDisplayType(@NotNull String audienceName) {
        return this.display.getOrCreateNode(audienceName).getIndexable("type", TimedMedia.Type.NAMES, TimedMedia.Type.BOSSBAR);
    }

    public @NotNull BossBar.Overlay getBossBarOverlay(@NotNull String audienceName) {
        return this.display.getOrCreateNode(audienceName).getIndexable("bossbar-overlay", BossBar.Overlay.NAMES, BossBar.Overlay.PROGRESS);
    }

    public @NotNull BossBar.Color getBossBarColor(@NotNull String audienceName) {
        return this.display.getOrCreateNode(audienceName).getIndexable("bossbar-color", BossBar.Color.NAMES, "global".equals(audienceName) ? BossBar.Color.YELLOW : BossBar.Color.RED);
    }

    public @NotNull Locale getLocale() {
        return this.locale;
    }

    public @NotNull List<@NotNull String> getEnabledIntegrations() {
        return this.integrations;
    }
}
