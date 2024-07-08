package SwordofMagic10.Player;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Tutorial {

    public static final Location SpawnLocation = new Location(SomCore.World, -17.5, 66, 55.5, 180, 0);
    public static final Location GateLocation = new Location(SomCore.World, -17.5, 68, 35.5, 180, 0);
    public static final Location NextLocation = new Location(SomCore.World, 154.5, -25, 79.5, 90, 0);
    public static void run() {
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, null);
        new BukkitRunnable() {
            double i = 0;
            @Override
            public void run() {
                for (int j = 0; j < 4; j++) {
                    double x = Math.cos(i) * 2;
                    double z = Math.sin(i) * 2;
                    Location[] locations = {
                            GateLocation.clone().add(x, 0, z),
                            GateLocation.clone().add(-x, 0, -z),
                    };
                    particle.setVector(SomParticle.VectorUp);
                    particle.setSpeed(0.15f);
                    for (Location loc : locations) {
                        particle.spawn(SomEntity.nearPlayer(PlayerData.getPlayerListNonAFK(), GateLocation, 32), loc);
                    }
                    i += 0.05;
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 1);
    }

    public static void Gate(PlayerData playerData) {
        if (playerData.getLocation().distance(GateLocation) < 2) {
            if (playerData.getItemInventory().has(ItemDataLoader.getItemData("アライネへの招待状"), 1)) {
                playerData.teleport(NextLocation);
            } else {
                playerData.sendMessage("§e案内人§aから§eアライネへの招待状§aを受け取ってください", SomSound.Nope);
            }
        }
    }
}
