package net.draycia.mcmmominingrestrictions;

import org.bukkit.plugin.java.JavaPlugin;

public final class McMMOMiningRestrictions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getCommand("mmr").setExecutor(new MMRCommand(this));
    }
}
