package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.Collection;

public class Debali extends EnemyBoss{
    private final Debali debali;
    public Debali(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        debali = this;
    }

    private final int Radius = 300;

    @Override
    public void tick(){
        areaOfSlow--;
        areaOfDefenceDown--;
        areaOfStun--;

        if (areaOfSlow <= 0) AreaOfSlow();
        if (areaOfDefenceDown <= 0 && getHealthPercent() <= 0.5) AreaOfDefenceDown();
        if (areaOfStun <= 0 && getHealthPercent() <= 0.2) AreaOfStun();
    }

    private int areaOfSlow = 0;
    public void AreaOfSlow(){
        areaOfSlow = 30;
        SomEffect effect = new SomEffect("DebaliSlow", "スロー", false, 10).setMultiply(StatusType.Movement, -0.66);

        for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)){
            entity.addEffect(effect, debali);
        }
    }

    private int areaOfDefenceDown = 0;
    public void AreaOfDefenceDown(){
        areaOfDefenceDown = 30;
        SomEffect effect = new SomEffect("DebaliDefenceDown", "防御低下", false, 20).setMultiply(StatusType.DEF, -0.33).setMultiply(StatusType.MDF, -0.33);

        for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)){
            entity.addEffect(effect, debali);
        }
    }

    private int areaOfStun = 0;
    public void AreaOfStun(){
        areaOfStun = 30;
        SomEffect effect = new SomEffect("DebaliStun", "スタン", false, 3).setStun(true);

        for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)){
            entity.addEffect(effect, debali);
        }
    }
}
