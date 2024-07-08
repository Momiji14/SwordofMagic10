package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;


public class Yoake extends EnemyBoss{
    private final Yoake yoake;
    public Yoake(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        yoake = this;
    }
    private final int Radius = 300;

    @Override
    public void tick(){
        starStrike--;
        laserAttack--;

        if (starStrike <= 0) StarStrike();
        if (laserAttack <= 0 && getHealthPercent() <= 0.5) LaserAttack();
        if (areaInArea && getHealthPercent() <= 0.2) AreaInArea();
    }

    private int starStrike = 0;
    public void StarStrike(){
        starStrike = 30;
        final SomParticle predictParticle = new SomParticle(Color.RED, yoake);
        final SomParticle strikeParticle = new SomParticle(Particle.SNOWFLAKE, yoake);
        final CustomLocation firstDeg = getEyeLocation();
        firstDeg.setPitch(0);

        predictParticle.line(getViewers(Radius), firstDeg, Radius);
        SomTask.delay(()->{
            final int perTick = 10;
            AtomicInteger count = new AtomicInteger();
            DurationSkill.Duration durationSkill = new DurationSkill.Duration(yoake, perTick, perTick*3);
            durationSkill.setRunnable(()->{
                SomRay rayPlus = SomRay.rayLocationEntity(getTargets(), firstDeg.clone().addYaw(72*count.get()), Radius, 1, true);
                strikeParticle.line(getViewers(Radius), firstDeg.clone().addYaw(72*count.get()),  rayPlus.getOriginPosition(), 1, 16);
                SomRay rayMinus = SomRay.rayLocationEntity(getTargets(), firstDeg.clone().addYaw(-72*count.get()), Radius, 1, true);
                strikeParticle.line(getViewers(Radius), firstDeg.clone().addYaw(-72*count.get()) ,  rayMinus.getOriginPosition(), 1, 16);

                for (SomEntity entity : rayPlus.getHitEntities()){
                    Damage.makeDamage(getEnemyData(), entity, DamageEffect.None, DamageOrigin.MAT, 0.5);
                }
                for (SomEntity entity : rayMinus.getHitEntities()){
                    Damage.makeDamage(getEnemyData(), entity, DamageEffect.None, DamageOrigin.MAT, 0.5);
                }

                count.getAndIncrement();
            });
            durationSkill.run();
        },20);
    }

    private int laserAttack = 0;
    public void LaserAttack(){
        laserAttack = 20;

        final SomParticle predictParticle = new SomParticle(Color.RED, yoake);
        final SomParticle strikeParticle = new SomParticle(Particle.SNOWFLAKE, yoake);
        final CustomLocation firstDeg = getEyeLocation();
        firstDeg.setPitch(0);

        predictParticle.line(getViewers(Radius), firstDeg, Radius, 7, 64);
        SomTask.delay(()->{
            SomRay ray = SomRay.rayLocationEntity(getTargets(), firstDeg, Radius, 3, true);
            strikeParticle.line(getViewers(Radius), getEyeLocation(), ray.getOriginPosition(), 7, 64);
            //ダメージ判定
            for (SomEntity entity : ray.getHitEntities()){
                Damage.makeDamage(getEnemyData(), entity, DamageEffect.None, DamageOrigin.MAT, 1);
            }
        },20);
    }

    private boolean areaInArea = true;
    public void AreaInArea(){
        areaInArea = false;

        final int perTick = 10;
        new BukkitRunnable(){
            final int minRadius = 10;
            final int maxRadius = 20;
            final SomParticle particle = new SomParticle(Color.RED, yoake);

            @Override
            public void run(){
                if (isInvalid()) this.cancel();

                CustomLocation location = getLocation().addY(0.2);
                particle.circle(getViewers(), location, maxRadius);
                particle.circle(getViewers(), location, minRadius);
                //ダメージ処理
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, maxRadius)) {
                    double distance = location.distance(entity.getLocation());
                    //該当範囲にいるプレイヤーのみ
                    if (minRadius < distance) {
                        Damage.makeDamage(yoake, entity, DamageEffect.None, DamageOrigin.MAT, 0.25);
                    }
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),0, perTick);
    }
}
