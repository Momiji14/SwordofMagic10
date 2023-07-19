package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomDisplayParticle;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.SomCore;
import org.bukkit.scheduler.BukkitRunnable;

public class DurationSkill {
    public static int SkillTick = 10;

    public static void create(Runnable runnable, SomEntity owner, CustomLocation location, double radius, int tick, int duration) {
        create(runnable, owner, location, radius, tick, duration, (SomDisplayParticle) null);
    }

    public static void create(Runnable runnable, SomEntity owner, CustomLocation location, double radius, int tick, int duration, SomDisplayParticle... displayParticle) {
        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                for (SomEntity entity : SomEntity.nearSomEntity(owner.getTargets(), location, radius)) {
                    if (entity.hasEffect("CounterSpell")) {
                        this.cancel();
                        if (displayParticle != null) {
                            for (SomDisplayParticle particle : displayParticle) {
                                particle.stop();
                            }
                        }
                    }
                }
                if (duration > timer) {
                    timer += tick;
                    runnable.run();
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, tick);
    }

}
