package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;

public class FastForward extends SomSkill {
    public FastForward(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect.Toggle(this, true);

        if (playerData.hasEffect(effect)) {
            playerData.removeEffect(effect);
        } else {
            playerData.addEffect(effect, playerData);
        }

        SomParticle particle = new SomParticle(Color.GRAY, playerData).setRandomVector().setSpeed(0.15f).setAmount(10);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.hasEffect(effect) && playerData.isOnline()) {
                    particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 5);

        SomSound.TickTack.play(playerData, playerData.getSoundLocation());
        return null;
    }
}
