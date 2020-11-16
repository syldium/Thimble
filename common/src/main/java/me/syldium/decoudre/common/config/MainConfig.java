package me.syldium.decoudre.common.config;

import me.syldium.decoudre.common.service.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MainConfig {

    @NotNull DataService.Type getDataStorageMethod();

    @NotNull String getJdbcUrl();

    @Nullable String getJdbcUsername();

    @Nullable String getJdbcPassword();
}
