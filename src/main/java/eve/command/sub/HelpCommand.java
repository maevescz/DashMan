package eve.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays the help menu.";
    }

    @Override
    public String getSyntax() {
        return "/dashman help";
    }

    @Override
    public String getPermission() {
        return "dashman.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String helpText = """
                <gold>           DashMan</gold>
                <yellow>       Plugin Manager</yellow>

                <dark_gray>  ─────────────────────────</dark_gray>

                <yellow>  list      </yellow> <gray>View plugins</gray>
                <yellow>  info      </yellow> <gray>Plugin details</gray>
                <yellow>  scan      </yellow> <gray>Scan plugin files</gray>

                <yellow>  load      </yellow> <gray>Load a plugin</gray>
                <yellow>  enable    </yellow> <gray>Enable a plugin</gray>
                <yellow>  disable   </yellow> <gray>Disable a plugin</gray>
                <yellow>  unload    </yellow> <gray>Unload a plugin</gray>
                <yellow>  reload    </yellow> <gray>Reload a plugin</gray>

                <dark_gray>  ─────────────────────────</dark_gray>
                <gray>  /dashman <command></gray>""";

        sender.sendMessage(miniMessage.deserialize(helpText));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
