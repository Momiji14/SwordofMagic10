package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.*;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.SomCore.Log;

public class Froal extends EnemyBoss{

    private final Froal froal;
    public Froal(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        froal = this;
    }

    //    Radius = 40くらい
    private final CustomLocation pivot = new CustomLocation(SomCore.World, 1500.5, -59 , 500.5);

    @Override
    public void tick() {
        firePillar--;
        if (!lavaGround) lavaGroundCT--;

        if (firePillar <= 0) FirePillar();
        if (!lavaGround && lavaGroundCT <= 0) LavaGround();

        if (fireOfEnthusiasm && getHealthPercent() <= 0.9) FireOfEnthusiasm();
        if (lavaGround && getHealthPercent() <= 0.5) LavaGround();
        if (solarFlare && getHealthPercent() <= 0.3) SolarFlare();
    }



    private int firePillar = 20;
    private final int[] firePillarRadius = {5, 6, 7, 10};
    private final int[] firePillarDuration = {5, 6, 7, 10};//秒
    private final int[] firePillarPredictWait = {1000, 1000, 1000, 1000};
    public void FirePillar(){
        firePillar = 20;

        SomTask.run(() -> {
            int skillRadius = firePillarRadius[getDifficulty().index()];
            int skillDuration = firePillarDuration[getDifficulty().index()];
            int predictWait = firePillarPredictWait[getDifficulty().index()];

            //パーティクル指定
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, froal);
            final SomParticle damageCircleParticle = new SomParticle(Particle.LAVA, froal);


            final List<CustomLocation> locations = new ArrayList<>();
            //座標取得
            for (PlayerData viewer: getViewers(Radius)){
                if (viewer.isDeath()) continue;
                locations.add(viewer.getLocation());
                //予測円
                predictCircleParticle.circle(getViewers(Radius), viewer.getLocation(), skillRadius, 16);
            }
            SomTask.wait(predictWait);
            //該当座標に
            for (CustomLocation location : locations){
                location.setY(-59);
                //魔法陣生成
                DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(this, location, skillRadius, 10, skillDuration*20);
                magicCircle.setRunnable(() -> {
                    SomTask.run(() -> {
                        damageCircleParticle.circleFill(getViewers(), location, skillRadius, 8);
                        predictCircleParticle.circle(getViewers(), location, skillRadius, 16);
                    });
                    SomSound.Fire.play(getViewers(), location);
                    for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, skillRadius)) {
                        Damage.makeDamage(this, entity, DamageEffect.Fire, DamageOrigin.MAT, 1);
                        SomTask.wait(50);
                    }
                });
                magicCircle.run();
            }
        });

    }

    private boolean fireOfEnthusiasm = true;
    private final String[] fireOfEnthusiasmMessage = {"§c「諦めることはなかった」", "§c「信念を捨てた」", "§c「信条を恨んだ」"};
    private final int[] fireOfEnthusiasmDeg = {1, 2, 4, 6};//perTick(Speed)
    private final int[] fireOfEnthusiasmDuration = {10, 12, 13, 15};//秒

    public void FireOfEnthusiasm(){
        fireOfEnthusiasm = false;
        sendBossSkillMessage(fireOfEnthusiasmMessage);

        SomTask.delay(()->{
            final SomParticle predictParticle = new SomParticle(Color.RED, froal);
            final SomParticle fireParticle = new SomParticle(Particle.SOUL_FIRE_FLAME, froal);

            int deg = (int) (getEyeLocation().getYaw() % 360);

            //Easyには予測線をつける
            if (getDifficulty() == DungeonDifficulty.Easy){
                for (int i = 0; i < 4; i++){
                    for (int j = 0; j < 3; j++){
                        CustomLocation location = getEyeLocation();
                        location.setPitch(0);
                        int degForRay = (deg + (120 * j)) - 180;
                        location.setYaw(degForRay);
                        predictParticle.line(getViewers(Radius), location, 64, 1.5, 4);
                    }
                    SomTask.wait(250);
                }
            }

            for (int i = 0; i < fireOfEnthusiasmDuration[getDifficulty().index()]*10; i++){
                if (isInvalid()) return;
                //座標取得
                CustomLocation location = getEyeLocation();
                location.setPitch(0);

                for (int j = 0; j < 3; j++){
                    int degForRay = (deg + (120 * j)) - 180;
                    location.setYaw(degForRay);
                    SomRay ray = SomRay.rayLocationEntity(getTargets(), location, 64, 0.5, true);
                    fireParticle.line(getViewers(Radius), location, 64, 1.5, 5);
                    //ダメージ判定
                    for (SomEntity entity : ray.getHitEntities()){
                        Damage.makeDamage(getEnemyData(), entity, DamageEffect.Fire, DamageOrigin.MAT, 4);
                        SomSound.Flame.play(entity);
                    }
                }
                //角度追加
                deg += fireOfEnthusiasmDeg[getDifficulty().index()]*2;
                SomTask.wait(100);
            }
        },30);
    }



    private boolean lavaGround = true;
    private int lavaGroundCT = 20;
    private final int[] lavaGroundPredictWait = {8, 6, 4, 2};//0,25 * n

    private final String[] lavaGroundMessage = {"§c「熱量を増す地面」", "§c「熱に殺された人々」", "§c「太陽が文明を滅ぼす」"};

    public void LavaGround(){
        if (lavaGround) sendBossSkillMessage(lavaGroundMessage);
        lavaGround = false;
        lavaGroundCT = 20;

        SomTask.run(()->{
            int radius = 32;
            final SomParticle predictParticle = new SomParticle(Particle.LAVA, froal);

            //予測パーティクル
            for (int i = 0; i < lavaGroundPredictWait[getDifficulty().index()]; i++){
                if (isInvalid()) return;
                predictParticle.circleFill(getViewers(), pivot, radius, 1);
                for (SomEntity entity : getViewers(Radius)) {
                    SomSound.Lava.play(entity);
                }
                SomTask.wait(250);
            }
            //ダメージ判定
            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), pivot, radius)) {
                if (entity.getLivingEntity().isOnGround()) {
                    if (getDifficulty() == DungeonDifficulty.Easy) Damage.makeDamage(this, entity, DamageEffect.Fire, DamageOrigin.MAT, 4);
                    else Damage.makeDamage(this, entity, DamageEffect.Fire, DamageOrigin.MAT, 10);
                    SomSound.Lava.play(entity);
                }
            }
        });
    }



    private boolean solarFlare = true;
    private final String[] solarFlareMessage = {"§c「生き残らなければいけない」", "§c「信念に殺された人々」", "§c「信条が文明を滅ぼす」"};
    private final int[] solarFlareAmount = {6, 8, 10, 12};
    private final int[] solarFlareDuration = {20, 18, 16, 15};//秒
    public void SolarFlare(){
        solarFlare = false;
        sendBossSkillMessage(solarFlareMessage);

        SomTask.run(()->{
            double radius = 0.5;
            final SomParticle FireParticle = new SomParticle(Particle.LANDING_LAVA, froal);
            SomEffect effect = new SomEffect("SolarFlare", "歪な熱意", false, solarFlareDuration[getDifficulty().index()]).setRank(SomEffect.Rank.Impossible);
            HashMap<CustomLocation, Boolean> flaresCondition = new HashMap<>();

            for (PlayerData entity : getViewers(Radius)){
                entity.sendMessage("§c§n熱量§aを抑えてください", SomSound.Tick);
                entity.addEffect(effect, froal);
            }

            Log(String.valueOf(getTargets().size()));
            for (int i = 0; i < solarFlareAmount[getDifficulty().index()]*getTargets().size(); i++){
                //ランダムなロケーションを制定
                CustomLocation randomLocation = pivot.clone().addY(1);
                //ランダム Θ(radian) + r(distance) 生成
                double randomTheta = randomDouble(0, Math.PI*2);
                double randomR = randomDouble(0, 29);//radius
                randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

                flaresCondition.put(randomLocation, false);
            }

            final int perTick = 5;
            new BukkitRunnable(){
                int tick = 0;

                boolean isClear = false;

                @Override
                public void run(){
                    if (isInvalid()) this.cancel();
                    if (tick < solarFlareDuration[getDifficulty().index()]*20){

                        flaresCondition.forEach((location, flg)->{
                            if (flg) return;
                            FireParticle.sphere(getViewers(Radius), location, radius, 5);

                            for (SomEntity viewer : getViewers()) {
                                double distance = location.distance(viewer.getLocation());
                                if (distance < radius*4) {
                                    flaresCondition.replace(location, true);
                                }
                            }
                        });

                        //1つでもクリアされてなければ続行
                        isClear = true;
                        for (Boolean flg : flaresCondition.values()){
                            if (!flg){
                                isClear = false;
                                break;
                            }
                        }
                        if (isClear){
                            for (PlayerData entity : getViewers(Radius)){
                                entity.sendMessage("§c§n熱量§aを抑えました", SomSound.Tick);
                                entity.removeEffect("SolarFlare");
                            }
                            this.cancel();
                        }

                        tick+=perTick;
                    }else{
                        //Clearしてなかったら死
                        if (!isClear){
                            for (PlayerData entity : getViewers(Radius)){
                                entity.death("§c§n熱量§aに§4焦がれ§aました");
                                this.cancel();
                            }
                        }
                    }
                }

            }.runTaskTimerAsynchronously(SomCore.plugin(),1, perTick);
        });
    }

}
