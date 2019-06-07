package net.draycia.mcmmominingrestrictions;

import org.bukkit.plugin.java.JavaPlugin;

public final class McMMOMiningRestrictions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Assign to a variable so it can be passed into the command for reloading purposes
        BlockBreakListener blockBreakListener = new BlockBreakListener(this);

        getServer().getPluginManager().registerEvents(blockBreakListener, this);

        // Primary command, currently only used for reloading
        getCommand("mmr").setExecutor(new MMRCommand(this, blockBreakListener));
    }
}
