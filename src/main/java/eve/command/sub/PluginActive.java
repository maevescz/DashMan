package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginActive implements SubCommand {

    private final String action;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    public PluginActive(String action) {
        this.action = action.toLowerCase();
    }

    @Override
    public String getName() {
        return action;
    }

    @Override
    public String getDescription() {
        return action.equalsIgnoreCase("enable") ? "Enable a disabled plugin." : "Disable an active plugin.";
    }

    @Override
    public String getSyntax() {
        return "/dashman " + action + " <plugin>";
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
            sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + plugin.getName() + "</yellow>' is a Paper Plugin (paper-plugin.yml) and cannot be toggled at runtime.</red>"));
            return true;
        }

        if (action.equalsIgnoreCase("enable")) {
            if (plugin.isEnabled()) {
                sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + plugin.getName() + "</yellow>' is already enabled.</red>"));
                return true;
            }
            pluginManager.enablePlugin(plugin);
            sender.sendMessage(miniMessage.deserialize("<green>Successfully enabled plugin '<yellow>" + plugin.getName() + "</yellow>'!</green>"));
        } else {
            if (!plugin.isEnabled()) {
                sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + plugin.getName() + "</yellow>' is already disabled.</red>"));
                return true;
            }
            pluginManager.disablePlugin(plugin);
            sender.sendMessage(miniMessage.deserialize("<green>Successfully disabled plugin '<yellow>" + plugin.getName() + "</yellow>'!</green>"));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String currentArg = args[1].toLowerCase();
            boolean isEnable = action.equalsIgnoreCase("enable");

            for (Plugin plugin : pluginManager.getPlugins()) {
                if (plugin.isEnabled() != isEnable && plugin.getName().toLowerCase().startsWith(currentArg)) {
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
