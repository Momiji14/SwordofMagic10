package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class PrizeBullet extends SomSkill {

    public PrizeBullet(PlayerData playerData) {
        super(playerData);
    }

    private boolean trigger = false;
    @Override
    public boolean cast() {
        if (!trigger) {
            trigger = true;
            SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
        }
        return super.cast();
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.55, playerData.getTargets(), true);
        for (SomEntity entity : ray.getHitEntities()) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, entity, getDamage(), getHeadDamage()));
        }
        SomParticle particle = new SomParticle(Particle.CRIT);
        SomParticle particle2 = new SomParticle(Particle.CRIT_MAGIC);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), 0.35);
        particle2.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), 0.35);
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        trigger = false;
        return null;
    }
}
