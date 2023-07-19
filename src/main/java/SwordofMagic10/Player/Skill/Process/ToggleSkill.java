package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;

public class ToggleSkill extends SomSkill {

    private final SomEffect effect;
    public ToggleSkill(PlayerData playerData, SomEffect effect) {
        super(playerData);
        this.effect = effect;
    }

    @Override
    public String active() {
        return null;
    }

    public void toggleEffect() {
        if (playerData.hasEffect(effect)) {
            playerData.removeEffect(effect);
        } else {
            playerData.addEffect(effect);
        }
    }
}
