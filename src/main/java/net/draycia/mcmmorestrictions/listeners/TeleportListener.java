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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public class TeleportListener implements Listener, MMRListener {
    private McMMORestrictions main;
    private HashMap<String, MMRRestriction> restrictions = new HashMap<>();

    private boolean enabled;

    public TeleportListener(McMMORestrictions main) {
        this.main = main;

        loadRestrictions();
    }

    @Override
    public void loadRestrictions() {
        enabled = main.getConfig().getBoolean("Restrictions.WorldEnter");

        // If this isn't enabled, don't even attempt to load from config
        if (!enabled) {
            return;
        }

        restrictions.clear();

        // Iterate through all block entries in the config
        ConfigurationSection rootSection = main.getConfig().getConfigurationSection("WorldEnter");
        for (String sectionKey : rootSection.getKeys(false)) {
            ConfigurationSection protectionSection = rootSection.getConfigurationSection(sectionKey);

            // Get the information about the restriction that's necessary
            PrimarySkillType requiredSkill = PrimarySkillType.getSkill(protectionSection.getString("Skill"));
            int requiredLevel = protectionSection.getInt("Level");
            String chatMessage = protectionSection.getString("Message");
            String actionbarMessage = protectionSection.getString("Actionbar");

            // Create a MMRRestriction and store it for the desired material
            // sectionKey is the world name
            restrictions.put(sectionKey, new MMRRestriction(requiredSkill, requiredLevel, chatMessage, actionbarMessage));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!enabled) {
            return;
        }

        Player player = event.getPlayer();

        if (player.hasPermission("mmr.bypass.itempickup")) {
            return;
        }

        // If the location or world are null, return
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return;
        }

        // Block isn't configured, don't protect it
        if (!restrictions.containsKey(event.getTo().getWorld().getName())) {
            return;
        }

        // Ensure the player's data is loaded in, cancel block breaks if it isn't
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        MMRRestriction restriction = restrictions.get(event.getTo().getWorld().getName());
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
