package net.draycia.mcmmominingrestrictions;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class BlockBreakListener implements Listener {
    private McMMOMiningRestrictions main;
    private HashMap<Material, BlockRestriction> restrictions = new HashMap<>();

    BlockBreakListener(McMMOMiningRestrictions main) {
        this.main = main;
        
        loadProtections();
    }

    /**
     * Clear protections and load them in from the config
     */
    public void loadProtections() {
        restrictions.clear();

        // Iterate through all block entries in the config
        ConfigurationSection rootSection = main.getConfig().getConfigurationSection("Restrictions");
        for (String sectionKey : rootSection.getKeys(false)) {
            ConfigurationSection protectionSection = rootSection.getConfigurationSection(sectionKey);

            // The key name is the material
            Material protectedMaterial = Material.getMaterial(sectionKey.toUpperCase());

            // Get the information about the restriction that's necessary
            PrimarySkillType requiredSkill = PrimarySkillType.getSkill(protectionSection.getString("Skill"));
            int requiredLevel = protectionSection.getInt("Level");
            String chatMessage = protectionSection.getString("Message");
            String actionbarMessage = protectionSection.getString("Actionbar");

            // Create a BlockRestriction and store it for the desired material
            restrictions.put(protectedMaterial, new BlockRestriction(requiredSkill, requiredLevel, chatMessage, actionbarMessage));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // Ensure the player's data is loaded in, cancel block breaks if it isn't
        McMMOPlayer mmoPlayer = UserManager.getPlayer(event.getPlayer());
        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        // Block isn't configured, don't protect it
        if (!restrictions.containsKey(event.getBlock().getType())) {
            return;
        }

        BlockRestriction restriction = restrictions.get(event.getBlock().getType());
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
