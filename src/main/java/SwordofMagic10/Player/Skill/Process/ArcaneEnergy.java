package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class ArcaneEnergy extends SomSkill {

    public ArcaneEnergy(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.AQUA);
        double radius = getParameter(SkillParameterType.Radius);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius, 6, 0, 0);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius/2, 6, 0, 0.5);
        return super.cast();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.AQUA);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            ally.addEffect(effect.clone().setDoubleData(0, playerData.getMana()));
            particle.circleHeightTwin(playerData.getViewers(), ally.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(ally);
        }
        return null;
    }
}
