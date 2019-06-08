package net.draycia.mcmmominingrestrictions.utilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class MMRRestriction {
    private PrimarySkillType requiredSkill;
    private int requiredLevel;
    private String chatMessage;
    private String actionbarMessage;

    public MMRRestriction(PrimarySkillType requiredSkill, int requiredLevel, String chatMessage, String actionbarMessage) {
        this.requiredSkill = requiredSkill;
        this.requiredLevel = requiredLevel;
        this.chatMessage = chatMessage;
        this.actionbarMessage = actionbarMessage;
    }

    public PrimarySkillType getRequiredSkill() {
        return requiredSkill;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getActionbarMessage() {
        return actionbarMessage;
    }
}
