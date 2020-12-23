package me.syldium.thimble.common.player;

import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum MessageKey {

    ACTIONBAR_ENDED("actionbar.ended"),
    ACTIONBAR_MISSED("actionbar.missed"),
    ACTIONBAR_MISSED_LIFE("actionbar.missed.life"),
    ACTIONBAR_MISSED_LIFES("actionbar.missed.lifes"),
    ACTIONBAR_NOT_ENOUGH_PLAYERS("actionbar.not-enough-players"),
    ACTIONBAR_SUCCESSFUL_JUMP("actionbar.successful-jump"),
    ACTIONBAR_THIMBLE("actionbar.thimble"),
    ACTIONBAR_WAITING("actionbar.waiting"),

    CHAT_ELIMINATED("chat.eliminated"),
    CHAT_JOINED("chat.joined"),
    CHAT_LEFT("chat.left"),
    CHAT_THIMBLE("chat.thimble"),

    FEEDBACK_ARENA_ALREADY_EXISTS("feedback.arena.already-exists"),
    FEEDBACK_ARENA_CREATED("feedback.arena.created"),
    FEEDBACK_ARENA_NOT_CONFIGURED("feedback.arena.not-configured"),
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
    FEEDBACK_NAN("feedback.nan"),
    FEEDBACK_NOT_VALID_EXECUTOR("feedback.not-valid-executor"),
    FEEDBACK_STATS_UNKNOWN_PLAYER("feedback.stats.unknown-player"),
    FEEDBACK_UNKNOWN_COMMAND("feedback.unknown-command"),

    HELP_BLOCK("help.block"),
    HELP_CREATE("help.create"),
    HELP_JOIN("help.join"),
    HELP_LEAVE("help.leave"),
    HELP_LIST("help.list"),
    HELP_REMOVE("help.remove"),
    HELP_SET_GAME_MODE("help.set-game-mode"),
    HELP_SET_JUMP("help.set-jump"),
    HELP_SET_SPAWN("help.set-spawn"),
    HELP_SET_WAIT("help.set-wait"),
    HELP_STATS("help.stats"),

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

    MessageKey(@NotNull String accessor) {
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

        public @NotNull List<Template> tl(int nb, @NotNull MessageService messageService) {
            return Arrays.asList(
                    Template.of(this.key, String.valueOf(nb)),
                    Template.of('u' + this.key, nb > 1 ? messageService.get(this.plural) : messageService.get(this.singular))
            );
        }
    }
}
