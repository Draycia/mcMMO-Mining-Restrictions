package net.draycia.mcmmorestrictions.listeners;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import net.draycia.mcmmorestrictions.utilities.MMRListener;
import net.draycia.mcmmorestrictions.utilities.MMRRestriction;
import net.draycia.mcmmorestrictions.McMMORestrictions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class BlockBreakListener implements Listener, MMRListener {
    private McMMORestrictions main;
    private HashMap<Material, MMRRestriction> restrictions = new HashMap<>();

    private boolean enabled;

    public BlockBreakListener(McMMORestrictions main) {
        this.main = main;

        loadRestrictions();
    }

    /**
     * Clear protections and load them in from the config
     */
    @Override
    public void loadRestrictions() {
        enabled = main.getConfig().getBoolean("Restrictions.ItemPickup");

        // If this isn't enabled, don't even attempt to load from config
        if (!enabled) {
            return;
        }

        restrictions.clear();

        // Iterate through all block entries in the config
        ConfigurationSection rootSection = main.getConfig().getConfigurationSection("BlockBreak");
        for (String sectionKey : rootSection.getKeys(false)) {
            ConfigurationSection protectionSection = rootSection.getConfigurationSection(sectionKey);

            // The key name is the material
            Material protectedMaterial = Material.getMaterial(sectionKey.toUpperCase());

            // Get the information about the restriction that's necessary
            PrimarySkillType requiredSkill = PrimarySkillType.getSkill(protectionSection.getString("Skill"));
            int requiredLevel = protectionSection.getInt("Level");
            String chatMessage = protectionSection.getString("Message");
            String actionbarMessage = protectionSection.getString("Actionbar");

            // Create a MMRRestriction and store it for the desired material
            restrictions.put(protectedMaterial, new MMRRestriction(requiredSkill, requiredLevel, chatMessage, actionbarMessage));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!enabled) {
            return;
        }

        if (event.getPlayer().hasPermission("mmr.bypass.blockbreak")) {
            return;
        }

        // Block isn't configured, don't protect it
        if (!restrictions.containsKey(event.getBlock().getType())) {
            return;
        }

        // Ensure the player's data is loaded in, cancel block breaks if it isn't
        McMMOPlayer mmoPlayer = UserManager.getPlayer(event.getPlayer());
        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        MMRRestriction restriction = restrictions.get(event.getBlock().getType());
        PrimarySkillType skillType = restriction.getRequiredSkill();
        int playerLevel = ExperienceAPI.getLevel(event.getPlayer(), skillType);


        // Prevent the block from being broken if the player's level isn't high enough
        if (restriction.getRequiredLevel() > playerLevel) {
            event.setCancelled(true);

            // Send the player a configured message when the break is cancelled
            String message = restriction.getChatMessage();
            if (message != null && !message.isEmpty()) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }

            // Send the player a configured actionbar message when the break is cancelled
            String actionbar = restriction.getActionbarMessage();
            if (actionbar != null && !actionbar.isEmpty()) {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
            }
        }
    }
}
