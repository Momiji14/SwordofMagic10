package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Revive extends SomSkill {
    public Revive(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.AQUA);
        particle.circle(playerData.getViewers(), playerData.getLocation(), getRadius());
        return super.cast();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.AQUA);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            ally.addEffect(effect);
            particle.circleHeightTwin(playerData.getViewers(), ally.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(ally);
        }
        return null;
    }
}
