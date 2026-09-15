package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Load a plugin JAR file dynamically from plugins folder.";
    }

    @Override
    public String getSyntax() {
        return "/dashman load <file.jar>";
    }

    @Override
    public String getPermission() {
        return "dashman.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        File pluginsFolder = new File("plugins");
        File[] files = pluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: " + getSyntax() + "</red>"));
            sender.sendMessage(miniMessage.deserialize("<gray>Available JAR files in plugins folder:</gray>"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    sender.sendMessage(miniMessage.deserialize("  <yellow>- " + file.getName() + "</yellow>"));
                }
            } else {
                sender.sendMessage(miniMessage.deserialize("  <red>No .jar files found in plugins folder.</red>"));
            }
            return true;
        }

        String fileName = args[1];
        if (!fileName.endsWith(".jar")) {
            fileName += ".jar";
        }

        File file = new File(pluginsFolder, fileName);

        if (!file.exists() || !file.isFile()) {
            sender.sendMessage(miniMessage.deserialize("<red>File '<yellow>" + fileName + "</yellow>' not found in plugins folder.</red>"));
            return true;
        }

        Metadata metadata = readMetadata(file);
        if (metadata == null || metadata.name == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Failed to parse plugin description from '<yellow>" + fileName + "</yellow>'.</red>"));
            return true;
        }

        if (pluginManager.getPlugin(metadata.name) != null) {
            sender.sendMessage(miniMessage.deserialize("<red>Plugin '<yellow>" + metadata.name + "</yellow>' is already loaded on the server.</red>"));
            return true;
        }

        List<String> missingDependencies = new ArrayList<>();
        for (String dep : metadata.depend) {
            if (pluginManager.getPlugin(dep) == null) {
                missingDependencies.add(dep);
            }
        }

        if (!missingDependencies.isEmpty()) {
            sender.sendMessage(miniMessage.deserialize("<red>[Alert] Plugin '<yellow>" + metadata.name + "</yellow>' requires missing dependencies: <gold>" + String.join(", ", missingDependencies) + "</gold></red>"));
            sender.sendMessage(miniMessage.deserialize("<red>Failed to load plugin '<yellow>" + metadata.name + "</yellow>' due to missing dependencies.</red>"));
            return true;
        }

        try {
            Plugin loadedPlugin = pluginManager.loadPlugin(file);
            if (loadedPlugin == null) {
                sender.sendMessage(miniMessage.deserialize("<red>Bukkit failed to load plugin '<yellow>" + metadata.name + "</yellow>'.</red>"));
                return true;
            }

            loadedPlugin.onLoad();
            pluginManager.enablePlugin(loadedPlugin);

            sender.sendMessage(miniMessage.deserialize("<green>Successfully loaded and enabled plugin '<yellow>" + metadata.name + "</yellow>' v" + metadata.version + "!</green>"));
        } catch (Exception e) {
            sender.sendMessage(miniMessage.deserialize("<red>An error occurred while loading plugin '<yellow>" + metadata.name + "</yellow>': " + e.getMessage() + "</red>"));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            File pluginsFolder = new File("plugins");
            File[] files = pluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));

            if (files == null) {
                return Collections.emptyList();
            }

            List<String> jarFiles = new ArrayList<>();
            String currentArg = args[1].toLowerCase();

            for (File file : files) {
                String name = file.getName();
                if (name.toLowerCase().startsWith(currentArg)) {
                    jarFiles.add(name);
                }
            }
            return jarFiles;
        }
        return Collections.emptyList();
    }

    private Metadata readMetadata(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry paperEntry = jarFile.getJarEntry("paper-plugin.yml");
            if (paperEntry != null) {
                try (InputStream stream = jarFile.getInputStream(paperEntry);
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
                    String name = config.getString("name");
                    String version = config.getString("version");
                    List<String> depend = new ArrayList<>();
                    depend.addAll(config.getStringList("dependencies.server.bootstrap"));
                    depend.addAll(config.getStringList("dependencies.server.load"));
                    return new Metadata(name, version, depend);
                }
            }

            JarEntry bukkitEntry = jarFile.getJarEntry("plugin.yml");
            if (bukkitEntry != null) {
                try (InputStream stream = jarFile.getInputStream(bukkitEntry)) {
                    PluginDescriptionFile desc = new PluginDescriptionFile(stream);
                    return new Metadata(desc.getName(), desc.getVersion(), desc.getDepend() != null ? desc.getDepend() : Collections.emptyList());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private record Metadata(String name, String version, List<String> depend) {}
}
