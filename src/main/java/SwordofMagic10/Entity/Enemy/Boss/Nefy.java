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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.SomCore.Log;

public class Nefy extends EnemyBoss{
    public Nefy(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        nefy = this;
    }

    Nefy nefy;
    //radius = 30
    private final CustomLocation pivot = new CustomLocation(SomCore.World, 3494.5, -37 , 494.5);

    private double lastHealthPercent = 1;
    @Override
    public void tick() {
        double currentHealthPercent = getHealthPercent();

        flowerChain--;
        healingFlower--;

        if (fieldGimmick) FieldGimmick();
        if (flowerChain <= 0 && !isChaining && !isHostage) FlowersChain();

        if (currentHealthPercent < 0.9 && 0.9 < lastHealthPercent){
            sendBossMessage("§c植物達の凶暴さが増してきました");
            flowerChainCT = 0;
        }
        if (currentHealthPercent < 0.55 && 0.55 < lastHealthPercent) {
            for (SomEntity entity : getViewers()){
                entity.sendMessage("§c植物達の凶暴さが収まりました", SomSound.Tick);
            }
            flowerChainCT = 10;
        }
        if (currentHealthPercent < 0.45 && 0.45 < lastHealthPercent) {
            sendBossMessage("§c植物達がネフィに助け舟を出します");
        }
        if (healingFlower <= 0 && getHealthPercent() <= 0.45) HealingFlower();
        if (hostage && getHealthPercent() <= 0.25) Hostage();


        lastHealthPercent = currentHealthPercent;
    }

    private final List<CustomLocation> flowerList = new ArrayList<>();

    final double flowerRadius = 4;
    final double flowerHeight = 15;
    private boolean isChaining = false;
    private boolean isHostage = false;

    private boolean fieldGimmick = true;
    public void FieldGimmick(){
        sendBossMessage("§c「今すぐ立ち去ってくれ！」");
        fieldGimmick = false;

        changeFlower();

        new BukkitRunnable(){
            final SomParticle flowerParticle = new SomParticle(Particle.SNEEZE, nefy).setVectorDown().setRandomSpeed(2f);

            @Override
            public void run() {
                if (nefy.isInvalid()) this.cancel();

                for (SomEntity entity : getTargets()){
                    CustomLocation entityLocation = entity.getLocation();
                    double distance = pivot.distance(entityLocation);

                    if ( 30-flowerRadius < distance && distance < 60 && !isHostage) Damage.makeDamage(nefy, entity, DamageEffect.None, DamageOrigin.MAT, 4);

                    for (CustomLocation location : flowerList){
                        double fromFlowerDistance = location.distanceXZ(entityLocation);
                        flowerParticle.circle(getViewers(), location.clone().addY(flowerHeight), flowerRadius, 32);
                        if (fromFlowerDistance < flowerRadius && !isHostage) Damage.makeDamage(nefy, entity, DamageEffect.None, DamageOrigin.MAT, 4);
                    }
                }
            }

        }.runTaskTimerAsynchronously(SomCore.plugin(), 40, 10);
    }

    private final int[] flowerAmount = {3, 5, 7, 9};
    public void changeFlower(){
        //ランダム Θ(radian) + r(distance) 生成
        double randomTheta = randomDouble(0, Math.PI*2);
        double R = 30;
        flowerList.clear();
        for (int i = 0; i < flowerAmount[getDifficulty().index()]; i++){
            //ランダムなロケーションを制定
            CustomLocation randomLocation = pivot.clone();
            double T = randomTheta+(360f/flowerAmount[getDifficulty().index()] * i);
            randomLocation.addXZ(Math.cos(T) * R, Math.sin(T) * R);

            flowerList.add(randomLocation);
        }
    }

