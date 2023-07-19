package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Bash extends SomSkill {

    public Bash(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getParameter(SkillParameterType.Reach);
        SomRay ray = SomRay.rayLocationEntity(playerData, radius, 0.5, playerData.getTargets(), true);
        for (SomEntity hitEntity : ray.getHitEntities()) {
            Damage.makeDamage(playerData, hitEntity, DamageEffect.None, DamageOrigin.ATK, getParameter(SkillParameterType.Damage));
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        particle.widthLine(playerData.getViewers(), ray.getHitPosition(), 0, 2);
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
