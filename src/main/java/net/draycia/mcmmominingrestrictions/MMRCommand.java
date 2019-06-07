package net.draycia.mcmmominingrestrictions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MMRCommand implements CommandExecutor {
    private McMMOMiningRestrictions main;

    MMRCommand(McMMOMiningRestrictions main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if the command being ran isn't /mmr reload
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            return true;
        }

        // Otherwise reload config and alert the command sender the command was ran
        main.reloadConfig();

        String message = "&amcMMO-Mining-Restrictions successfully reloaded!";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }
}
