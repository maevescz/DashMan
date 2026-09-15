package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfoCommand implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Show detailed plugin info.";
    }

    @Override
    public String getSyntax() {
        return "/dashman info <plugin>";
    }

    @Override
    public String getPermission() {
        return "dashman.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: " + getSyntax() + "</red>"));
            return true;
        }

        String pluginName = args[1];
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + pluginName + "</yellow>' is not loaded on the server.</red>"));
            return true;
        }

        PluginDescriptionFile desc = plugin.getDescription();
        String status = plugin.isEnabled() ? "<green>Enabled</green>" : "<red>Disabled</red>";
        String authors = desc.getAuthors().isEmpty() ? "Unknown" : String.join(", ", desc.getAuthors());
        String depend = desc.getDepend().isEmpty() ? "None" : String.join(", ", desc.getDepend());

        String infoMessage = "<gold>Plugin Info: <yellow>" + plugin.getName() + "</yellow></gold>\n"
                + "<gray>Status:</gray> " + status + "\n"
                + "<gray>Version:</gray> <white>" + desc.getVersion() + "</white>\n"
                + "<gray>Main Class:</gray> <white>" + desc.getMain() + "</white>\n"
                + "<gray>Author(s):</gray> <white>" + authors + "</white>\n"
                + "<gray>Dependencies:</gray> <white>" + depend + "</white>\n"
                + "<gray>Description:</gray> <white>" + (desc.getDescription() != null ? desc.getDescription() : "N/A") + "</white>";

        sender.sendMessage(miniMessage.deserialize(infoMessage));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String currentArg = args[1].toLowerCase();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getName().toLowerCase().startsWith(currentArg)) {
                    completions.add(plugin.getName());
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
