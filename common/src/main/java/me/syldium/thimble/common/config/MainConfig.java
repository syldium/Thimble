package me.syldium.thimble.common.config;

import me.syldium.thimble.common.player.media.TimedMedia;
import me.syldium.thimble.common.service.DataService;
import me.syldium.thimble.common.util.EnumUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static me.syldium.thimble.common.util.MinecraftVersion.is18;
import static me.syldium.thimble.common.util.MinecraftVersion.isLegacy;

public class MainConfig {

    private final Locale locale;
    private final ConfigNode display;
    private final ConfigNode game;
    private final ConfigNode storage;
    private final List<String> integrations;
    private final Set<String> allowedCommands;
    private final Sound jumpFailedSound, jumpSucceedSound, thimbleSound;
    private final Key countdown;

    public MainConfig(@NotNull ConfigNode config) {
        String languageTag = config.getString("locale", null);
        String[] parts = languageTag == null ? new String[0] : languageTag.split("[_\\.]");
        if (parts.length == 2) {
            this.locale = new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            this.locale = new Locale(parts[0], parts[1], parts[2]);
        } else {
            this.locale = parts.length > 0 ? new Locale(parts[0]) : Locale.getDefault();
        }
        this.display = config.getNode("display");
        this.game = config.getNode("game");
        this.storage = config.getNode("storage");
        this.integrations = config.getStringList("integrations");
        this.allowedCommands = new HashSet<>(config.getStringList("allowed-commands-in-game"));

        ConfigNode sound = config.getOrCreateNode("sound");
        this.jumpFailedSound = sound.getSound("jump-failed", is18() ? "game.player.hurt" : "entity.player.hurt", 0.7f);
        this.jumpSucceedSound = sound.getSound("jump-succeed", isLegacy() ? (is18() ? "note.pling" : "block.note.xylophone") : "block.note_block.xylophone");
        this.thimbleSound = sound.getSound("thimble", is18() ? "random.orb" : "entity.experience_orb.pickup");
        this.countdown = sound.getKey("countdown", isLegacy() ? (is18() ? "note.harp" : "block.note.harp") : "block.note_block.harp");
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

    public @NotNull String getDatabaseFilename(boolean removeExtension) {
        String filename = this.storage.getString("file", "database.db");
        if (removeExtension) {
            int pos = filename.lastIndexOf('.');
            return pos > -1 ? filename.substring(0, pos) : filename;
        }
        return filename;
    }

    public @Nullable String getJdbcUsername() {
        return this.storage.getString("username");
    }

    public @Nullable String getJdbcPassword() {
        return this.storage.getString("password");
    }

    public @NotNull ConfigNode getGameNode() {
        return this.game;
    }

    public @NotNull Sound getJumpFailedSound() {
        return this.jumpFailedSound;
    }

    public @NotNull Sound getJumpSucceedSound() {
        return this.jumpSucceedSound;
    }

    public @NotNull Sound getThimbleSound() {
        return this.thimbleSound;
    }

    public @NotNull Sound getTimerSound(int remainingTicks) {
        return Sound.sound(this.countdown, Sound.Source.PLAYER, 1f, remainingTicks == 0 ? 1.5f : 1.0f);
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

    public Set<String> getAllowedCommands() {
        return this.allowedCommands;
    }
}
