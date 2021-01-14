package me.syldium.thimble.bukkit.hook;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.service.GameService;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <p><a href="https://github.com/lucko/LuckPerms">LuckPerms</a></p>
 *
 * <p>Adds context "thimble:arena".</p>
 */
class LuckPermsContext implements ContextCalculator<Player> {

    private static final String KEY = "thimble:arena";

    private final GameService gameService;

    LuckPermsContext(@NotNull ServicesManager servicesManager, @NotNull GameService gameService) {
        this.gameService = gameService;
        // noinspection ConstantConditions
        servicesManager.load(LuckPerms.class).getContextManager().registerCalculator(this);
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        Optional<ThimbleGame> gameOpt = this.gameService.playerGame(target.getUniqueId());
        // noinspection OptionalIsPresent
        if (gameOpt.isPresent()) {
            consumer.accept(KEY, gameOpt.get().arena().name());
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        for (ThimbleArena arena : this.gameService.arenas()) {
            builder.add(KEY, arena.name());
        }
        return builder.build();
    }
}
