package me.syldium.decoudre.common.command.game;

import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.service.MessageService;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand extends ChildCommand.One<String> {

    public StatsCommand() {
        super("stats", Arguments.string("player").optional(), MessageKey.HELP_STATS, Permission.stats());
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @Nullable String player) throws CommandException {
        if (player != null && !player.equals(sender.name())) {
            Permission.stats("others").verify(sender);
        }

        String username = player == null ? sender.name() : player;
        plugin.getStatsService().getPlayerStatistics(username).thenAccept(optional -> {
            if (optional.isPresent()) {
                DePlayerStats stats = optional.get();
                List<Template> args = new ArrayList<>();
                MessageService service = plugin.getMessageService();
                args.add(Template.of("player", stats.name()));
                args.addAll(MessageKey.Unit.WINS.tl(stats.getWins(), service));
                args.addAll(MessageKey.Unit.LOSSES.tl(stats.getLosses(), service));
                args.addAll(MessageKey.Unit.JUMPS.tl(stats.getJumps(), service));
                args.addAll(MessageKey.Unit.DACS.tl(stats.getDacs(), service));
                sender.sendFeedback(CommandResult.success(MessageKey.FEEDBACK_GAME_STATS, args.toArray(new Template[0])));
            } else {
                sender.sendFeedback(CommandResult.error(MessageKey.FEEDBACK_GAME_STATS_UNKNOWN, Template.of("player", username)));
            }
        });
        return CommandResult.success();
    }
}
