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

public class ShieldBash extends SomSkill {

    public ShieldBash(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT);
        for (SomEntity entity : SomEntity.rectangleSomEntity(playerData.getTargets(), playerData.getLocation(), getReach(), 5)) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, getDamage());
            particle.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 10);
        }
        particle.rectangle(playerData.getViewers(), playerData.getLocation(), getReach(), 5);
        playerData.setVelocity(playerData.getDirection().normalize().setY(0.25));
        SomSound.Bash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
