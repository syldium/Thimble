package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class Argument<T> implements ComponentLike {

    private final String name;
    private boolean required = true;

    protected Argument(@NotNull String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }

    @Override
    public @NotNull Component asComponent() {
        if (this.required) {
            return Component.text("<" + this.name + ">");
        }
        return Component.text("[" + this.name + "]");
    }

    public @NotNull Argument<T> optional() {
        this.required = false;
        return this;
    }

    public abstract @NotNull T parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException;

    public List<String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + this.name + '\'' +
                ", required=" + this.required +
                '}';
    }
}
