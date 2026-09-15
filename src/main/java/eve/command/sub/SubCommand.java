package eve.command.sub;

import org.bukkit.command.CommandSender;

import java.util.List;
public interface SubCommand {
    String getName();
    String getDescription();
    String getSyntax();
    String getPermission();
    boolean execute(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);
}
