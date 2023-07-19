package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Ritirarsi extends SomSkill {

    public Ritirarsi(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double damage = getDamage();
        CustomLocation center = playerData.getLocation();
        for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.ATK, damage);
        }
        SomParticle particle = new SomParticle(Particle.LAVA);
        particle.circleFill(playerData.getViewers(), center, radius);
        playerData.setVelocity(playerData.getDirection().normalize().multiply(-1).setY(0.5));
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
