package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

public class CounterBlade extends BladeSkill {

    public CounterBlade(PlayerData playerData) {
        super(playerData);
    }

    private BukkitTask task;
    @Override
    public String active() {
        SomEffect effect = new SomEffect(getId(), getDisplay(), true, getDuration(), getCount());
        playerData.addEffect(effect, playerData);
        SomParticle particle = new SomParticle(Particle.SOUL_FIRE_FLAME, playerData);
        SomSound.Blade.play(playerData);
        task = SomTask.timerPlayer(playerData, () -> {
            if (playerData.hasEffect(effect)) {
                particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
            } else {
                task.cancel();
            }
        }, 0, 10);
        return null;
    }
}
