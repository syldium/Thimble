package me.syldium.thimble.bukkit.hook;

import me.syldium.thimble.api.bukkit.BukkitGameEndEvent;
import me.syldium.thimble.api.player.ThimblePlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

class VaultIntegration implements Listener {

    private final Economy economy;
    private final Plugin plugin;

    VaultIntegration(@NotNull Plugin plugin) {
        this.economy = plugin.getServer().getServicesManager().load(Economy.class);
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGameEnd(BukkitGameEndEvent event) {
        final double winnerDeposit = this.plugin.getConfig().getDouble("economy.winner-deposit");
        final double participantDeposit = this.plugin.getConfig().getDouble("economy.participant-deposit");
        final boolean multiplyByJumpRatio = this.plugin.getConfig().getBoolean("economy.multiply-by-jump-ratio", true);

        final int maxJumps = multiplyByJumpRatio ? this.maxJumps(event.getGame().players()) : 1;
        for (ThimblePlayer player : event.getGame().players()) {
            if (player.isVanished()) {
                continue;
            }

            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(player.uuid());
            boolean isWinner = player.equals(event.getLatestPlayer());
            double deposit = isWinner ? winnerDeposit : participantDeposit;
            if (multiplyByJumpRatio && !isWinner) {
                deposit *= (double) player.jumpsForGame() / maxJumps;
            }
            this.economy.depositPlayer(offlinePlayer, deposit);
        }
    }

    private int maxJumps(@NotNull Iterable<@NotNull ThimblePlayer> players) {
        int maximum = 0;
        for (ThimblePlayer player : players) {
            if (player.jumpsForGame() > maximum) {
                maximum = player.jumpsForGame();
            }
        }
        return maximum;
    }
}
