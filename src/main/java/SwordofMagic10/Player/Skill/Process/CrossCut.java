package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class CrossCut extends SomSkill {

    public CrossCut(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        for (int i = 0; i < 2; i++) {
            for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), getAngle(), getReach())) {
                Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, getDamage()/2);
            }
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        SomParticle particle2 = new SomParticle(Particle.CRIT);
        particle.widthLine(playerData.getViewers(), playerData.getHipsLocation(), getReach()/2, 1);
        particle2.fanShaped(playerData.getViewers(), playerData.getLocation(), getReach(), getAngle());
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
