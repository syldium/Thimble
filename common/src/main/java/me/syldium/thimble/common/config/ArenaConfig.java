package me.syldium.thimble.common.config;

import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.util.EnumUtil;
import me.syldium.thimble.common.util.SignAction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArenaConfig {

    private final ConfigFile config;
    private final ThimblePlugin plugin;

    public ArenaConfig(@NotNull ThimblePlugin plugin, @NotNull ConfigFile config) {
        this.config = config;
        this.plugin = plugin;
    }

    public @NotNull Collection<@NotNull Arena> load() {
        List<Arena> arenas = new LinkedList<>();
        ConfigNode arenasNode = this.config.getNode("arenas");
        if (arenasNode == null) {
            return arenas;
        }

        for (NodeEntry entry : arenasNode.getChildren()) {
            Arena arena = new Arena(this.plugin, entry.key());
            arenas.add(arena);

            ConfigNode node = entry.node();
            node.hydrateLocation("spawn-location", arena::setSpawnLocation);
            node.hydrateLocation("jump-location", arena::setJumpLocation);
            node.hydrateLocation("wait-location", arena::setWaitLocation);
            arena.setMinPlayers(node.getInt("min-players", arena.getMinPlayers()));
            arena.setMaxPlayers(node.getInt("max-players", arena.getMaxPlayers()));
            arena.setGameMode(EnumUtil.valueOf(ThimbleGameMode.class, node.getString("gamemode"), ThimbleGameMode.SINGLE));

            ConfigNode signsNode = node.getNode("signs");
            if (signsNode == null) {
                continue;
            }

            for (NodeEntry signEntry : signsNode.getChildren()) {
                BlockPos pos = signsNode.getBlockPos(signEntry.key());
                if (pos != null) {
                    this.plugin.getGameService().addSign(pos, arena);
                }
            }
        }

        return arenas;
    }

    public @NotNull Map<@NotNull BlockPos, @NotNull SignAction> loadActionSigns() {
        Map<BlockPos, SignAction> map = new HashMap<>();
        ConfigNode signsNode = this.config.getNode("signs");
        if (signsNode == null) {
            return map;
        }

        for (NodeEntry entry : signsNode.getChildren()) {
            SignAction action = EnumUtil.valueOf(SignAction.class, entry.key(), null);
            if (action == null) continue;

            for (NodeEntry signEntry : entry.node().getChildren()) {
                BlockPos pos = signEntry.node().asBlockPos();
                if (pos != null) {
                    map.put(pos, action);
                }
            }
        }
        return map;
    }

    public void save(@NotNull Set<@NotNull Arena> arenas) {
        for (Arena arena : arenas) {
            ConfigNode node = this.config.getOrCreateNode("arenas", arena.getName());
            node.setLocation("spawn-location", arena.getSpawnLocation());
            node.setLocation("jump-location", arena.getJumpLocation());
            node.setLocation("wait-location", arena.getWaitLocation());
            node.setValue("min-players", arena.getMinPlayers());
            node.setValue("max-players", arena.getMaxPlayers());
            node.setValue("gamemode", arena.getGameMode().name());
            node.setBlockVector("min-point", arena.getPoolMinPoint());
            node.setBlockVector("max-point", arena.getPoolMaxPoint());

            ConfigNode signsSection = node.getOrCreateNode("signs");
            int i = 0;
            for (BlockPos position : arena.getSigns()) {
                signsSection.setBlockPos(String.valueOf(i++), position);
            }
        }
    }

    public void save(@NotNull Map<SignAction, Set<BlockPos>> actionSigns) {
        for (Map.Entry<SignAction, Set<BlockPos>> entry : actionSigns.entrySet()) {
            ConfigNode node = this.config.getOrCreateNode("signs", entry.getKey().name());

            int i = 0;
            for (BlockPos position : entry.getValue()) {
                node.setBlockPos(String.valueOf(i++), position);
            }
        }
    }

    public void save() {
        this.config.save();
    }
}
