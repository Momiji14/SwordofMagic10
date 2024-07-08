package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class DiscernEvil extends SomSkill {

    public DiscernEvil(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        SomParticle particle = new SomParticle(Color.PURPLE, playerData);
        SomParticle particle2 = new SomParticle(Particle.SPELL_WITCH, playerData).setRandomVector().setSpeed(0.2f);
        if (ray.isHitEntity()) {
            particle.line(playerData.getViewers(), playerData.getEyeLocation(), ray.getHitPosition());
            particle2.spawn(playerData.getViewers(), ray.getHitPosition());
            for (SomEffect effect : ray.getHitEntity().getEffect().values()) {
                if (!effect.isBuff() && !effect.isExtend() && effect.getRank() != SomEffect.Rank.Impossible) {
                    effect.addTime((int) (Math.max(getParameter(SkillParameterType.DiscernEvil), effect.getTime() * getParameter(SkillParameterType.DiscernEvilMin))));
                    effect.setExtend(true);
                    playerData.sendMessage(ray.getHitEntity().getDisplayName() + "§aの§c" + effect.getDisplay() + "§aを§c延長§aしました");
                }
            }
        }
        return null;
    }
}
