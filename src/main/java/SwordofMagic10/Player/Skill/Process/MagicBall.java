package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class MagicBall extends SomSkill {

    public MagicBall(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.MAT, getDamage());
        }
        SomParticle particle = new SomParticle(Particle.CRIT_MAGIC, playerData);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Ball.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
