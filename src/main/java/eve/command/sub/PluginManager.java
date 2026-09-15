package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final HelpCommand helpCommand = new HelpCommand();

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return helpCommand.execute(sender, args);
        }

        String subName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subName);

        if (subCommand == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Unknown subcommand. Use /" + label + " help for available commands.</red>"));
            return true;
        }

        if (subCommand.getPermission() != null && !subCommand.getPermission().isEmpty() && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(miniMessage.deserialize("<red>You do not have permission to execute this command.</red>"));
            return true;
        }

        return subCommand.execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (SubCommand subCommand : subCommands.values()) {
                if (subCommand.getPermission() == null || subCommand.getPermission().isEmpty() || sender.hasPermission(subCommand.getPermission())) {
                    if (subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(subCommand.getName());
                    }
                }
            }
            return completions;
        }

        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.tabComplete(sender, args);
            }
        }

        return List.of();
    }
}
