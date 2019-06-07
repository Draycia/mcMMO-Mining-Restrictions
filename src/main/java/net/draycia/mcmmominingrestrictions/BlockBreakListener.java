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

    public void loadProtections() {
        restrictions.clear();

        ConfigurationSection rootSection = main.getConfig().getConfigurationSection("Restrictions");
        for (String sectionKey : rootSection.getKeys(false)) {
            ConfigurationSection protectionSection = rootSection.getConfigurationSection(sectionKey);

            Material protectedMaterial = Material.getMaterial(sectionKey.toUpperCase());

            PrimarySkillType requiredSkill = PrimarySkillType.getSkill(protectionSection.getString("Skill"));
            int requiredLevel = protectionSection.getInt("Level");
            String chatMessage = protectionSection.getString("Message");
            String actionbarMessage = protectionSection.getString("Actionbar");

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
