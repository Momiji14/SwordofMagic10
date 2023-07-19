package SwordofMagic10.Component;

import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SomTask {

    public static BukkitTask run(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(SomCore.plugin(), runnable);
    }

    public static BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(SomCore.plugin(), runnable);
    }

    public static BukkitTask delay(Runnable runnable, int tick) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(SomCore.plugin(), runnable, tick);
    }

    public static BukkitTask syncDelay(Runnable runnable, int tick) {
        return Bukkit.getScheduler().runTaskLater(SomCore.plugin(), runnable, tick);
    }

    public static BukkitTask timer(Runnable runnable, int tick) {
        return timer(runnable, 0, tick);
    }

    public static BukkitTask timer(Runnable runnable, int delay, int tick) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(SomCore.plugin(), runnable, delay, tick);
    }

    public static BukkitTask syncTimer(Runnable runnable, int tick) {
        return syncTimer(runnable, 0, tick);
    }

    public static BukkitTask syncTimer(Runnable runnable, int delay, int tick) {
        return Bukkit.getScheduler().runTaskTimer(SomCore.plugin(), runnable, delay, tick);
    }

    public static void wait(int milli) {
        if (milli > 0) {
            try {
                Thread.sleep(milli);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void wait(double milli) {
        if (milli > 0) {
            try {
                Thread.sleep((long) Math.floor(milli));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
