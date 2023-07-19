package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

public class CounterBlade extends BladeSkill {

    public static SomEffect Effect = new SomEffect("CounterBlade", "反撃刀", true, 30, 3);
    public CounterBlade(PlayerData playerData) {
        super(playerData);
    }

    private BukkitTask task;
    @Override
    public String active() {
        playerData.addEffect(Effect.setTime(getDuration()).setStack(getCount()));
        SomParticle particle = new SomParticle(Particle.SOUL_FIRE_FLAME);
        SomSound.Blade.play(playerData);
        task = SomTask.timer(() -> {
            if (playerData.hasEffect(Effect)) {
                particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
            } else {
                task.cancel();
            }
        }, 0, 10);
        return null;
    }
}
