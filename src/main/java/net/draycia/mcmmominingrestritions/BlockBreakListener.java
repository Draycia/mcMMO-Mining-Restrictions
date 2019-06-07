package net.draycia.mcmmominingrestritions;

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
    private McMMOMiningRestritions main;

    BlockBreakListener(McMMOMiningRestritions main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(event.getPlayer());

        if (mmoPlayer == null) {
            event.setCancelled(true);
            return;
        }

        ConfigurationSection section = main.getConfig().getConfigurationSection("Restrictions." + event.getBlock().getType().name());
        if (section == null) return;

        PrimarySkillType skillType = PrimarySkillType.getSkill(section.getString("Skill"));
        if (skillType == null) return;

        int requiredLevel = section.getInt("Level");
        int playerLevel = ExperienceAPI.getLevel(event.getPlayer(), skillType);

        if (requiredLevel > playerLevel) {
            event.setCancelled(true);

            String message = section.getString("Message");
            if (message != null && !message.isEmpty()) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }

            String actionbar = section.getString("Actionbar");
            if (actionbar != null && !actionbar.isEmpty()) {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
            }
        }
    }
}
