package me.syldium.thimble.common.command.game;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;

public class StatsCommand extends ChildCommand.One<String> {

    public StatsCommand() {
        super("stats", Arguments.string("player").optional(), MessageKey.HELP_STATS, Permission.stats());
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @Nullable String player) throws CommandException {
        if (player != null && !player.equals(sender.name())) {
            Permission.stats("others").verify(sender);
        }

        String username = player == null ? sender.name() : player;
        plugin.getStatsService().getPlayerStatistics(username).thenAccept(optional -> {
            if (optional.isPresent()) {
                ThimblePlayerStats stats = optional.get();
                TagResolver.Builder args = TagResolver.builder();
                MessageService service = plugin.getMessageService();
                args.resolver(component("player", stats.displayName()));
                args.resolvers(MessageKey.Unit.WINS.tl(stats.wins(), service));
                args.resolvers(MessageKey.Unit.LOSSES.tl(stats.losses(), service));
                args.resolvers(MessageKey.Unit.JUMPS.tl(stats.jumps(), service));
                args.resolvers(MessageKey.Unit.THIMBLES.tl(stats.thimbles(), service));
                sender.sendFeedback(CommandResult.success(MessageKey.FEEDBACK_GAME_STATS, args.build()));
            } else {
                sender.sendFeedback(CommandResult.error(MessageKey.FEEDBACK_GAME_STATS_UNKNOWN, unparsed("player", username)));
            }
        });
        return CommandResult.success();
    }
}
