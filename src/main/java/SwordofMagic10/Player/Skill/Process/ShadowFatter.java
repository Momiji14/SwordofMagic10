package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class ShadowFatter extends SomSkill {

    public ShadowFatter(PlayerData playerData) {
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
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.Ice, DamageOrigin.ATK, headShot(playerData, ray, getDamage(), getHeadDamage()));
            ray.getHitEntity().addEffect(new SomEffect(this, false).setStun(true).setRank(SomEffect.Rank.High));
        }
        SomParticle particle = new SomParticle(Color.BLACK);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        trigger = false;
        return null;
    }
}
