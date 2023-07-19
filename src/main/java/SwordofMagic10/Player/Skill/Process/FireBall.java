package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class FireBall extends SomSkill {

    public FireBall(PlayerData playerData) {
        super(playerData);

    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        CustomLocation center = ray.getOriginPosition();
        double radius = getRadius();
        double damage = getDamage();
        center.add(center.getDirection().multiply(radius));
        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, victim, DamageEffect.Fire, DamageOrigin.MAT, damage);
            SomTask.wait(50);
        }
        SomParticle particle = new SomParticle(Particle.FLAME);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        particle.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
        SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
