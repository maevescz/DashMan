package eve;

import eve.command.sub.HelpCommand;
import eve.command.sub.InfoCommand;
import eve.command.sub.ListCommand;
import eve.command.sub.PluginActive;
import eve.command.sub.PluginLoader;
import eve.command.sub.PluginManager;
import eve.command.sub.PluginReloader;
import eve.command.sub.ScanCommand;
import eve.command.sub.UnloadCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class DashMan extends JavaPlugin {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private PluginManager pluginManagerCommand;

    @Override
    public void onEnable() {
        this.pluginManagerCommand = new PluginManager();

        this.pluginManagerCommand.registerSubCommand(new HelpCommand());
        this.pluginManagerCommand.registerSubCommand(new ListCommand());
        this.pluginManagerCommand.registerSubCommand(new InfoCommand());
        this.pluginManagerCommand.registerSubCommand(new ScanCommand());
        this.pluginManagerCommand.registerSubCommand(new PluginLoader());
        this.pluginManagerCommand.registerSubCommand(new UnloadCommand());
        this.pluginManagerCommand.registerSubCommand(new PluginReloader());
        this.pluginManagerCommand.registerSubCommand(new PluginActive("enable"));
        this.pluginManagerCommand.registerSubCommand(new PluginActive("disable"));

        registerCommands();
        sendBanner(true);
    }

    @Override
    public void onDisable() {
        sendBanner(false);
    }

    private void registerCommands() {
        PluginCommand command = getCommand("dashman");
        if (command != null) {
            command.setExecutor(pluginManagerCommand);
            command.setTabCompleter(pluginManagerCommand);
        }
    }

    private void sendBanner(boolean enabled) {
        String color = enabled ? "<green>" : "<red>";
        String status = enabled ? "Enable" : "Disable";
        String pluginName = getDescription().getName();
        String version = getDescription().getVersion();

        String bannerText = "\n"
                + color + "░░██╗██████╗░██╗░░</" + (enabled ? "green" : "red") + ">\n"
                + color + "░██╔╝██╔══██╗╚██╗░</" + (enabled ? "green" : "red") + ">\n"
                + color + "██╔╝░██║░░██║░╚██╗</" + (enabled ? "green" : "red") + ">\n"
                + color + "╚██╗░██║░░██║░██╔╝</" + (enabled ? "green" : "red") + ">\n"
                + color + "░╚██╗██████╔╝██╔╝░</" + (enabled ? "green" : "red") + ">\n"
                + color + "░░╚═╝╚═════╝░╚═╝░░</" + (enabled ? "green" : "red") + ">\n"
                + "<white>- Plugin : " + pluginName + "</white>\n"
                + "<white>- Version : " + version + "</white>\n"
                + "<white>- Status : " + status + "</white>";

        Component parsedBanner = miniMessage.deserialize(bannerText);
        getServer().getConsoleSender().sendMessage(parsedBanner);
    }

    public PluginManager getPluginManagerCommand() {
        return pluginManagerCommand;
    }
}
