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
import org.bukkit.Particle;

public class ShieldPush extends SomSkill {

    public ShieldPush(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, getDamage());
            ray.getHitEntity().addEffect(new SomEffect(this, false).setStun(true));
        }
        SomParticle particle = new SomParticle(Particle.CRIT);
        particle.randomLocation(playerData.getViewers(), ray.getHitPosition(), 1, 10);
        SomSound.Bash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
