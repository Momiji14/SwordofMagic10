package SwordofMagic10.Player.Skill.Process;

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

public class Zornhau extends SomSkill {

    public Zornhau(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), getReach(), getAngle())) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, getDamage());
            SomTask.wait(50);
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        particle.widthLine(playerData.getViewers(), playerData.getEyeLocation().frontHorizon(getReach()/2), 0, 1);
        particle.fanShaped(playerData.getViewers(), playerData.getLocation(), getReach(), getAngle());
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        ((Zucken) playerData.getSkillManager().getSkill("Zucken")).setReady();
        return null;
    }
}
