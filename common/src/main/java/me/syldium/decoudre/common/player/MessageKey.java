package me.syldium.decoudre.common.player;

import org.jetbrains.annotations.NotNull;

public enum MessageKey {

    ACTIONBAR_COMBO("actionbar.combo"),
    ACTIONBAR_ENDED("actionbar.ended"),
    ACTIONBAR_MISSED_PLURAL("actionbar.missed.lifes"),
    ACTIONBAR_MISSED_SINGULAR("actionbar.missed.life"),
    ACTIONBAR_NOT_ENOUGH_PLAYERS("actionbar.not-enough-players"),
    ACTIONBAR_SUCCESSFUL_JUMP("actionbar.successful-jump"),
    ACTIONBAR_WAITING("actionbar.waiting"),

    FEEDBACK_ARENA_ALREADY_EXISTS("feedback.arena.already-exists"),
    FEEDBACK_ARENA_CREATED("feedback.arena.created"),
    FEEDBACK_GAME_ALREADY_IN_GAME("feedback.game.already-in-game"),
    FEEDBACK_GAME_JOINED("feedback.game.joined"),
    FEEDBACK_GAME_LEFT("feedback.game.left"),
    FEEDBACK_GAME_NOT_IN_GAME("feedback.game.not-in-game"),
    FEEDBACK_GAME_UNKNOWN("feedback.game.unknown"),
    FEEDBACK_NAN("feedback.nan"),
    FEEDBACK_NOT_VALID_EXECUTOR("feedback.not-valid-executor"),
    FEEDBACK_STATS_UNKNOWN_PLAYER("feedback.stats.unknown-player"),
    FEEDBACK_UNKNOWN_COMMAND("feedback.unknown-command"),

    HELP_CREATE("help.create"),
    HELP_JOIN("help.join"),
    HELP_LEAVE("help.leave"),
    HELP_LIST("help.list"),
    HELP_SET_JUMP("help.set-jump"),
    HELP_SET_SPAWN("help.set-spawn"),
    HELP_STATS("help.stats");

    private final String accessor;

    MessageKey(@NotNull String accessor) {
        this.accessor = accessor;
    }

    public @NotNull String getAccessor() {
        return this.accessor;
    }
}
