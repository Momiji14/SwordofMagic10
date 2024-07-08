package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Meditation extends SomSkill {

    public Meditation(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addMana(playerData.getMaxMana()*0.3);
        SomParticle particle = new SomParticle(Color.AQUA, playerData);
        particle.circleHeightTwin(playerData.getViewers(), playerData.getLocation(), 1, 2, 2);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
