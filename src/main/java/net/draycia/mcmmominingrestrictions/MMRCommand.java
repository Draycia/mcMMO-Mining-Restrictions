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
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            return true;
        }

        main.reloadConfig();

        String message = "&amcMMO-Mining-Restrictions successfully reloaded!";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }
}
