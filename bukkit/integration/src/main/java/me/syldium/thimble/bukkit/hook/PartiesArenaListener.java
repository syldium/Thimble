package me.syldium.thimble.bukkit.hook;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.Party;
import me.syldium.thimble.api.bukkit.BukkitPlayerJoinArenaEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * <p><a href="https://github.com/AlessioDP/Parties">Parties</a></p>
 *
 * <p>If the player is the leader of a party, have the other members join.</p>
 */
class PartiesArenaListener implements Listener {

    private final Plugin plugin;

    PartiesArenaListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(BukkitPlayerJoinArenaEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String partyName = Parties.getApi().getPartyPlayer(uuid).getPartyName();
        if (partyName == null || partyName.isEmpty()) return;

        Party party = Parties.getApi().getParty(partyName);
        if (!uuid.equals(party.getLeader())) return;

        int size = event.game().size();
        if (!event.game().acceptPlayers(size)) return;

        for (UUID member : party.getMembers()) {
            if (uuid.equals(member)) continue;

            if (this.plugin.getServer().getPlayer(member) != null) {
                event.arena().addPlayer(member);
            }
        }
    }
}
