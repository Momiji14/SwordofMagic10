package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;

public class PainBarrier extends SomSkill {

    public PainBarrier(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(SomEffect.List.Invincible.getEffect());
        SomEffect effect = new SomEffect(this, true);
        playerData.addEffect(effect);
        SomParticle particle = new SomParticle(Color.MAROON).setRandomVector().setSpeed(0.15f).setAmount(10);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getPlayerData().hasEffect(effect)) {
                    particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 5);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
