package me.syldium.decoudre.common.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import static me.syldium.decoudre.common.DeCoudrePlugin.PREFIX;
import static me.syldium.decoudre.common.player.Message.MessageType.ERROR;
import static me.syldium.decoudre.common.player.Message.MessageType.INFO;
import static me.syldium.decoudre.common.player.Message.MessageType.INFORMATIVE;
import static me.syldium.decoudre.common.player.Message.MessageType.SUCCESS;
import static me.syldium.decoudre.common.player.Message.MessageType.SUCCESS_GOLD;

public enum Message implements ComponentLike {

    ARENA_CREATED(SUCCESS, "The arena has been created."),
    ARENA_JOINED(SUCCESS, "You have joined the arena."),
    COMBO(SUCCESS_GOLD, "Combo"),
    COMMAND_FAILED(ERROR, Component.translatable("command.failed")),
    CREATE_NEW_ARENA(INFO, "Create a new arena"),
    END_GAME(INFORMATIVE, "End of the game"),
    IN_GAME(ERROR, "You are already in game!"),
    JOIN_ARENA(INFO, "Join an arena to play"),
    LEAVE_ARENA(INFO, "Leave the arena"),
    LIFES_LEFT(INFO, "%lifes% left"),
    NAN(ERROR, "Not a number"),
    NOT_ENOUGH_PLAYERS(ERROR, "Game start aborted because there are not enough players."),
    NOT_IN_GAME(ERROR, "You are not in game!"),
    SET_ARENA_JUMP(INFO, "Set the location from which players must jump"),
    SET_ARENA_SPAWN(INFO, "Sets the location of the arena lobby"),
    SUCCESSFUL_JUMP(INFO, "Successful jump"),
    UNKNOWN_ARENA(ERROR, "Unknown arena"),
    UNKNOWN_COMMAND(ERROR, Component.translatable("command.unknown.command")),
    WAITING(ERROR, "Waiting...");

    private final Component component;

    Message(@NotNull MessageType type, @NotNull String value) {
        this(type, Component.text(value));
    }

    Message(@NotNull MessageType type, @NotNull Component component) {
        this.component = component.color(type.color);
    }

    public Component format() {
        return PREFIX.append(this.component);
    }

    public Component format(@NotNull Player player) {
        return PREFIX.append(this.component.replaceText("%player%", Component.text(player.getName())));
    }

    public Component format(int n) {
        return PREFIX.append(this.component.replaceText("%lifes%", Component.text(n)));
    }

    @Override
    public @NotNull Component asComponent() {
        return this.component;
    }

    enum MessageType {
        ERROR(NamedTextColor.RED),
        INFO(NamedTextColor.GRAY),
        INFORMATIVE(NamedTextColor.YELLOW),
        SUCCESS(NamedTextColor.GREEN),
        SUCCESS_GOLD(NamedTextColor.GOLD);

        private final TextColor color;

        MessageType(@NotNull TextColor color) {
            this.color = color;
        }
    }
}
