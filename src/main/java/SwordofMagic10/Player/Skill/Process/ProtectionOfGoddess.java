package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomBlockParticle;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

public class ProtectionOfGoddess extends SomSkill {

    public ProtectionOfGoddess(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double damage = getDamage();
        new SomBlockParticle(Material.GOLD_BLOCK).bell(playerData.getViewers(), playerData.getEyeLocation().addY(2), 1);
        SomTask.delay(() -> {
            SomParticle particle4 = new SomParticle(Color.YELLOW, playerData);
            SomParticle particle5 = new SomParticle(Particle.FIREWORKS_SPARK, playerData).setSpeed(0.25f);
            particle4.circleHeightTwin(playerData.getViewers(), playerData.getLocation(), radius, radius, 10);
            particle5.randomVectorHalf(playerData.getViewers(), playerData.getLocation().addY(4), radius);
            SomSound.Bell.play(playerData.getViewers(), playerData.getSoundLocation());
            for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), radius)) {
                Damage.makeDamage(playerData, entity, DamageEffect.Holy, DamageOrigin.MAT, damage);
                SomTask.wait(50);
            }
        }, 10);
        return null;
    }
}