    private int flowerChainCT = 10;
    private int flowerChain = 3;
    public void FlowersChain(){
        flowerChain = flowerChainCT;
        isChaining = true;

        SomTask.run(()->{
            final SomParticle cherryParticle = new SomParticle(Particle.END_ROD, nefy).setVectorDown().setRandomSpeed(2f);
            final SomParticle flowerParticle = new SomParticle(Color.fromRGB(255, 192, 203), nefy);

            int listSize = flowerList.size();
            if (listSize <= 0) return;
            int randomInt = randomInt(0, listSize);
            CustomLocation randomFlower = flowerList.get(randomInt);

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(nefy, 10, 10*5);
            durationSkill.setRunnable(()-> cherryParticle.circle(getViewers(), randomFlower.clone().addY(flowerHeight), flowerRadius+0.1));
            durationSkill.setRunnableEnd(()->{
                for (CustomLocation location : flowerList){
                    if (location.equals(randomFlower)) continue;
                    SomTask.run(()->{
                        for (int i = (int) flowerRadius; i < flowerHeight; i+=flowerRadius){
                            int finalI = i;
                            SomTask.run(()->{
                                SomRay ray = SomRay.rayLocationEntity(getTargets(), location.clone().addY(finalI), 52, flowerRadius*0.9, true);
                                if (ray.isHitEntity()){
                                    for (SomEntity entity : ray.getHitEntities()){
                                        Damage.makeDamage(nefy, entity, DamageEffect.None, DamageOrigin.MAT, 2);
                                    }
                                }
                                flowerParticle.line(getViewers(), randomFlower.clone().addY(finalI), location.clone().addY(finalI), flowerRadius*2);
                            });
                        }
                    });
                }
                changeFlower();
                isChaining = false;
            });
            durationSkill.run();
        });

    }

    private int healingFlower = 0;
    private final double[] healAmount = {0.05, 0.075, 0.1, 0.125};
    //とったら回復する花
    public void HealingFlower(){
        healingFlower = 30;

        final int radius = 5;
        //ランダムなロケーションを制定
        CustomLocation randomLocation = pivot.clone();
        //ランダム Θ(radian) + r(distance) 生成
        double R = 30-flowerRadius-radius;
        double randomTheta = randomDouble(0, Math.PI*2);
        randomLocation.addXZ(Math.cos(randomTheta) * R, Math.sin(randomTheta) * R);

        Collection<SomEntity> list = getTargets();
        list.add(nefy);

        new BukkitRunnable(){
            final SomParticle cherryParticle = new SomParticle(Particle.END_ROD, nefy);
            final SomParticle flowerParticle = new SomParticle(Color.fromRGB(255, 192, 203), nefy);

            @Override
            public void run() {
                if (nefy.isInvalid()) this.cancel();
                nefy.setTargetOverride(randomLocation);

                flowerParticle.circle(getViewers(), randomLocation, radius);
                cherryParticle.circle(getViewers(), randomLocation, radius);
                for (SomEntity entity : SomEntity.nearSomEntity(list, randomLocation, radius)){
                    entity.addHealth(entity.getMaxHealth() * healAmount[getDifficulty().index()]);
                    nefy.setTargetOverride(null);
                    this.cancel();
                    break;
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 20, 10);

    }

    //人?質
    private boolean hostage = true;
    private final int[] waitTick = {140, 120, 100, 80};
    public void Hostage(){
        sendBossSkillMessage("§c「私の弟たちをこれ以上傷つけるな！」");
        hostage = false;
        isHostage = true;
        for (SomEntity entity : getViewers()){
            entity.sendMessage("§c攻撃が来ます。どうにかしてください", SomSound.Tick);
        }
        SomTask.delay(()->{
            Collection<SomEntity> list = getTargets();
            for (SomEntity entity : getTargets()){
                for (CustomLocation location : flowerList){
                    double fromFlowerDistance = location.distanceXZ(entity.getLocation());
                    if (fromFlowerDistance < flowerRadius){
                        list.remove(entity);
                        entity.sendMessage("§c「弟を盾に使うなんて卑怯だ！」",SomSound.Tick);
                    }
                }
            }
            for (SomEntity target : list){
                target.death();
                target.sendMessage("§cあなたはネフィの魔力に潰されました");
            }
            SomTask.delay(() -> isHostage = false,40);
        }, waitTick[getDifficulty().index()]);
    }
}
