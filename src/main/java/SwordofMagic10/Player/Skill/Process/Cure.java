package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import java.util.ArrayList;

public class Cure extends SomSkill {
    public Cure(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, new ArrayList<>(playerData.getAlliesNoMe()), false);
        SomEntity target;
        if (ray.isHitEntity()) {
            target = ray.getHitEntity();
        } else {
            target = playerData;
        }
        for (SomEffect effect : target.getEffect().values()) {
            if (!effect.isBuff() && effect.getRank() == SomEffect.Rank.Normal) {
                target.removeEffect(effect);
                break;
            }
        }
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, playerData);
        if (target != playerData) particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
        particle.circleHeightTwin(playerData.getViewers(), target.getLivingEntity(), 1, 2, 3);
        SomSound.Heal.play(playerData.getViewers(), target.getSoundLocation());
        return null;
    }
}
