package me.syldium.thimble.bukkit.util;

import me.syldium.thimble.bukkit.adventure.VanillaComponentSerializer;
import me.syldium.thimble.common.player.media.Scoreboard;
import me.syldium.thimble.common.util.MinecraftVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

import static me.syldium.thimble.bukkit.adventure.AdventureProvider.legacy;
import static me.syldium.thimble.bukkit.util.MinecraftReflection.findClass;
import static me.syldium.thimble.bukkit.util.MinecraftReflection.findEnum;
import static me.syldium.thimble.bukkit.util.MinecraftReflection.findMcClassName;
import static me.syldium.thimble.bukkit.util.MinecraftReflection.findNmsClassName;
import static org.bukkit.Bukkit.getScoreboardManager;

// Adapted for adventure from https://github.com/MrMicky-FR/FastBoard
public class CraftBukkitFacet {

    private static final boolean LEGACY_PACKETS = MinecraftVersion.isLegacy();
    private static final boolean SINGLE_PACKAGE = MinecraftReflection.hasClass(findNmsClassName("Packet"));
    private static final BiFunction<Player, Scoreboard, ScoreboardFacet> FACET;

    private final Map<Scoreboard, ScoreboardFacet> scoreboards = Collections.synchronizedMap(new IdentityHashMap<>(4));

    static {
        if (ScoreboardPacket.isSupported()) {
            if (LEGACY_PACKETS) {
                FACET = ScoreboardPacket1_12::new;
            } else {
                if (SINGLE_PACKAGE) {
                    FACET = ScoreboardPacket1_13::new;
                } else if (ScoreboardPacket1_21.isSupported()) {
                    FACET = ScoreboardPacket1_21::new;
                } else {
                    FACET = ScoreboardPacket1_17::new;
                }
            }
        } else {
            FACET = ScoreboardApi::new;
        }
    }

    public void setScoreboard(@NotNull Player player, @NotNull Scoreboard scoreboard) {
        ScoreboardFacet facet = FACET.apply(player, scoreboard);
        scoreboard.addListener(facet);
        ScoreboardFacet prev = this.scoreboards.put(scoreboard, facet);
        if (prev != null) {
            prev.remove(player, scoreboard);
        }
    }

    public void removeScoreboard(@NotNull Player player, @NotNull Scoreboard scoreboard) {
        ScoreboardFacet facet = this.scoreboards.remove(scoreboard);
        if (facet != null) {
            facet.remove(player, scoreboard);
        }
    }

    private interface ScoreboardFacet extends Scoreboard.Listener {

        void remove(@NotNull Player player, @NotNull Scoreboard scoreboard);
    }

    @SuppressWarnings("deprecation")
    private static class ScoreboardApi implements ScoreboardFacet {

        private final WeakReference<org.bukkit.scoreboard.Scoreboard> lastScoreboard;
        private final org.bukkit.scoreboard.Scoreboard scoreboard;
        private final Objective objective;
        private final String id = "th-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        ScoreboardApi(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            this.scoreboard = getScoreboardManager().getNewScoreboard();
            this.objective = this.scoreboard.registerNewObjective(this.id, "dummy", legacy().serialize(scoreboard.title()));
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (int score = 0; score < scoreboard.size(); score++) {
                this.lineAdded(scoreboard, scoreboard.lineByScore(score), score);
            }
            this.lastScoreboard = new WeakReference<>(player.getScoreboard());
            player.setScoreboard(this.scoreboard);
        }

        @Override
        public void titleChanged(@NotNull Scoreboard scoreboard, @NotNull Component oldTitle, @NotNull Component newTitle) {
            this.objective.setDisplayName(legacy().serialize(newTitle));
        }

        @Override
        public void lineAdded(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
            final int score = scoreboard.reverseIndex(line);
            final String colorCode = getColorCode(score);
            final String legacy = legacy().serialize(scoreboard.line(line));
            this.objective.getScore(colorCode).setScore(score);
            final Team team = this.scoreboard.registerNewTeam(this.id + score);
            team.setPrefix(legacy);
            team.addEntry(colorCode);
        }

