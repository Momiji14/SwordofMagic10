package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class Retaliation extends SomSkill {

    public Retaliation(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double reach = getReach();
        double radius = getRadius();
        double damage = getDamage();
        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), reach, true);
        CustomLocation center = ray.getOriginPosition();
        SomParticle particle = new SomParticle(Color.YELLOW);
        SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK);
        particle.line(playerData.getViewers(), center, center.clone().addY(7), radius);
        particle2.line(playerData.getViewers(), center, center.clone().addY(7), radius);
        for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, entity, DamageEffect.Holy, DamageOrigin.MAT, damage);
            SomTask.wait(50);
        }
        SomSound.Shine.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
