package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class DimensionCompression extends SomSkill {

    public DimensionCompression(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        CustomLocation center = ray.getOriginPosition();

        double radius = getRadius();
        double damage = getDamage();

        SomParticle particle = new SomParticle(Color.PURPLE, playerData);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        particle.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
        SomSound.Warp.play(playerData.getViewers(), playerData.getSoundLocation());

        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, victim, DamageEffect.None, DamageOrigin.MAT, damage);
            if (victim instanceof EnemyData && victim.isBoss()){
                SomTask.wait(20);
                continue;
            }
            victim.setVelocity(victim.getLocation().toLocationVector(center).normalize().setY(0.5));
            SomTask.wait(20);
        }

        return null;
    }
}
