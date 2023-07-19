package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class TreNapalm extends SomSkill {

    public TreNapalm(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        int waitTick = getDurationTick()/getCount();
        double damage = getDamage() / getCount();
        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition();
        SomParticle particle = new SomParticle(Particle.FLAME).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.LAVA).setRandomVector().setRandomSpeed(1f);
        SomParticle particle3 = new SomParticle(Particle.CRIT);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), center, 0, 2);
        particle3.line(playerData.getViewers(), playerData.getHandLocation(), center, 0);
        particle.setTime(waitTick*50);
        particle2.setTime(waitTick*50);
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        DurationSkill.create(() -> {
            SomTask.run(() -> particle.circleFill(playerData.getViewers(), center, radius, 3));
            SomTask.run(() -> particle2.circleFill(playerData.getViewers(), center, radius, 3));
            SomSound.Flame.play(playerData.getViewers(), center);
            for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.ATK, damage);
                SomTask.wait(50);
            }
        }, playerData, center, radius, waitTick, getDurationTick());
        return null;
    }
}
