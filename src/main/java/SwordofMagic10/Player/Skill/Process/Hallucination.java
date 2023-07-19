package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Hallucination extends SomSkill {
    public Hallucination(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        playerData.addEffect(effect);
        SomParticle particle = new SomParticle(Color.BLACK);
        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
