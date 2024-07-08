package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class Rampage  extends SomSkill {
    public Rampage(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particleAngry = new SomParticle(Particle.VILLAGER_ANGRY, playerData);

        playerData.addEffect(effect, playerData);
        SomSound.Angry.play(playerData.getViewers(), playerData.getSoundLocation());

        particleAngry.sphere(playerData.getViewers(), playerData.getLocation().addY(3), 0.3);

        return null;
    }
}
