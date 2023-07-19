package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class HellBreath extends SomSkill {
    public HellBreath(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double reach = getReach();
        double count = getCount();
        double damage = getDamage() / count;
        double wait = getDurationMillie()/count;
        SomParticle particle = new SomParticle(Particle.FLAME).setRandomVector().setRandomSpeed(0.1f);
        for (int i = 0; i < count; i++) {
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 0.15, playerData.getTargets(), true);
            for (SomEntity entity : ray.getHitEntities()) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, damage);
            }
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
            SomTask.wait(wait);
        }
        return null;
    }
}