        @Override
        public void lineUpdated(@NotNull Scoreboard scoreboard, @NotNull Component oldLine, @NotNull Component newLine, int line) {
            final int score = scoreboard.reverseIndex(line);
            final Team team = this.scoreboard.getTeam(this.id + score);
            if (team != null) {
                team.setPrefix(legacy().serialize(scoreboard.line(line)));
            }
        }

        @Override
        public void lineRemoved(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
            final int score = scoreboard.reverseIndex(line);
            this.scoreboard.resetScores(getColorCode(score));
            final Team team = this.scoreboard.getTeam(this.id + score);
            if (team != null) {
                team.unregister();
            }
        }

        @Override
        public void remove(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            final org.bukkit.scoreboard.Scoreboard lastScoreboard = this.lastScoreboard.get();
            player.setScoreboard(lastScoreboard == null ? getScoreboardManager().getMainScoreboard() : lastScoreboard);
        }
    }

    private abstract static class ScoreboardPacket implements ScoreboardFacet {

        protected static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(6);
        protected static final Class<?> CLASS_CHAT_COMPONENT = findClass(
                findNmsClassName("IChatBaseComponent"),
                findMcClassName("network.chat.IChatBaseComponent"),
                findMcClassName("network.chat.Component")
        );
        protected static final Class<?> ENUM_CHAT_FORMAT = findClass(
                findNmsClassName("EnumChatFormat"),
                findMcClassName("EnumChatFormat"),
                findMcClassName("ChatFormatting")
        );
        protected static final Object RESET_FORMATTING = findEnum(ENUM_CHAT_FORMAT, "RESET", 21);
        protected static final Object EMPTY_COMPONENT = VanillaComponentSerializer.isSupported() ? VanillaComponentSerializer.serialize(Component.empty()) : null;

        protected static final Class<?> CLASS_OBJECTIVE_PACKET = findClass(
                findNmsClassName("PacketPlayOutScoreboardObjective"),
                findMcClassName("network.protocol.game.PacketPlayOutScoreboardObjective"),
                findMcClassName("network.protocol.game.ClientboundSetObjectivePacket")
        );
        protected static final PacketInvoker NEW_OBJECTIVE_PACKET = PacketInvoker.find(CLASS_OBJECTIVE_PACKET);
        protected static final Class<?> CLASS_DISPLAY_OBJECTIVE_PACKET = findClass(
                findNmsClassName("PacketPlayOutScoreboardDisplayObjective"),
                findMcClassName("network.protocol.game.PacketPlayOutScoreboardDisplayObjective"),
                findMcClassName("network.protocol.game.ClientboundSetDisplayObjectivePacket")
        );
        protected static final PacketInvoker NEW_DISPLAY_OBJECTIVE_PACKET = PacketInvoker.find(CLASS_DISPLAY_OBJECTIVE_PACKET);
        protected static final Class<?> CLASS_SCORE_PACKET = findClass(
                findNmsClassName("PacketPlayOutScoreboardScore"),
                findMcClassName("network.protocol.game.PacketPlayOutScoreboardScore"),
                findMcClassName("network.protocol.game.ClientboundSetScorePacket")
        );
        protected static final MethodHandle NEW_SCORE_PACKET;
        protected static final MethodHandle RESET_SCORE_PACKET;
        protected static final boolean SCORE_OPTIONAL_COMPONENTS;
        protected static final Class<?> CLASS_TEAM_PACKET = findClass(
                findNmsClassName("PacketPlayOutScoreboardTeam"),
                findMcClassName("network.protocol.game.PacketPlayOutScoreboardTeam"),
                findMcClassName("network.protocol.game.ClientboundSetPlayerTeamPacket")
        );
        protected static final PacketInvoker NEW_TEAM_PACKET = PacketInvoker.find(CLASS_TEAM_PACKET);
        protected static final Class<?> CLASS_SERIALIZABLE_TEAM;
        protected static final PacketInvoker NEW_SERIALIZABLE_TEAM;

        private static final Class<?> ENUM_SB_HEALTH_DISPLAY = findClass(
                findNmsClassName("IScoreboardCriteria$EnumScoreboardHealthDisplay"),
                findMcClassName("world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay"),
                findMcClassName("world.scores.criteria.ObjectiveCriteria$RenderType")
        );
        private static final Class<?> ENUM_SB_ACTION = findClass(
                findNmsClassName("PacketPlayOutScoreboardScore$EnumScoreboardAction"),
                findNmsClassName("ScoreboardServer$Action"),
                findMcClassName("server.ScoreboardServer$Action"),
                findMcClassName("server.ServerScoreboard$Method")
        );
        private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER = findEnum(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
        protected static final Object ENUM_SB_ACTION_CHANGE = findEnum(ENUM_SB_ACTION, "CHANGE", 0);
        protected static final Object ENUM_SB_ACTION_REMOVE = findEnum(ENUM_SB_ACTION, "REMOVE", 1);
        private static final Class<?> DISPLAY_SLOT_TYPE;
        private static final Object SIDEBAR_DISPLAY_SLOT;

        protected static final Class<?> ENUM_COLLISION_RULE = findClass(
                findNmsClassName("ScoreboardTeamBase$EnumTeamPush"),
                findMcClassName("world.scores.ScoreboardTeamBase$EnumTeamPush"),
                findMcClassName("world.scores.Team$CollisionRule")
        );
        protected static final Class<?> ENUM_NAME_TAG_VISIBILITY_RULE = findClass(
                findNmsClassName("ScoreboardTeamBase$EnumNameTagVisibility"),
                findMcClassName("world.scores.ScoreboardTeamBase$EnumNameTagVisibility"),
                findMcClassName("world.scores.Team$Visibility")
        );
        protected static final Object ENUM_COLLISION_RULE_ALWAYS = findEnum(ENUM_COLLISION_RULE, "ALWAYS", 0);
        protected static final Object ENUM_NAME_TAG_VISIBILITY_ALWAYS = findEnum(ENUM_NAME_TAG_VISIBILITY_RULE, "ALWAYS", 0);

        static {
            Class<?> serializableTeamClass = null;
            PacketInvoker newSerializableTeam = null;
            if (CLASS_TEAM_PACKET != null) {
                for (Class<?> innerClass : CLASS_TEAM_PACKET.getDeclaredClasses()) {
                    if (!innerClass.isEnum()) {
                        serializableTeamClass = innerClass;
                        newSerializableTeam = PacketInvoker.find(innerClass);
                    }
                }
            }
            CLASS_SERIALIZABLE_TEAM = serializableTeamClass;
            NEW_SERIALIZABLE_TEAM = newSerializableTeam;
            Class<?> displaySlotEnum = findClass(findMcClassName("world.scores.DisplaySlot"));
            DISPLAY_SLOT_TYPE = displaySlotEnum != null ? displaySlotEnum : int.class;
            SIDEBAR_DISPLAY_SLOT = displaySlotEnum != null ? findEnum(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) : 1;

            Class<?> numberFormatClass = findClass(
                    findMcClassName("network.chat.numbers.NumberFormat")
            );
            boolean scoreOptionalComponents = false;
            if (numberFormatClass != null) { // 1.20.3
                Class<?> resetScoreClass = findClass(
                        findMcClassName("network.protocol.game.ClientboundResetScorePacket")
                );
                MethodHandle newScorePacket = MinecraftReflection.findConstructor(CLASS_SCORE_PACKET, String.class, String.class, int.class, CLASS_CHAT_COMPONENT, numberFormatClass);
                if (newScorePacket == null) {
                    newScorePacket = MinecraftReflection.findConstructor(CLASS_SCORE_PACKET, String.class, String.class, int.class, Optional.class, Optional.class);
                    scoreOptionalComponents = true;
                }
                NEW_SCORE_PACKET = newScorePacket;
                RESET_SCORE_PACKET = MinecraftReflection.findConstructor(resetScoreClass, String.class, String.class);
            } else {
                MethodHandle newScorePacket = MinecraftReflection.findConstructor(CLASS_SCORE_PACKET, ENUM_SB_ACTION, String.class, String.class, int.class);
                if (newScorePacket != null) { // 1.13+
                    NEW_SCORE_PACKET = newScorePacket;
                } else {
                    NEW_SCORE_PACKET = MinecraftReflection.findConstructor(CLASS_SCORE_PACKET);
                }
                RESET_SCORE_PACKET = null;
            }
            SCORE_OPTIONAL_COMPONENTS = scoreOptionalComponents;

            for (Class<?> clazz : Arrays.asList(CLASS_OBJECTIVE_PACKET, CLASS_DISPLAY_OBJECTIVE_PACKET, CLASS_SCORE_PACKET, CLASS_TEAM_PACKET, CLASS_SERIALIZABLE_TEAM)) {
                if (clazz == null) continue;
                Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .toArray(Field[]::new);
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }
        }

        private final Object connection;
        protected final String id = "th-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        ScoreboardPacket(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            this.connection = PacketUtil.getPlayerConnection(player);
            this.sendObjectivePacket(ObjectiveMode.CREATE, scoreboard.title());
            this.sendDisplaySlotPacket();
            for (int score = 0; score < scoreboard.size(); score++) {
                this.sendScorePacket(score, ScoreboardAction.CHANGE);
                this.sendTeamPacket(scoreboard.lineByScore(score), score, TeamMode.CREATE);
            }
        }

        @Override
        public void remove(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            List<Component> lines = scoreboard.lines();
            for (int score = 0; score < lines.size(); score++) {
                this.sendTeamPacket(scoreboard.lineByScore(score), score, TeamMode.REMOVE);
            }
            this.sendObjectivePacket(ObjectiveMode.REMOVE, Component.empty());
        }

        @Override
        public void titleChanged(@NotNull Scoreboard scoreboard, @NotNull Component oldTitle, @NotNull Component newTitle) {
            this.sendObjectivePacket(ObjectiveMode.UPDATE, newTitle);
        }

        @Override
        public void lineAdded(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
            this.sendScorePacket(line, ScoreboardAction.CHANGE);
            this.sendTeamPacket(content, scoreboard.reverseIndex(line), TeamMode.CREATE);
        }

        @Override
        public void lineUpdated(@NotNull Scoreboard scoreboard, @NotNull Component oldLine, @NotNull Component newLine, int line) {
            this.sendTeamPacket(newLine, scoreboard.reverseIndex(line), TeamMode.UPDATE);
        }

        @Override
        public void lineRemoved(@NotNull Scoreboard scoreboard, @NotNull Component content, int line) {
            this.sendTeamPacket(content, scoreboard.reverseIndex(line), TeamMode.REMOVE);
            this.sendScorePacket(scoreboard.reverseIndex(line), ScoreboardAction.REMOVE);
        }

        protected void sendObjectivePacket(@NotNull ObjectiveMode mode, @NotNull Component title) {
            try {
                Object packet = NEW_OBJECTIVE_PACKET.invoke();

                this.setField(packet, String.class, this.id);
                this.setField(packet, int.class, mode.ordinal());

                if (mode != ObjectiveMode.REMOVE) {
                    this.setComponentField(packet, title, 1);
                    this.setField(packet, Optional.class, Optional.empty()); // Number format for 1.20.5+, previously nullable
                    this.setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
                }

                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        protected void sendDisplaySlotPacket() {
            try {
                Object packet = NEW_DISPLAY_OBJECTIVE_PACKET.invoke();
                this.setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT);
                this.setField(packet, String.class, this.id);
                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        public void sendScorePacket(int score, ScoreboardAction action) {
            try {
                Object packet = NEW_SCORE_PACKET.invoke();
                this.setField(packet, String.class, getColorCode(score), 0);
                this.setField(packet, ENUM_SB_ACTION, action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE);

                if (action == ScoreboardAction.CHANGE) {
                    this.setField(packet, String.class, this.id, 1);
                    this.setField(packet, int.class, score);
                }

                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        public abstract void sendTeamPacket(@NotNull Component line, int score, @NotNull CraftBukkitFacet.TeamMode mode);

        protected void sendPacket(@NotNull Object packet) {
            PacketUtil.sendPacket(this.connection, packet);
        }

        protected void setField(@NotNull Object object, @NotNull Class<?> fieldType, @NotNull Object value) throws ReflectiveOperationException {
            this.setField(object, fieldType, value, 0);
        }

        protected void setField(@NotNull Object object, @NotNull Class<?> fieldType, @NotNull Object value, int count) throws ReflectiveOperationException {
            int i = 0;

            for (Field f : PACKETS.get(object.getClass())) {
                if (f.getType() == fieldType && i++ == count) {
                    f.set(object, value);
                }
            }
        }

        protected abstract void setComponentField(@NotNull Object object, @NotNull Component component, int count) throws ReflectiveOperationException;

        public static boolean isSupported() {
            if (!(PacketUtil.isSupported() && VanillaComponentSerializer.isSupported() && NEW_OBJECTIVE_PACKET != null && NEW_DISPLAY_OBJECTIVE_PACKET != null && NEW_SCORE_PACKET != null && NEW_TEAM_PACKET != null)) {
                return false;
            }
            return SINGLE_PACKAGE || (RESET_FORMATTING != null && NEW_SERIALIZABLE_TEAM != null);
        }
    }

    private static class ScoreboardPacket1_21 extends ScoreboardPacket1_17 {

        ScoreboardPacket1_21(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            super(player, scoreboard);
        }

        @Override
        public void hydrateSerializableTeam(@NotNull Object team, @NotNull Component line) throws ReflectiveOperationException {
            this.setComponentField(team, Component.empty(), 0); // Display name
            this.setField(team, ENUM_CHAT_FORMAT, RESET_FORMATTING); // Formatting
            this.setComponentField(team, line, 1); // Prefix
            this.setComponentField(team, Component.empty(), 2); // Suffix
            this.setField(team, ENUM_NAME_TAG_VISIBILITY_RULE, ENUM_NAME_TAG_VISIBILITY_ALWAYS, 0);
            this.setField(team, ENUM_COLLISION_RULE, ENUM_COLLISION_RULE_ALWAYS, 0);
        }

        public static boolean isSupported() {
            return CLASS_SERIALIZABLE_TEAM != null && Arrays.stream(CLASS_SERIALIZABLE_TEAM.getDeclaredFields()).anyMatch(field -> field.getType().equals(ENUM_NAME_TAG_VISIBILITY_RULE));
        }
    }

    private static class ScoreboardPacket1_17 extends ScoreboardPacket1_13 {

        ScoreboardPacket1_17(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            super(player, scoreboard);
        }

        @Override
        public void sendTeamPacket(@NotNull Component line, int score, @NotNull TeamMode mode) {
            if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
                throw new UnsupportedOperationException();
            }

            try {
                Object packet = NEW_TEAM_PACKET.invoke();

                this.setField(packet, String.class, this.id + ':' + score); // Team name
                this.setField(packet, int.class, mode.ordinal(), 0); // Update mode

                if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
                    Object team = NEW_SERIALIZABLE_TEAM.invoke();
                    this.hydrateSerializableTeam(team, line);
                    this.setField(packet, Optional.class, Optional.of(team));
                }

                if (mode == TeamMode.CREATE) {
                    setField(packet, Collection.class, Collections.singletonList(getColorCode(score))); // Players in the team
                }

                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        public void hydrateSerializableTeam(@NotNull Object team, @NotNull Component line) throws ReflectiveOperationException {
            this.setComponentField(team, Component.empty(), 0); // Display name
            this.setField(team, ENUM_CHAT_FORMAT, RESET_FORMATTING); // Formatting
            this.setComponentField(team, line, 1); // Prefix
            this.setComponentField(team, Component.empty(), 2); // Suffix
            this.setField(team, String.class, "always", 0); // Visibility
            this.setField(team, String.class, "always", 1); // Collisions
        }
    }

    private static class ScoreboardPacket1_13 extends ScoreboardPacket {

        ScoreboardPacket1_13(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            super(player, scoreboard);
        }

        @Override
        public void sendScorePacket(int score, ScoreboardAction action) {
            try {
                String objName = getColorCode(score);
                Object enumAction = action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;

                if (RESET_SCORE_PACKET == null) { // Pre 1.20.3
                    sendPacket(NEW_SCORE_PACKET.invoke(enumAction, this.id, objName, score));
                    return;
                }

                if (action == ScoreboardAction.REMOVE) {
                    sendPacket(RESET_SCORE_PACKET.invoke(objName, this.id));
                    return;
                }

                if (SCORE_OPTIONAL_COMPONENTS) {
                    sendPacket(NEW_SCORE_PACKET.invoke(objName, this.id, score, Optional.empty(), Optional.empty()));
                } else {
                    sendPacket(NEW_SCORE_PACKET.invoke(objName, this.id, score, null, null));
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        @Override
        public void sendTeamPacket(@NotNull Component line, int score, @NotNull TeamMode mode) {
            if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
                throw new UnsupportedOperationException();
            }

            try {
                Object packet = NEW_TEAM_PACKET.invoke();

                this.setField(packet, String.class, this.id + ':' + score); // Team name
                this.setField(packet, int.class, mode.ordinal(), 0); // Update mode

                if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
                    this.setComponentField(packet, line, 2); // Prefix
                }

                if (mode == TeamMode.CREATE) {
                    setField(packet, Collection.class, Collections.singletonList(getColorCode(score))); // Players in the team
                }

                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        @Override
        protected void setComponentField(@NotNull Object object, @NotNull Component component, int count) throws ReflectiveOperationException {
            int i = 0;
            for (Field f : PACKETS.get(object.getClass())) {
                if ((f.getType() == String.class || f.getType() == CLASS_CHAT_COMPONENT) && i++ == count) {
                    f.set(object, component == Component.empty() ? EMPTY_COMPONENT : VanillaComponentSerializer.serialize(component));
                }
            }
        }
    }

    private static class ScoreboardPacket1_12 extends ScoreboardPacket {

        ScoreboardPacket1_12(@NotNull Player player, @NotNull Scoreboard scoreboard) {
            super(player, scoreboard);
        }

        @Override
        public void sendTeamPacket(@NotNull Component line, int score, @NotNull TeamMode mode) {
            if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
                throw new UnsupportedOperationException();
            }

            try {
                Object packet = NEW_TEAM_PACKET.invoke();

                setField(packet, String.class, this.id + ':' + score); // Team name
                setField(packet, int.class, mode.ordinal(), 1); // Update mode

                if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
                    String legacy = legacy().serialize(line);
                    String prefix;
                    String suffix = null;

                    if (legacy.isEmpty()) {
                        prefix = getColorCode(score) + ChatColor.RESET;
                    } else if (legacy.length() <= 16) {
                        prefix = legacy;
                    } else {
                        // Prevent splitting color codes
                        int index = legacy.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
                        prefix = legacy.substring(0, index);
                        String suffixTmp = legacy.substring(index);
                        ChatColor chatColor = null;

                        if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) {
                            chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
                        }

                        String color = ChatColor.getLastColors(prefix);
                        boolean addColor = chatColor == null || chatColor.isFormat();

                        suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
                    }

                    if (prefix.length() > 16 || (suffix != null && suffix.length() > 16)) {
                        // Something went wrong, just cut to prevent client crash/kick
                        prefix = prefix.substring(0, 16);
                        suffix = (suffix != null) ? suffix.substring(0, 16) : null;
                    }

                    setField(packet, String.class, prefix, 2); // Prefix
                    setField(packet, String.class, suffix == null ? "" : suffix, 3); // Suffix
                    setField(packet, String.class, "always", 4); // Visibility for 1.8+
                    setField(packet, String.class, "always", 5); // Collisions for 1.9+

                    if (mode == TeamMode.CREATE) {
                        setField(packet, Collection.class, Collections.singletonList(getColorCode(score))); // Players in the team
                    }
                }

                this.sendPacket(packet);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        @Override
        protected void setComponentField(@NotNull Object object, @NotNull Component component, int count) throws ReflectiveOperationException {
            this.setField(object, String.class, legacy().serialize(component), count);
        }
    }

    protected static @NotNull String getColorCode(int score) {
        return ChatColor.values()[score].toString();
    }

    enum ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    enum TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    enum ScoreboardAction {
        CHANGE, REMOVE
    }
}
