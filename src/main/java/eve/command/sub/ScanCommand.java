package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanCommand implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public String getName() {
        return "scan";
    }

    @Override
    public String getDescription() {
        return "Scan plugin files in plugins directory.";
    }

    @Override
    public String getSyntax() {
        return "/dashman scan";
    }

    @Override
    public String getPermission() {
        return "dashman.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        File pluginsFolder = new File("plugins");
        File[] files = pluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (files == null || files.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<red>No .jar files found in plugins directory.</red>"));
            return true;
        }

        sender.sendMessage(miniMessage.deserialize("<gray>Scanned Plugin Files (" + files.length + "):</gray>\n"));

        for (File file : files) {
            Metadata metadata = readMetadata(file);
            if (metadata != null && metadata.name != null) {
                sender.sendMessage(miniMessage.deserialize("  <yellow>•</yellow> <white>" + file.getName() + "</white> <gray>(" + metadata.name + " v" + metadata.version + ")</gray>"));
            } else {
                sender.sendMessage(miniMessage.deserialize("  <red>•</red> <white>" + file.getName() + "</white> <red>(Invalid JAR or missing plugin.yml)</red>"));
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private Metadata readMetadata(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry paperEntry = jarFile.getJarEntry("paper-plugin.yml");
            if (paperEntry != null) {
                try (InputStream stream = jarFile.getInputStream(paperEntry);
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
                    return new Metadata(config.getString("name"), config.getString("version"));
                }
            }

            JarEntry bukkitEntry = jarFile.getJarEntry("plugin.yml");
            if (bukkitEntry != null) {
                try (InputStream stream = jarFile.getInputStream(bukkitEntry)) {
                    PluginDescriptionFile desc = new PluginDescriptionFile(stream);
                    return new Metadata(desc.getName(), desc.getVersion());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private record Metadata(String name, String version) {}
}
