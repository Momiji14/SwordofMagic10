package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class DivineMight extends SomSkill {

    public DivineMight(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.PURPLE);
        particle.circle(playerData.getViewers(), playerData.getLocation(), getRadius());
        return super.cast();
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Color.PURPLE);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            for (SomEffect effect : ally.getEffect().values()) {
                if (effect.isBuff() && !effect.isExtend()) {
                    effect.addTime((int) (Math.max(getParameter(SkillParameterType.DivineMightMin), effect.getTime() * getParameter(SkillParameterType.DivineMight))));
                    effect.setExtend(true);
                }
            }
            particle.circleHeightTwin(playerData.getViewers(), ally.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(ally);
        }
        return null;
    }
}
