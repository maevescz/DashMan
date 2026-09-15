package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnloadCommand implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    @Override
    public String getName() {
        return "unload";
    }

    @Override
    public String getDescription() {
        return "Unload a plugin dynamically.";
    }

    @Override
    public String getSyntax() {
        return "/dashman unload <plugin>";
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
        Plugin plugin = pluginManager.getPlugin(pluginName);

        if (plugin == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + pluginName + "</yellow>' is not loaded on the server.</red>"));
            return true;
        }

        if (isPaperPlugin(plugin)) {
            sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + plugin.getName() + "</yellow>' is a Paper Plugin (paper-plugin.yml) and cannot be dynamically unloaded safely.</red>"));
            return true;
        }

        try {
            if (plugin.isEnabled()) {
                pluginManager.disablePlugin(plugin);
            }
            sender.sendMessage(miniMessage.deserialize("<green>Successfully unloaded plugin '<yellow>" + plugin.getName() + "</yellow>'!</green>"));
        } catch (Exception e) {
            sender.sendMessage(miniMessage.deserialize("<red>An error occurred while unloading plugin '<yellow>" + plugin.getName() + "</yellow>': " + e.getMessage() + "</red>"));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String currentArg = args[1].toLowerCase();
            for (Plugin plugin : pluginManager.getPlugins()) {
                if (plugin.getName().toLowerCase().startsWith(currentArg)) {
                    completions.add(plugin.getName());
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }

    private boolean isPaperPlugin(Plugin plugin) {
        try {
            return plugin.getPluginLoader().getClass().getName().contains("io.papermc.paper.plugin");
        } catch (Throwable ignored) {
        }
        return false;
    }
}
