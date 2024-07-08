package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Smite extends SomSkill {

    public Smite(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double reach = getReach();
        double radius = getRadius();
        double damage = getDamage();
        CustomLocation center = playerData.getLocation().frontHorizon(reach).addY(0.1);
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK, playerData);
        particle.circleFill(playerData.getViewers(), center, radius);
        particle.widthLine(playerData.getViewers(), playerData.getEyeLocation(), reach, 2, 1);
        particle2.widthLine(playerData.getViewers(), playerData.getEyeLocation(), reach, 2, 1);
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, entity, DamageEffect.Holy, DamageOrigin.ATK, damage);
            SomTask.wait(50);
        }
        return null;
    }
}
