package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class TripBurst extends SomSkill {
    public TripBurst(PlayerData playerData) {
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
        double reach = getReach();
        double count = getCount();
        double damage = getDamage() / count;
        double headDamage = getHeadDamage() / count;
        SomParticle particle = new SomParticle(Particle.CRIT);
        for (int i = 0; i < count; i++) {
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 0.1, playerData.getTargets(), false);
            if (ray.isHitEntity()) {
                Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, headDamage));
            }
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
            SomTask.wait(100);
        }
        trigger = false;
        return null;
    }
}
