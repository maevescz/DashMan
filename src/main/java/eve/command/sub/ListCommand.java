package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class ListCommand implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "View installed plugins and status.";
    }

    @Override
    public String getSyntax() {
        return "/dashman list";
    }

    @Override
    public String getPermission() {
        return "dashman.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        int total = plugins.length;
        int enabledCount = 0;
        int disabledCount = 0;

        sender.sendMessage(miniMessage.deserialize("<gray>DashMan Plugins</gray>\n"));

        for (Plugin plugin : plugins) {
            if (plugin.isEnabled()) {
                enabledCount++;
                sender.sendMessage(miniMessage.deserialize("  <green>✔</green> <white>" + String.format("%-14s", plugin.getName()) + "</white> <gray>" + plugin.getDescription().getVersion() + "</gray>"));
            } else {
                disabledCount++;
                sender.sendMessage(miniMessage.deserialize("  <red>✘</red> <white>" + String.format("%-14s", plugin.getName()) + "</white> <gray>" + plugin.getDescription().getVersion() + "</gray>"));
            }
        }

        sender.sendMessage(miniMessage.deserialize("\n<gray>" + total + " Plugins • <green>" + enabledCount + " Enabled</green> • <red>" + disabledCount + " Disabled</red></gray>"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
