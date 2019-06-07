package net.draycia.mcmmominingrestrictions;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private McMMOMiningRestrictions main;

    BlockBreakListener(McMMOMiningRestrictions main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // Ensure the player's data is loaded in, cancel block breaks if it isn't
        McMMOPlayer mmoPlayer = UserManager.getPlayer(event.getPlayer());
        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        // Check if the block being broken is registered in the config
        ConfigurationSection section = main.getConfig().getConfigurationSection("Restrictions." + event.getBlock().getType().name());
        if (section == null) {
            return;
        }

        // Get the required skill to break the block
        PrimarySkillType skillType = PrimarySkillType.getSkill(section.getString("Skill"));
        if (skillType == null) {
            return;
        }

        // Get the required skill and the player's skill level
        int requiredLevel = section.getInt("Level");
        int playerLevel = ExperienceAPI.getLevel(event.getPlayer(), skillType);

        // Prevent the block from being broken if the player's level isn't high enough
        if (requiredLevel > playerLevel) {
            event.setCancelled(true);

            // Send the player a configured message when the break is cancelled
            String message = section.getString("Message");
            if (message != null && !message.isEmpty()) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }

            // Send the player a configured actionbar message when the break is cancelled
            String actionbar = section.getString("Actionbar");
            if (actionbar != null && !actionbar.isEmpty()) {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
            }
        }
    }
}
