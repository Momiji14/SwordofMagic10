package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

import static SwordofMagic10.Component.Function.randomInt;

public class Hensal extends EnemyBoss{
    private final Hensal hensal;
    public Hensal(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        hensal = this;
    }

    private final int Radius = 300;

    @Override
    public void tick(){

        areaOfSpeed--;
        areaOfConfusion--;

        if (youCanNotRegen) YouCanNotRegen();
        if (areaOfSpeed <= 0 && getHealthPercent() <= 0.5) AreaOfSpeed();
        if (areaOfConfusion <= 0 && getHealthPercent() <= 0.2) AreaOfConfusion();
    }

    private boolean youCanNotRegen = true;
    public void YouCanNotRegen(){
        youCanNotRegen = false;

        final int perTick = 100;
        new BukkitRunnable(){

            final SomEffect effect = new SomEffect("YouCanNotHealthRegen", "自動体力回復停止", false, 6).setMultiply(StatusType.HealthRegen, -0.999);

            @Override
            public void run(){
                if (isInvalid()) this.cancel();

                //ダメージ処理
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)) {
                    entity.addEffect(effect, hensal);
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),0, perTick);
    }

    private int areaOfSpeed = 0;
    public void AreaOfSpeed(){
        areaOfSpeed = 30;

        final SomEffect effect = new SomEffect("MaximumSpeed", "制御不能", true, 20).setMultiply(StatusType.Movement, 3);

        for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)) {
            entity.addEffect(effect, hensal);
        }

    }

    private int areaOfConfusion = 0;
    public void AreaOfConfusion(){
        areaOfConfusion = 5;

        for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), getLocation(), Radius)) {
            if (entity instanceof PlayerData){
                CustomLocation location = entity.getLocation();
                int randomYaw =  randomInt(-90, 90);
                int randomPitch =  randomInt(-180, 180);
                location.setYaw(randomYaw);
                location.setPitch(randomPitch);
                SomTask.sync(()-> ((PlayerData) entity).teleport(location));
            }
        }
    }
}
