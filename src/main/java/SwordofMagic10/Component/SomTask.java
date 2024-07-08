package SwordofMagic10.Component;

import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Predicate;

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

    public static BukkitTask timerPlayer(PlayerData playerData, Runnable runnable, int tick) {
        return timerPlayer(playerData, runnable, 0, tick);
    }

    public static BukkitTask timerPlayer(PlayerData playerData, Runnable runnable, Predicate<PlayerData> predicate, int tick) {
        return timerPlayer(playerData, runnable, predicate, 0, tick);
    }

    public static BukkitTask timerPlayer(PlayerData playerData, Runnable runnable, int delay, int tick) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.isOnline()) {
                    runnable.run();
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), delay, tick);
    }

    public static BukkitTask timerPlayer(PlayerData playerData, Runnable runnable, Predicate<PlayerData> predicate, int delay, int tick) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.isOnline() && predicate.test(playerData)) {
                    runnable.run();
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), delay, tick);
    }

    public static BukkitTask syncTimer(Runnable runnable, int tick) {
        return syncTimer(runnable, 0, tick);
    }

    public static BukkitTask syncTimer(Runnable runnable, int delay, int tick) {
        return Bukkit.getScheduler().runTaskTimer(SomCore.plugin(), runnable, delay, tick);
    }

    public static BukkitTask syncTimerPlayer(PlayerData playerData, Runnable runnable, int tick) {
        return syncTimerPlayer(playerData, runnable, 0, tick);
    }

    public static BukkitTask syncTimerPlayer(PlayerData playerData, Runnable runnable, int delay, int tick) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.isOnline()) {
                    runnable.run();
                } else this.cancel();
            }
        }.runTaskTimer(SomCore.plugin(), delay, tick);
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
