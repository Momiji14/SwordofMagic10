package SwordofMagic10.Entity;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomDisplayParticle;
import SwordofMagic10.DataBase.SkillDataLoader;
import SwordofMagic10.SomCore;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static SwordofMagic10.SomCore.Log;

public abstract class DurationSkill {
    public static int SkillTick = 10;
    public static double CounterSpellRadius = 0;

    public static void setup() {
        CounterSpellRadius = SkillDataLoader.getSkillData("CounterSpell").getRadius();
    }

    protected BukkitTask task;
    protected final SomEntity owner;
    protected Runnable runnable;
    protected Runnable runnableEnd;
    protected int tick;
    protected final SomDisplayParticle[] displayParticle;
    public DurationSkill(SomEntity owner, int tick, SomDisplayParticle... displayParticle) {
        this.owner = owner;
        this.tick = tick;
        this.displayParticle = displayParticle;
    }

    public void end() {
        task.cancel();
        if (runnableEnd != null) runnableEnd.run();
        if (displayParticle != null) {
            for (SomDisplayParticle particle : displayParticle) {
                if (particle != null) particle.stop();
            }
        }
    }

    public DurationSkill setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public DurationSkill setRunnableEnd(Runnable runnableEnd) {
        this.runnableEnd = runnableEnd;
        return this;
    }

    public DurationSkill setTick(int tick) {
        this.tick = tick;
        return this;
    }

    public static MagicCircle magicCircle(SomEntity owner, CustomLocation location, double radius, int tick, int duration) {
        return magicCircle(owner, location, radius, tick, duration, (SomDisplayParticle) null);
    }

    public static MagicCircle magicCircle(SomEntity owner, CustomLocation location, double radius, int tick, int duration, SomDisplayParticle... displayParticle) {
        return new MagicCircle(owner, location, radius, tick, duration, displayParticle);
    }

    public static class MagicCircle extends DurationSkill.Duration {
        protected CustomLocation location;
        protected double radius;

        public CustomLocation getLocation() {
            return location;
        }

        public void setLocation(CustomLocation location) {
            this.location = location;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        private MagicCircle(SomEntity owner, CustomLocation location, double radius, int tick, int duration, SomDisplayParticle... displayParticle) {
            super(owner, tick, duration, displayParticle);
            this.location = location;
            this.radius = radius;
        }

        public void run() {
            task = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    for (SomEntity entity : SomEntity.nearSomEntity(owner.getTargets(), getLocation(), radius+CounterSpellRadius)) {
                        if (entity.hasEffect("CounterSpell") && owner.getTargets().contains(entity.getEffect("CounterSpell").getOwner())) {
                            end();
                        }
                    }
                    if (!owner.isInvalid() && duration > timer) {
                        timer += tick;
                        runnable.run();
                    } else end();
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, tick);
        }
    }

    public static Duration duration(SomEntity owner, int tick, int duration) {
        return duration(owner, tick, duration, (SomDisplayParticle) null);
    }
    public static Duration duration(SomEntity owner, int tick, int duration, SomDisplayParticle... displayParticle) {
        return new Duration(owner, tick, duration, displayParticle);
    }
    public static class Duration extends DurationSkill {
        protected final int duration;
        public Duration(SomEntity owner, int tick, int duration, SomDisplayParticle... displayParticle) {
            super(owner, tick, displayParticle);
            this.duration = duration;
        }

        public void run() {
            task = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    if (!owner.isInvalid() && duration > timer) {
                        timer += tick;
                        runnable.run();
                    } else end();
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, tick);
        }
    }

    public static Count count(SomEntity owner, int tick, int count) {
        return count(owner, tick, count, (SomDisplayParticle) null);
    }
    public static Count count(SomEntity owner, int tick, int count, SomDisplayParticle... displayParticle) {
        return new Count(owner, tick, count, displayParticle);
    }
    public static class Count extends DurationSkill {
        protected final int count;
        public Count(SomEntity owner, int tick, int count, SomDisplayParticle... displayParticle) {
            super(owner, tick, displayParticle);
            this.count = count;
        }

        public void run() {
            task = new BukkitRunnable() {
                int current = 0;
                @Override
                public void run() {
                    if (!owner.isInvalid() && count > current) {
                        current++;
                        runnable.run();
                    } else end();
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, tick);
        }
    }
}
