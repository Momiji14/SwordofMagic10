package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Lethargy extends SomSkill {

    public Lethargy(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false);
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        CustomLocation center = ray.getOriginPosition();
        double radius = getRadius();
        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            victim.addEffect(effect);
        }
        SomParticle particle = new SomParticle(Color.GREEN);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        particle.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
        SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
