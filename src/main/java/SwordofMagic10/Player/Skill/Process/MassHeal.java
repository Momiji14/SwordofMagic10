package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class MassHeal extends SomSkill {

    public MassHeal(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.GREEN);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        return super.cast();
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.VILLAGER_HAPPY);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            Damage.makeHeal(playerData, ally, playerData.getSPT(), getHeal());
            particle.circleHeightTwin(playerData.getViewers(), ally.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(ally);
        }
        return null;
    }
}
