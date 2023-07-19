package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Slithering extends SomSkill {

    public Slithering(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setToggle(true).setSilence(getId()).setStun(true);
        if (playerData.hasEffect(effect)) {
            playerData.removeEffect(effect);
        } else {
            playerData.addEffect(effect);
        }
        SomSound.Bash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
