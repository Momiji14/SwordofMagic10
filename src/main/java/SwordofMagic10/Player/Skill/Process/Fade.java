package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Fade extends SomSkill {
    public Fade(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(SomEffect.List.Invincible.getEffect());
        playerData.addEffect(new SomEffect(this, true));
        SomParticle particle = new SomParticle(Particle.END_ROD).setRandomVector().setSpeed(0.15f);
        particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
