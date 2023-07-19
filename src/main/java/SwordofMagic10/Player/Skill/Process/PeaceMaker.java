package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.Sound;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class PeaceMaker extends SomSkill {

    public PeaceMaker(PlayerData playerData) {
        super(playerData);
    }

    private boolean trigger = false;
    @Override
    public boolean cast() {
        if (!trigger) {
            trigger = true;
            SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
            SomSound.PeaceMaker.play(playerData.getViewers(), playerData.getSoundLocation());
        }
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH).setRandomVector().setRandomSpeed(0.1f);
        particle.spawn(playerData.getViewers(), playerData.getHandLocation());
        return super.cast();
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            SomEntity entity = ray.getHitEntity();
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, getDamage(), getHeadDamage()));
            SomParticle particle = new SomParticle(Particle.SPELL_WITCH).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle3 = new SomParticle(Particle.LAVA).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle4 = new SomParticle(Particle.EXPLOSION_NORMAL).setRandomVector().setRandomSpeed(0.5f);
            if (ray.isHeadShot(entity)) {
                particle.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle2.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle3.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle4.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                SomSound.Explode.play(playerData.getViewers(), playerData.getSoundLocation());
            }
            SomSound.Curse.play(playerData.getViewers(), playerData.getSoundLocation());
        }
        SomParticle particle = new SomParticle(Particle.CRIT);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        trigger = false;
        return null;
    }
}
