package me.syldium.decoudre.bukkit.config;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import me.syldium.decoudre.common.service.DataService;
import me.syldium.decoudre.common.util.EnumUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class BukkitMainConfig extends FileConfig implements MainConfig {

    public BukkitMainConfig(@NotNull DeBukkitPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    @Override
    public @NotNull Locale getLocale() {
        return new Locale(this.getString("locale", "en"));
    }

    @Override
    public @NotNull DataService.Type getDataStorageMethod() {
        return EnumUtil.valueOf(DataService.Type.class, this.getString("storage-method", "sqlite"), DataService.Type.SQLITE);
    }

    @Override
    public @NotNull String getJdbcUrl() {
        return this.getString("sql.jdbcUri", "jdbc:mysql://localhost:3306/decoudre");
    }

    @Override
    public @Nullable String getJdbcUsername() {
        return this.getString("sql.username");
    }

    @Override
    public @Nullable String getJdbcPassword() {
        return this.getString("sql.password");
    }

    @Override
    public @NotNull List<String> getEnabledIntegrations() {
        return this.configuration.getStringList("integrations");
    }

    @Override
    public boolean doesTeleportAtEnd() {
        return this.configuration.getBoolean("game.teleport-at-end", true);
    }

    @Override
    public int getCountdownTime() {
        return this.configuration.getInt("game.countdown-time", 30);
    }

    @Override
    public int getJumpTime() {
        return this.configuration.getInt("game.jump-time", 15);
    }

    @Override
    public @Nullable String getRawDisplayProperty(@NotNull String audienceName, @NotNull String propertyKey) {
        return this.configuration.getString("display." + audienceName + "." + propertyKey);
    }

    @Override
    public double getWinnerDeposit() {
        return this.configuration.getDouble("vault.winner-deposit", 5.0D);
    }
}
