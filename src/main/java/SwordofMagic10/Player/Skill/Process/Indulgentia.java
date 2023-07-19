package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Indulgentia extends SomSkill {

    public Indulgentia(PlayerData playerData) {
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
        SomEffect effect = new SomEffect(this, true);
        double heal = getHeal() / getCount();
        SomParticle particle = new SomParticle(Particle.VILLAGER_HAPPY);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            ally.addEffect(effect);
            SomSound.Heal.play(ally);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (SomEntity ally : playerData.getAllies()) {
                    if (ally.hasEffect(effect) && ally.getEffect(effect.getId()).getOwner() == playerData) {
                        Damage.makeHeal(playerData, ally, playerData.getSPT(), heal);
                    }
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, getDurationTick()/getCount());
        return null;
    }
}
