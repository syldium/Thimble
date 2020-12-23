package me.syldium.thimble.bukkit.hook;

import de.myzelyam.api.vanish.VanishAPI;
import me.syldium.thimble.common.service.VanishService;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SuperVanishHook implements VanishService {

    @Override
    public boolean isVanished(@NotNull UUID uuid) {
        return VanishAPI.isInvisibleOffline(uuid);
    }
}
