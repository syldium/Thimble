package me.syldium.decoudre.bukkit.config;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import me.syldium.decoudre.common.service.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
        String method = this.getString("storage-method", "sqlite");
        try {
            return DataService.Type.valueOf(method.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return DataService.Type.SQLITE;
        }
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
}
