package net.draycia.mcmmominingrestritions;

import org.bukkit.plugin.java.JavaPlugin;

public final class McMMOMiningRestritions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getCommand("mmr").setExecutor(new MMRCommand(this));
    }
}
