package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.DurationSkill.SkillTick;

public class IcePike extends SomSkill {

    public IcePike(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double count = getCount();
        double damage = getDamage()/count;
        double wait = getDurationMillie()/count;
        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition();
        SomBlockParticle displayParticle = new SomBlockParticle(Material.ICE);
        displayParticle.pillar(playerData.getViewers(), center, 1.5, 7, getDurationTick());
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK).setVectorDown().setRandomSpeed(1f);
        SomParticle particle2 = new SomParticle(Particle.END_ROD).setVectorUp().setRandomSpeed(1f);
        particle.setTime(SkillTick);
        particle2.setTime(SkillTick);
        SomTask.run(() -> {
            for (int i = 0; i < count; i++) {
                SomTask.run(() -> particle.circleFill(playerData.getViewers(), center.clone().addY(4), radius, 1));
                SomTask.run(() -> particle2.circleFill(playerData.getViewers(), center, radius, 1));
                SomSound.Ice.play(playerData.getViewers(), center);
                for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                    Damage.makeDamage(playerData, entity, DamageEffect.Ice, DamageOrigin.MAT, damage);
                    SomTask.wait(50);
                }
                SomTask.wait(wait);
            }
        });
        return null;
    }
}
