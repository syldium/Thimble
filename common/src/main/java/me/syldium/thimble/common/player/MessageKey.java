package me.syldium.thimble.common.player;

import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;

import static me.syldium.thimble.common.service.MessageService.MESSAGES_BUNDLE;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;

public enum MessageKey {

    ACTIONBAR_ENDED("actionbar.ended"),
    ACTIONBAR_MISSED("actionbar.missed"),
    ACTIONBAR_MISSED_LIFE("actionbar.missed.life"),
    ACTIONBAR_MISSED_LIFES("actionbar.missed.lifes"),
    ACTIONBAR_NOT_ENOUGH_PLAYERS("actionbar.not-enough-players"),
    ACTIONBAR_SUCCESSFUL_JUMP("actionbar.successful-jump"),
    ACTIONBAR_THIMBLE("actionbar.thimble"),
    ACTIONBAR_WAITING("actionbar.waiting"),

    CHAT_ARENA_FULL("chat.arena-full"),
    CHAT_ELIMINATED("chat.eliminated"),
    CHAT_JOINED("chat.joined"),
    CHAT_LEFT("chat.left"),
    CHAT_THIMBLE("chat.thimble"),
    CHAT_WIN("chat.win"),

    FEEDBACK_ARENA_ALREADY_EXISTS("feedback.arena.already-exists"),
    FEEDBACK_ARENA_CREATED("feedback.arena.created"),
    FEEDBACK_ARENA_NOT_CONFIGURED("feedback.arena.not-configured"),
    FEEDBACK_ARENA_NOT_LOADED("feedback.arena.not-loaded"),
    FEEDBACK_ARENA_REMOVE("feedback.arena.remove"),
    FEEDBACK_ARENA_SET_GAME_MODE("feedback.arena.set-game-mode"),
    FEEDBACK_ARENA_SET_GAME_MODE_UNKNOWN("feedback.arena.set-game-mode.unknown"),
    FEEDBACK_ARENA_SET_JUMP("feedback.arena.set-jump"),
    FEEDBACK_ARENA_SET_MAX("feedback.arena.set-max"),
    FEEDBACK_ARENA_SET_MAX_LESS_THAN_MIN("feedback.arena.set-max.less-than-min"),
    FEEDBACK_ARENA_SET_MIN("feedback.arena.set-min"),
    FEEDBACK_ARENA_SET_MIN_GREATER_THAN_MAX("feedback.arena.set-min.greater-than-max"),
    FEEDBACK_ARENA_SET_REGION("feedback.arena.set-region"),
    FEEDBACK_ARENA_SET_REGION_NO_SELECTION("feedback.arena.set-region.no-selection"),
    FEEDBACK_ARENA_SET_SPAWN("feedback.arena.set-spawn"),
    FEEDBACK_ARENA_SET_WAIT("feedback.arena.set-wait"),

    FEEDBACK_GAME_ALREADY_IN_GAME("feedback.game.already-in-game"),
    FEEDBACK_GAME_COMMAND("feedback.command-in-game"),
    FEEDBACK_GAME_FULL("feedback.game.full"),
    FEEDBACK_GAME_JOINED("feedback.game.joined"),
    FEEDBACK_GAME_LEFT("feedback.game.left"),
    FEEDBACK_GAME_NOT_IN_GAME("feedback.game.not-in-game"),
    FEEDBACK_GAME_STARTED_GAME("feedback.game.started-game"),
    FEEDBACK_GAME_STATS("feedback.game.stats"),
    FEEDBACK_GAME_STATS_UNKNOWN("feedback.game.stats.unknown"),
    FEEDBACK_GAME_UNKNOWN("feedback.game.unknown"),
    FEEDBACK_MIGRATION("feedback.migration"),
    FEEDBACK_NAN("feedback.nan"),
    FEEDBACK_NOT_VALID_EXECUTOR("feedback.not-valid-executor"),
    FEEDBACK_RELOAD("feedback.reload"),
    FEEDBACK_STATS_UNKNOWN_PLAYER("feedback.stats.unknown-player"),
    FEEDBACK_UNKNOWN_COMMAND("feedback.unknown-command"),
    FEEDBACK_VERSION_ALREADY_LATEST("feedback.version.already-latest"),
    FEEDBACK_VERSION_CURRENT("feedback.version.current"),
    FEEDBACK_VERSION_DOWNLOADING("feedback.version.downloading"),
    FEEDBACK_VERSION_FAILED("feedback.version.failed"),
    FEEDBACK_VERSION_FINISHED("feedback.version.finished"),
    FEEDBACK_VERSION_OUTDATED("feedback.version.outdated"),
    FEEDBACK_VERSION_UNKNOWN_LATEST("feedback.version.unknown-latest"),
    FEEDBACK_VERSION_UP_TO_DATE("feedback.version.up-to-date"),

    HELP_ARENA("help.arena"),
    HELP_BLOCK("help.block"),
    HELP_CREATE("help.create"),
    HELP_JOIN("help.join"),
    HELP_LEAVE("help.leave"),
    HELP_LIST("help.list"),
    HELP_MIGRATE("help.migrate"),
    HELP_RELOAD("help.reload"),
    HELP_REMOVE("help.remove"),
    HELP_SET_GAME_MODE("help.set-game-mode"),
    HELP_SET_JUMP("help.set-jump"),
    HELP_SET_SPAWN("help.set-spawn"),
    HELP_SET_WAIT("help.set-wait"),
    HELP_STATS("help.stats"),
    HELP_VERSION("help.version"),

    INVENTORY_BLOCK_SELECTION("inventory.block-selection"),

    UNIT_JUMP("unit.jump"),
    UNIT_JUMPS("unit.jumps"),
    UNIT_LOSS("unit.loss"),
    UNIT_LOSSES("unit.losses"),
    UNIT_THIMBLE("unit.thimble"),
    UNIT_THIMBLES("unit.thimbles"),
    UNIT_WIN("unit.win"),
    UNIT_WINS("unit.wins");

    private final String accessor;

    MessageKey(@NotNull @PropertyKey(resourceBundle = MESSAGES_BUNDLE) String accessor) {
        this.accessor = accessor;
    }

    public @NotNull String getAccessor() {
        return this.accessor;
    }

    public enum Unit {
        THIMBLES(UNIT_THIMBLE, UNIT_THIMBLES),
        JUMPS(UNIT_JUMP, UNIT_JUMPS),
        LOSSES(UNIT_LOSS, UNIT_LOSSES),
        WINS(UNIT_WIN, UNIT_WINS);

        private final String key;
        private final MessageKey singular;
        private final MessageKey plural;

        Unit(@NotNull MessageKey singular, @NotNull MessageKey plural) {
            this.key = name().toLowerCase(Locale.ROOT);
            this.singular = singular;
            this.plural = plural;
        }

        public @NotNull TagResolver[] tl(int nb, @NotNull MessageService messageService) {
            return new TagResolver[]{
                    unparsed(this.key, String.valueOf(nb)),
                    unparsed('u' + this.key, nb > 1 ? messageService.get(this.plural) : messageService.get(this.singular))
            };
        }
    }
}
