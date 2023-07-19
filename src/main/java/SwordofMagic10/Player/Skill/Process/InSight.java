package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class InSight extends SomSkill {
    public InSight(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        playerData.addEffect(effect);
        SomParticle particle = new SomParticle(Color.AQUA);
        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
