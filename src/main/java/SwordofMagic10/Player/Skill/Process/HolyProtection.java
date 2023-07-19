package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class HolyProtection extends SomSkill {

    public SomEffect Effect = new SomEffect("HolyProtection", "聖なる加護", true, 60);
    public HolyProtection(PlayerData playerData) {
        super(playerData);
        Effect.setDoubleData(0, getDamageResist());
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Particle.END_ROD);
        double radius = getParameter(SkillParameterType.Radius);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius, 6, 0, 0);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius/2, 6, 0, 0.5);
        return super.cast();
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.END_ROD);
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            ally.addEffect(Effect);
            particle.circleHeightTwin(playerData.getViewers(), ally.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(ally);
        }
        return null;
    }
}
