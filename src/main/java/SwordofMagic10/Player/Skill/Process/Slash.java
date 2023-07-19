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

public class Slash extends SomSkill {

    public Slash(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, getDamage());
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        particle.widthLine(playerData.getViewers(), ray.getHitPosition(), 0, 1);
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
