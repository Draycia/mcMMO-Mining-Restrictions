package net.draycia.mcmmorestrictions;

import net.draycia.mcmmorestrictions.commands.MMRCommand;
import net.draycia.mcmmorestrictions.listeners.BlockBreakListener;
import net.draycia.mcmmorestrictions.listeners.ItemPickupListener;
import net.draycia.mcmmorestrictions.listeners.TeleportListener;
import net.draycia.mcmmorestrictions.utilities.MMRListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class McMMORestrictions extends JavaPlugin {
    // Assign everything to an array for easier reloading
    private ArrayList<MMRListener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        BlockBreakListener blockBreakListener = new BlockBreakListener(this);
        listeners.add(blockBreakListener);

        ItemPickupListener itemPickupListener = new ItemPickupListener(this);
        listeners.add(itemPickupListener);

        TeleportListener teleportListener = new TeleportListener(this);
        listeners.add(teleportListener);

        for (MMRListener listener : listeners) {
            if (listener instanceof Listener) {
                getServer().getPluginManager().registerEvents((Listener)listener, this);
            }
        }

        // Primary command, currently only used for reloading
        getCommand("mmr").setExecutor(new MMRCommand(this, listeners));
    }
}
