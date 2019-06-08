package net.draycia.mcmmominingrestrictions;

import net.draycia.mcmmominingrestrictions.commands.MMRCommand;
import net.draycia.mcmmominingrestrictions.listeners.BlockBreakListener;
import net.draycia.mcmmominingrestrictions.listeners.ItemPickupListener;
import net.draycia.mcmmominingrestrictions.utilities.MMRListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class McMMOMiningRestrictions extends JavaPlugin {
    // Assign everything to an array for easier reloading
    private ArrayList<MMRListener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        BlockBreakListener blockBreakListener = new BlockBreakListener(this);
        listeners.add(blockBreakListener);
        getServer().getPluginManager().registerEvents(blockBreakListener, this);

        ItemPickupListener itemPickupListener = new ItemPickupListener(this);
        listeners.add(itemPickupListener);
        getServer().getPluginManager().registerEvents(itemPickupListener, this);

        // Primary command, currently only used for reloading
        getCommand("mmr").setExecutor(new MMRCommand(this, listeners));
    }
}
