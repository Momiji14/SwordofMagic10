package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;

public abstract class BladeSkill extends SomSkill {
    public BladeSkill(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        if (playerData.hasEffect("DrawBlade")) {
            return true;
        } else {
            playerData.sendMessage(DrawBlade.Message);
            return false;
        }
    }
}
