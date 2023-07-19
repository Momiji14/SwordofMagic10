package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Particle;

public class InstantBlade extends BladeSkill {
    public InstantBlade(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), true);
        CustomLocation from = playerData.getHipsLocation().clone();
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.SWEEP_ATTACK).setRandomSpeed(0.1f);
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());
        for (SomEntity entity : ray.getHitEntities()) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, getDamage());
        }
        playerData.teleport(to, playerData.getDirection().setY(0));
        particle.line(playerData.getViewers(), from, to);
        particle2.line(playerData.getViewers(), from, to);
        SomSound.Blade.play(playerData.getViewers(), to);
        return null;
    }
}
