package net.draycia.mcmmorestrictions.listeners;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import net.draycia.mcmmorestrictions.McMMORestrictions;
import net.draycia.mcmmorestrictions.utilities.MMRListener;
import net.draycia.mcmmorestrictions.utilities.MMRRestriction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.HashMap;

public class ItemPickupListener implements Listener, MMRListener {
    private McMMORestrictions main;
    private HashMap<Material, MMRRestriction> restrictions = new HashMap<>();

    private boolean enabled;

    public ItemPickupListener(McMMORestrictions main) {
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
        ConfigurationSection rootSection = main.getConfig().getConfigurationSection("ItemPickup");
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

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!enabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getEntity();

        if (player.hasPermission("mmr.bypass.itempickup")) {
            return;
        }

        // Block isn't configured, don't protect it
        if (!restrictions.containsKey(event.getItem().getItemStack().getType())) {
            return;
        }

        // Ensure the player's data is loaded in, cancel block breaks if it isn't
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        MMRRestriction restriction = restrictions.get(event.getItem().getItemStack().getType());
        PrimarySkillType skillType = restriction.getRequiredSkill();
        int playerLevel = ExperienceAPI.getLevel(player, skillType);


        // Prevent the block from being broken if the player's level isn't high enough
        if (restriction.getRequiredLevel() > playerLevel) {
            event.setCancelled(true);

            // Send the player a configured message when the break is cancelled
            String message = restriction.getChatMessage();
            if (message != null && !message.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }

            // Send the player a configured actionbar message when the break is cancelled
            String actionbar = restriction.getActionbarMessage();
            if (actionbar != null && !actionbar.isEmpty()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
            }
        }
    }
}
