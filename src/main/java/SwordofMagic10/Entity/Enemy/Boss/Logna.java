package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.Collection;

public class Logna extends EnemyData {
    public Logna(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        super(mobData, level, difficulty, location, viewers);
    }

    @Override
    public void tick() {
        waterRipples--;
        if (waterRipples <= 0) WaterRipples();
        if (whirlpool && getHealthPercent() <= 0.8) Whirlpool();
        if (drawn && getHealthPercent() <= 0.5) Drawn();
        if (tsunami && getHealthPercent() <= 0.2) Tsunami();
    }

    private int waterRipples = 20;
    public void WaterRipples() {
        waterRipples = 20;
    }

    private boolean whirlpool = true;
    public void Whirlpool() {
        whirlpool = false;
    }

    private boolean drawn = true;
    public void Drawn() {
        drawn = false;
    }

    private boolean tsunami = true;
    public void Tsunami() {
        tsunami = false;
    }
}
