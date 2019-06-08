package net.draycia.mcmmominingrestrictions.commands;

import net.draycia.mcmmominingrestrictions.McMMOMiningRestrictions;
import net.draycia.mcmmominingrestrictions.listeners.BlockBreakListener;
import net.draycia.mcmmominingrestrictions.listeners.ItemPickupListener;
import net.draycia.mcmmominingrestrictions.utilities.MMRListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class MMRCommand implements CommandExecutor {
    private McMMOMiningRestrictions main;
    private ArrayList<MMRListener> listeners;

    public MMRCommand(McMMOMiningRestrictions main, ArrayList<MMRListener> listeners) {
        this.main = main;
        this.listeners = listeners;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if the command being ran isn't /mmr reload
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            return true;
        }

        // Otherwise reload config and alert the command sender the command was ran
        main.reloadConfig();

        for (MMRListener listener : listeners) {
            listener.loadRestrictions();
        }

        String message = "&amcMMO-Mining-Restrictions successfully reloaded!";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }
}
