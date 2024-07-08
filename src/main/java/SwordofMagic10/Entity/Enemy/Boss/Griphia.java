package SwordofMagic10.Entity.Enemy.Boss;


import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MobDataLoader;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.Entity.DurationSkill.magicCircle;
import static SwordofMagic10.SomCore.Log;

public class Griphia extends EnemyBoss {
    private final Griphia griphia;
    private final int FieldRadius;
    public Griphia(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        griphia = this;
        FieldRadius = 60;
        dungeon.resetStartTime();
    }

    private final CustomLocation pivot = new CustomLocation(SomCore.World, 0.5, 29 , -2499.5);
    private void makeDamageGriphia(SomEntity entity, double multiply){
        double factor = 1+(FlameCount/10000.0);
        Damage.makeDamage(this, entity, DamageEffect.Fire, DamageOrigin.MAT, multiply*factor);
    }

    private int FlameCount = 0;
    private double lastHealthPercent = 1;

    @Override
    public void tick(){
        double currentHealthPercent = getHealthPercent();
        if (currentHealthPercent < 0.98 && 0.98 < lastHealthPercent) {
            SafeArea();
            sendBossMessage("§c§nグリフィアはいつものように侵入者を排除する");
        }
        if (currentHealthPercent < 0.94 && 0.94 < lastHealthPercent) {
            SealMemory();
        }
        if (currentHealthPercent < 0.90 && 0.90 < lastHealthPercent) {
            RedAwaken();
        }
        if (currentHealthPercent < 0.85 && 0.85 < lastHealthPercent) {
            ImprintedMemory();
        }
        if (currentHealthPercent < 0.80 && 0.80 < lastHealthPercent) {
            sendBossMessage("§c§n攻撃を凌いだことに驚嘆する");
            SealMemory();
            SafeArea();
        }

        if (currentHealthPercent < 0.70 && 0.70 < lastHealthPercent) {
            SaturationSpell();
            sendBossMessage("§c§n魔力に反応して、蝋燭に灯る弱った炎が小さく揺れる");
        }
        if (currentHealthPercent < 0.66 && 0.66 < lastHealthPercent) {
            sendBossMessage("§c§n記憶に激しい揺さぶりを掛けられている");
            SealMemory();
            SafeArea();
        }
        if (currentHealthPercent < 0.64 && 0.64 < lastHealthPercent) {
            SealMemory();
            SafeArea();
        }
        if (currentHealthPercent < 0.62 && 0.62 < lastHealthPercent) {
            SealMemory();
            SafeArea();
        }
        if (currentHealthPercent < 0.58 && 0.58 < lastHealthPercent) {
            sendBossMessage("§c§n周囲の魔力が顫動している");
            RedAwaken();
            SaturationSpell();
        }
        if (currentHealthPercent < 0.52 && 0.52 < lastHealthPercent) {
            sendBossMessage("§c§n焼き付いて離れない呪いを持っている");
            ImprintedMemory();
        }
        if (currentHealthPercent < 0.45 && 0.45 < lastHealthPercent) {
            SealMemory();
            SafeArea();
            RedAwaken();
        }
        if (currentHealthPercent < 0.40 && 0.40 < lastHealthPercent) {
            sendBossMessage("§c§n焼き付いて離れない呪いを待っている");
            SafeArea();
            ImprintedMemory();
        }

        if (currentHealthPercent < 0.25 && 0.25 < lastHealthPercent) {
            sendBossMessage("§c§n記憶が剥がれ落ち、焼け爛れようとする");
            SealMemory();
            SafeArea();
            RedAwaken();
        }
        if (currentHealthPercent < 0.20 && 0.20 < lastHealthPercent) {
            SealMemory();
            SafeArea();
            RedAwaken();
            SaturationSpell();
        }
        if (currentHealthPercent < 0.15 && 0.15 < lastHealthPercent) {
            SealMemory();
            SafeArea();
            RedAwaken();
            ImprintedMemory();
        }
        if (currentHealthPercent < 0.10 && 0.10 < lastHealthPercent) {
            sendBossMessage("§c§nとても、とても大事なものを忘れたまま");
            ImprintedMemory();
            RedAwaken();
            SaturationSpell();
        }

        lastHealthPercent = currentHealthPercent;
        if (fieldGimmick) FieldGimmick();

        simpleAttack--;
        overHeat--;
        stunAttack--;

        meteor--;
        flameOfDestiny--;
        canNotForgetFlame--;
        flamePillar--;
        loyalty--;

        if (overHeat <= 0) OverHeat();

        if (meteor <= 0 && getHealthPercent() <= 0.9) Meteor();
        if (flameOfDestiny <= 0 && 0.3 <= getHealthPercent() && getHealthPercent() <= 0.75) FlameOfDestiny();
        if (canNotForgetFlame <= 0 && 0.2 <= getHealthPercent() && getHealthPercent() <= 0.7) CanNotForgetFlame();
        if (flamePillar <= 0 && getHealthPercent() <= 0.7) FlamePillar();
        if (loyalty <= 0 && getHealthPercent() <= 0.35) Loyalty();

    }

    private static final CustomLocation gateEnterLocation = new CustomLocation(SomCore.World, -32.5, 29 , -2466.5);
    private static final CustomLocation gateExitLocation = new CustomLocation(SomCore.World, 0.5, 118 , -2499.5);
    private static final int gateRadius = 2;

    public static void Gate (PlayerData playerData){
        if (playerData.getLocation().distance(gateEnterLocation) < gateRadius && playerData.hasEffect("SaturationSpell")) playerData.teleport(gateExitLocation);
        if (playerData.getLocation().distance(gateExitLocation) < gateRadius ) playerData.teleport(gateEnterLocation);
    }

    private boolean fieldGimmick = true;
    public void FieldGimmick(){
        fieldGimmick = false;

        BossBar bossBar = Bukkit.createBossBar("炎の魔力 : "+FlameCount, BarColor.RED, BarStyle.SOLID);
        new BukkitRunnable(){

            final int stunDistance = 20;
            final int attackDistance = 10;

            @Override
            public void run(){
                FlameCount += 4;
                if (isInvalid()){
                    bossBar.removeAll();
                    bossBar.setVisible(false);
                    this.cancel();
                }

                for (PlayerData player: getViewers()){
                    bossBar.addPlayer(player.getPlayer());
                }
                bossBar.setTitle(String.format("§c炎の魔力 : "+FlameCount));

                double distance = getLocation().distance(getTarget().getLocation());
                if (stunDistance < distance && stunAttack <= 0){
                    StunAttack();
                }else if (distance < attackDistance && simpleAttack <= 0){
                    SimpleAttack();
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),1, 20);

        new BukkitRunnable(){
            private final SomParticle gateParticle = new SomParticle(Particle.FIREWORKS_SPARK, griphia).setVector(SomParticle.VectorUp).setSpeed(0.15f);
            double gateDeg = 0;

            @Override
            public void run(){
                if (isInvalid()) this.cancel();

                double x = Math.cos(gateDeg) * gateRadius;
                double z = Math.sin(gateDeg) * gateRadius;
                Location[] enterLocations = {
                        gateEnterLocation.clone().addXZ(x, z),
                        gateEnterLocation.clone().addXZ(-x, -z),
                };
                Location[] exitLocations = {
                        gateExitLocation.clone().add(x, 0, z),
                        gateExitLocation.clone().add(-x, 0, -z),
                };
                for (Location loc : enterLocations) {
                    gateParticle.spawn(getViewers(), loc);
                }
                for (Location loc : exitLocations) {
                    gateParticle.spawn(getViewers(), loc);
                }
                gateDeg += 0.2;
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),20, 1);
    }


    //////////////////////////////////////////////////

    private int simpleAttack = 10;
    public void SimpleAttack(){
        simpleAttack = 4;
        SomTask.run(()->{
            final SomParticle predictParticle = new SomParticle(Color.RED, griphia);
            final SomParticle flameParticle = new SomParticle(Particle.FLAME, griphia);
            final int radius = 5;

            CustomLocation location = getEyeLocation().clone();
            location.setPitch(0);
            location.lookLocation(getTarget().getEyeLocation());


            SomRay ray = SomRay.rayLocationBlock(location, radius, true);
            location = ray.getHitPosition().lower();

            predictParticle.circle(getViewers(), location, radius);
            SomTask.wait(1000);
            flameParticle.circle(getViewers(), location, radius);
            List<SomEntity> list = SomEntity.nearSomEntity(getTargets(), location, radius);
            if (!list.isEmpty()){
                for (SomEntity entity : list){
                    //Hit Attack
                    SomSound.Fire.play(entity);
                    makeDamageGriphia(entity, 1);
                }
            }else{
                //NotHit Attack
                FlameCount += 50;
            }
        });
    }

    private int overHeat = 30;
    public void OverHeat(){
        overHeat = 60;
        SomTask.run(()->{
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, griphia);
            final SomParticle damageCircleParticle = new SomParticle(Particle.LAVA, griphia);
            final int radius = 16;

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, 5,50);
            //予測
            durationSkill.setRunnable(()->{
                for (PlayerData viewer: getViewers(Radius)){
                    if (viewer.isDeath()) continue;
                    //予測円
                    predictCircleParticle.circle(getViewers(Radius), viewer.getLocation(), radius, 16);
                    predictCircleParticle.sphere(getViewers(), viewer.getLocation().addY(3), 0.5);
                    SomSound.Tick.play(viewer);
                }
            });
            //爆発
            durationSkill.setRunnableEnd(()->{
                for (PlayerData viewer: getViewers(Radius)){
                    if (viewer.isDeath()) continue;
                    List<SomEntity> list = SomEntity.nearSomEntity(getTargets(), viewer.getLocation(), radius);
                    for (SomEntity entity : list){
                        damageCircleParticle.circle(getViewers(Radius), viewer.getLocation(), radius, 16);
                        //OverHeat HIT list.size() ^ list.size()
                        SomSound.Fire.play(entity);
                        makeDamageGriphia(entity, list.size());
                    }
                }
            });
            durationSkill.run();
        });
    }

    private int stunAttack = 20;
    public void StunAttack(){
        stunAttack = 30;
        SomTask.run(()->{
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, griphia);
            final SomParticle purpleParticle = new SomParticle(Particle.SPELL_WITCH, griphia);
            final SomParticle damageCircleParticle = new SomParticle(Particle.LAVA, griphia);
            final SomEffect stunEffect = SomEffect.List.Stun.getEffect();
            final int radius = 10;

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, 5,50);
            //予測
            durationSkill.setRunnable(()->{
                for (PlayerData viewer: getViewers(Radius)){
                    if (viewer.isDeath()) continue;
                    //予測円
                    predictCircleParticle.circle(getViewers(Radius), viewer.getLocation(), radius, 16);
                    purpleParticle.sphere(getViewers(), viewer.getLocation().addY(3), 0.5);
                    SomSound.Tick.play(viewer);
                }
            });
            //爆発
            durationSkill.setRunnableEnd(()->{
                for (PlayerData viewer: getViewers(Radius)){
                    if (viewer.isDeath()) continue;
                    List<SomEntity> list = SomEntity.nearSomEntity(getTargets(), viewer.getLocation(), radius);
                    for (SomEntity entity : list){
                        damageCircleParticle.circle(getViewers(Radius), viewer.getLocation(), radius, 16);
                        //StunAttack HIT 2 ^ list.size() s
                        entity.addEffect(stunEffect.setTime(2^list.size()), griphia);
                        SomSound.Fire.play(viewer);
                        makeDamageGriphia(entity, 1);
                    }
                }
            });
            durationSkill.run();
        });
    }


    //////////////////////////////////////////////////


    //浄化の炎
    private int meteor = 0;
    public void Meteor(){
        meteor = 60;

        SomTask.run(()->{
            final int amount = 10;
            final int rateTick = 3;
            final int radius = 15;
            final int predictWait = 40;//tick

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, rateTick, rateTick*amount);
            durationSkill.setRunnable(()-> MeteorTick(radius, predictWait));
            durationSkill.run();
        });
    }
    public void MeteorTick(int radius, int predictWait){
        SomTask.run(() -> {
            //パーティクル指定
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, griphia).setVectorUp().setRandomSpeed(0.15f);
            final SomParticle damageCircleParticle = new SomParticle(Particle.FLAME, griphia).setVectorUp().setRandomSpeed(0.1f);

            //ランダムなロケーションを制定
            CustomLocation randomLocation = pivot.clone();//pivot
            //ランダム Θ(radian) + r(distance) 生成
            double randomTheta = randomDouble(0, Math.PI*2);
            double randomR = randomDouble(0, FieldRadius - radius/1.5);//field radius
            randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, 10, predictWait);
            //予測
            durationSkill.setRunnable(()->{
                predictCircleParticle.circle(getViewers(Radius), randomLocation, radius, 48);
                SomSound.Fire.play(getViewers(), randomLocation);
            });
            //爆発
            durationSkill.setRunnableEnd(()->{
                //ダメージ円パーティクル
                damageCircleParticle.circle(getViewers(Radius), randomLocation, radius);
                SomSound.Explode.play(getViewers(), randomLocation);

                //ダメージ処理
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), randomLocation, radius)) {
                    //Meteor Hit
                    makeDamageGriphia(entity, 1);
                    //バフ消去
                    if (entity.hasEffect("Forgiveness")){
                        SomEffect forgiveness = entity.getEffect("Forgiveness");
                        forgiveness.addStack(-1);
                        entity.sendMessage(forgiveness.getDisplay() + "§aが§cメテオのバフ消去効果§aと§c対消滅§aしました");
                    }else{
                        for (SomEffect effect : entity.getEffect().values()) {
                            if (!(effect.getId().equals("Cook")) && !(effect.getId().equals("Potion")) && effect.isBuff() && effect.getRank() == SomEffect.Rank.Normal) {
                                entity.removeEffect(effect);
                                break;
                            }
                        }
                    }
                }
            });
            durationSkill.run();

        });
    }

    //運命の炎
    private final int flameOfDestinyCT = 60;
    private int flameOfDestiny = 0;
    public void FlameOfDestiny(){
        flameOfDestiny = flameOfDestinyCT+5;

        SomTask.delay(()->{

            int radius = 8;
            final SomParticle particleBlue = new SomParticle(Color.BLUE, griphia);
            final SomParticle particleRed = new SomParticle(Color.RED, griphia);
            final SomEffect effectBlue = new SomEffect("DestinyBlue", "運命共同体[青]", false, flameOfDestinyCT).setRank(SomEffect.Rank.Impossible);
            final SomEffect effectRed = new SomEffect("DestinyRed", "運命共同体[赤]", false, flameOfDestinyCT).setRank(SomEffect.Rank.Impossible);
            final SomEffect effectPower = new SomEffect("DestinyPower", "相互作用", true, 1).setMultiply(StatusType.ATK, 0.3).setMultiply(StatusType.MAT, 0.3).setRank(SomEffect.Rank.Impossible);

            int i = 0;
            for (SomEntity entity : getTargetsOutHate()){
                if (i % 2 == 0) entity.addEffect(effectBlue, this);
                else entity.addEffect(effectRed, this);
                i++;
            }

            int perTick = 2;
            new BukkitRunnable(){
                int tick = 0;

                @Override
                public void run(){
                    if (flameOfDestinyCT*20 < tick || isInvalid()){
                        for (SomEntity entity : getTargets()){
                            if (entity.hasEffect(effectBlue)) entity.removeEffect(effectBlue);
                            if (entity.hasEffect(effectRed)) entity.removeEffect(effectRed);
                        }
                        this.cancel();
                    }

                    for (SomEntity entity : getTargets()){

                        if (entity.hasEffect(effectBlue)){
                            particleBlue.circle(getViewers(), entity.getLocation(), radius, 32);

                            for (SomEntity player : getTargetsOutHate()){
                                if (entity.equals(player)) continue;
                                double distance = entity.getLocation().distance(player.getLocation());

                                if (player.hasEffect(effectRed)){
                                    if (distance < radius*2){
                                        //blue on red
                                        makeDamageGriphia(entity, 2);
                                    }
                                }
                                if (player.hasEffect(effectBlue)){
                                    if (distance < radius){
                                        //blue on blue
                                        player.addEffect(effectPower, griphia);
                                    }
                                }
                            }
                        }

                        if (entity.hasEffect(effectRed)){
                            particleRed.circle(getViewers(), entity.getLocation(), radius, 32);

                            for (SomEntity player : getTargetsOutHate()){
                                if (entity.equals(player)) continue;
                                double distance = entity.getLocation().distance(player.getLocation());

                                if (player.hasEffect(effectBlue)){
                                    if (distance < radius*2){
                                        //red on blue
                                        makeDamageGriphia(entity, 2);
                                    }
                                }
                                if (player.hasEffect(effectRed)){
                                    if (distance < radius){
                                        //red on red
                                        player.addEffect(effectPower, griphia);
                                    }
                                }
                            }
                        }
                    }
                    tick+=perTick;
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, perTick);

        }, 30);
    }

    //消せない炎
    private int canNotForgetFlame = 0;
    public void CanNotForgetFlame(){
        canNotForgetFlame = 100;

        SomTask.run(()->{
            List<SomEntity> list = SomEntity.nearestSomEntity(getTargets(), getLocation(), Radius);

            list.removeIf(SomEntity::isDeath);
            if (list.isEmpty()) return;
            //一番遠い人
            SomEntity target = list.get(list.size()-1);

            for (SomEntity viewer : getViewers()){
                viewer.sendMessage("§cグリフィアが "+target.getDisplayName()+" を§n注視しています");
            }

            SomTask.wait(3000);

            final int amount = 8;
            final int rateTick = 15;
            final int radius = 10;

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, rateTick, rateTick*amount);
            durationSkill.setRunnable(()-> CanNotForgetFlameTick(radius, target));
            durationSkill.run();
        });

    }
    public void CanNotForgetFlameTick(int radius, SomEntity target){
        SomTask.run(()->{
            final SomParticle particle = new SomParticle(Particle.SMOKE_LARGE, griphia).setVectorUp().setRandomSpeed(0.01f);

            CustomLocation location = target.getLocation().lower();
            SomTask.wait(1500);
            SomSound.Fire.play(getViewers(), location);

            DurationSkill.MagicCircle magicCircle = magicCircle(griphia, location, radius,10, 36000);
            magicCircle.setRunnable(()->{
                particle.circle(getViewers(), location, radius, 32);
                List<SomEntity> list = SomEntity.nearSomEntity(getTargets(), location, radius);
                list.removeIf(SomEntity::isDeath);
                for (SomEntity entity : list){
                    //Hit CNF
                    makeDamageGriphia(entity, 1);
                    FlameCount+=10;
                }
            });
            magicCircle.run();
        });
    }

    //炎の柱
    private int flamePillar = 0;
    public void FlamePillar(){
        flamePillar = 100;

        SomTask.run(()->{
            final SomParticle particle = new SomParticle(Particle.FLAME, griphia).setVectorUp().setRandomSpeed(3f);
            final SomParticle predictParticle = new SomParticle(Color.RED, griphia);
            final int radius = 5;
            final float speed = 1;
            final Collection<SomEntity> list = getTargets();
            list.add(griphia);

            //ランダムなロケーションを制定
            CustomLocation randomLocation = pivot.clone();
            //ランダム Θ(radian) + r(distance) 生成
            double randomTheta = randomDouble(0, Math.PI*2);
            double randomR = randomDouble(38+radius, 54-radius);//field radius
            randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

            //グリフィアニに向かってすすむ
            randomLocation.lookLocation(getLocation());
            double firstRadian = -randomLocation.getYaw() * (Math.PI / 180);
            randomLocation.addXZ(Math.sin(firstRadian) * speed, Math.cos(firstRadian) * speed);

            //予測円
            predictParticle.circle(getViewers(), randomLocation, radius);
            SomTask.wait(1000);

            AtomicInteger count = new AtomicInteger(10);
            DurationSkill.MagicCircle magicCircle = magicCircle(griphia, randomLocation, radius,10, 36000);
            magicCircle.setRunnable(()->{
                //グリフィアニに向かってすすむ
                CustomLocation location = magicCircle.getLocation().lookLocation(getLocation());
                double radian = -location.getYaw() * (Math.PI / 180);
                location.addXZ(Math.sin(radian) * speed, Math.cos(radian) * speed);
                magicCircle.setLocation(location);

                particle.circle(getViewers(), randomLocation, radius);
                for (SomEntity entity : SomEntity.nearSomEntity(list, randomLocation, radius)){
                    if (entity.equals(griphia)){
                        //Hit FlamePillar Griphia
                        SomSound.Fire.play(getViewers(), magicCircle.getLocation());
                        FlameCount += 25;
                    }else{
                        //Hit FlamePillar Player
                        makeDamageGriphia(entity, 1);
                        SomSound.Fire.play(getViewers(), magicCircle.getLocation());
                        count.getAndDecrement();
                    }
                    if (count.get() <= 0) magicCircle.end();
                }
            });
            magicCircle.run();
        });
    }

    //忠誠
    private final int loyaltyCT = 30;
    private int loyalty = 0;
    public void Loyalty(){
        loyalty = loyaltyCT+5;

        SomTask.run(()->{
            final SomParticle particleBlue = new SomParticle(Color.BLUE, griphia);
            final SomParticle particleRed = new SomParticle(Color.RED, griphia);
            final SomEffect effectForenos = new SomEffect("LoyaltyForenos", "寡黙な氷", false, loyaltyCT).setRank(SomEffect.Rank.Impossible);
            final SomEffect effectPhiema = new SomEffect("LoyaltyPhiema", "強気な炎", false, loyaltyCT).setRank(SomEffect.Rank.Impossible);
            final int radius = 8;

            List<SomEntity> list = SomEntity.nearestSomEntity(getTargets(), getLocation(), Radius);
            list.removeIf(SomEntity::isDeath);


            //一番遠い人
            list.get(list.size()-1).addEffect(effectForenos, griphia);
            SomSound.Shine.play(list.get(list.size()-1));
            //一番近い人
            list.get(0).addEffect(effectPhiema, griphia);
            SomSound.Shine.play(list.get(0));

            int perTick = 2;
            new BukkitRunnable(){
                int tick = 0;

                @Override
                public void run(){
                    if (loyaltyCT*20 < tick || isInvalid()){
                        this.cancel();
                        for (SomEntity entity : getViewers()){
                            if (entity.hasEffect(effectForenos)) entity.removeEffect(effectForenos);
                            if (entity.hasEffect(effectPhiema)) entity.removeEffect(effectPhiema);
                        }
                    }

                    for (SomEntity entity : getViewers()){
                        if (entity.hasEffect(effectForenos)) {
                            particleBlue.circle(getViewers(), entity.getLocation(), radius, 32);
                            for (SomEntity target : SomEntity.nearSomEntity(getTargets(), entity.getLocation(), radius*2)){
                                //一人だった場合の救済
                                if (entity.equals(target)) continue;
                                if (target.hasEffect(effectPhiema)) {
                                    //二人が近づいた場合
                                    for (SomEntity viewer : getViewers()){
                                        makeDamageGriphia(viewer, 1000);
                                        viewer.sendMessage("§cグリフィアの記憶を刺激しました");
                                    }
                                    //バフ消す
                                    for (SomEntity player : getViewers()){
                                        if (player.hasEffect(effectForenos)) player.removeEffect(effectForenos);
                                        if (player.hasEffect(effectPhiema)) player.removeEffect(effectPhiema);
                                    }
                                }
                            }
                        }

                        if (entity.hasEffect(effectPhiema)) particleRed.circle(getViewers(), entity.getLocation(), radius, 32);
                    }

                    tick+=perTick;
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),60, perTick);
        });
    }


    //////////////////////////////////////////////////

    //炎風の目
    public void SafeArea(){
        sendBossMessage("§cグリフィアの周りが炎風で満たされます");

        SomTask.delay(()->{
            final CustomLocation location1 = new CustomLocation(SomCore.World, -17.5, 29, -2517.5);
            final CustomLocation location2 = new CustomLocation(SomCore.World, 18.5, 29, -2481.5);
            final SomParticle particle = new SomParticle(Particle.END_ROD, griphia).setVectorUp().setRandomSpeed(0.2f);
            final SomEffect effect = new SomEffect("SafeArea", "炎風の目", true, 3).setRank(SomEffect.Rank.Impossible);
            final int radius = 8;

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(griphia, 10,100);
            //予測
            durationSkill.setRunnable(()->{
                //予測円
                particle.circle(getViewers(Radius), location1, radius);
                particle.circle(getViewers(Radius), location2, radius);
            });
            //発動
            durationSkill.setRunnableEnd(()->{
                List<SomEntity> listLocation1 = SomEntity.nearSomEntity(getTargets(), location1, radius);
                List<SomEntity> listLocation2 = SomEntity.nearSomEntity(getTargets(), location2, radius);

                if (listLocation1.size() <= 2){
                    for (SomEntity entity : listLocation1){
                        entity.addEffect(effect, griphia);
                    }
                }
                if (listLocation2.size() <= 2){
                    for (SomEntity entity : listLocation2){
                        entity.addEffect(effect, griphia);
                    }
                }

                for (SomEntity entity : getTargets()){
                    if (!entity.hasEffect(effect)){
                        if(listLocation1.contains(entity) || listLocation2.contains(entity)){
                            //In Area But Filled
                            makeDamageGriphia(entity, 1000);
                            entity.sendMessage("§c魔法陣の許容量を超えました", SomSound.Tick);
                        }
                        else if(getTarget().equals(entity)){
                            //Out of Area But Target
                            entity.sendMessage("§a貴方は影響を受けませんでした", SomSound.Tick);
                        }
                        else{
                            //Out of Area
                            makeDamageGriphia(entity, 1000);
                            entity.sendMessage("§c貴方は炎風に焼かれました", SomSound.Tick);
                        }
                    }else{
                        //In Area
                        entity.sendMessage("§a魔法陣により炎風を防ぎました", SomSound.Tick);
                    }
                }

            });
            durationSkill.run();

        }, 40);
    }

    //記憶の封印
    public void SealMemory(){
        sendBossMessage("§cグリフィアの記憶を封印してください");

        SomTask.delay(()->{
            final int duration = 10;
            final SomEffect effect = new SomEffect("Sealable", "封印可能", true, 1).setRank(SomEffect.Rank.Impossible);

            List<SomEntity> sealableList = new ArrayList<>();
            List<SomEntity> list = (List<SomEntity>) getTargets();

            list.removeIf(SomEntity::isDeath);

            int randomInt = randomInt(0, list.size());
            sealableList.add(list.get(randomInt));
            if (list.size() == 1){
                sealableList.add(list.get(0));
            }else{
                list.remove(randomInt);
                randomInt = randomInt(0, list.size());
                sealableList.add(list.get(randomInt));
            }



            int perTick = 2;
            new BukkitRunnable(){
                int tick = 0;
                final int radius = 5;

                @Override
                public void run(){
                    if (isInvalid()) this.cancel();
                    if (tick < duration*20){
                        sealableList.removeIf(entity -> {
                            entity.addEffect(effect, griphia);
                            return getLocation().distance(entity.getLocation()) < radius;
                        });
                        if (sealableList.isEmpty()){
                            for (SomEntity entity : getViewers()){
                                //封印完了
                                entity.sendMessage("§cグリフィアの記憶を抑え込みました", SomSound.Tick);
                            }
                            this.cancel();
                        }
                        tick+=perTick;
                    }else{
                        //時間切れ
                        for (PlayerData entity : getViewers()){
                            SomSound.Tick.play(entity);
                            entity.death("§cグリフィアの記憶を抑え込めませんでした");
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),0, perTick);

        },40);
    }

    //赤い目覚め
    private final MobData redAwakenMob = MobDataLoader.getMobData("フレア");
    public void RedAwaken(){
        sendBossMessage("§cグリフィアが魔力を補給しようとしています");

        SomTask.syncDelay(()->{
            List<SomEntity> flares = new ArrayList<>();
            final int flaresAmount = 8;

            for (int i = 0; i < flaresAmount; i++){
                //ランダムなロケーションを制定
                CustomLocation randomLocation = pivot.clone();
                //ランダム Θ(radian) + r(distance) 生成
                double randomTheta = randomDouble(0, Math.PI*2);
                //double randomR = randomDouble(FieldRadius-15, FieldRadius);
                double randomR = randomDouble(38, 54);
                randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

                EnemyData enemyData = spawnByBoss(redAwakenMob, randomLocation, 10);
                enemyData.addHate(griphia, 2^14);
                enemyData.setTargetOverrideEntity(griphia);
                flares.add(enemyData);
            }

            int perTick = 10;
            new BukkitRunnable(){
                final int radius = 6;
                boolean flg = true;
                int flaresCount = 0;

                @Override
                public void run(){
                    if (isInvalid()){
                        for (SomEntity flare : flares){
                            flare.death();
                        }
                        this.cancel();
                    }

                    //グリフィアが近づいたら
                    for (SomEntity entity : SomEntity.nearSomEntity(flares, getLocation(), radius)){
                        if (!entity.isDeath()) {
                            entity.death();
                            FlameCount+=200;
                            flaresCount++;
                        }
                    }
                    //半分取られたら
                    if (flaresAmount/2 <= flaresCount){
                        //失敗
                        for (SomEntity entity : getViewers()){
                            makeDamageGriphia(entity, 1000);
                            entity.sendMessage("§cグリフィアの魔力が満たされました", SomSound.Tick);
                            FlameCount+=3000;
                        }
                        for (SomEntity flare : flares){
                            flare.death();
                        }
                        this.cancel();
                    }else{
                        flg = true;
                        for (SomEntity entity : flares){
                            if (!entity.isInvalid()){
                                flg = false;
                                break;
                            }
                        }
                        if (flg){
                            //全て倒して終了
                            for (SomEntity flare : flares){
                                flare.death();
                            }
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, perTick);
        }, 40);
    }

    //焼き付いて離れない呪い
    public void ImprintedMemory(){
        sendBossMessage("§cグリフィアの呪いが発現します");

        SomTask.delay(()->{
            final int duration = 30;
            final SomEffect effect = new SomEffect("ImprintedMemory", "呪い", false, duration).setRank(SomEffect.Rank.Impossible);
            final SomParticle particle = new SomParticle(Particle.SPELL_WITCH, griphia);

            //呪いを受けたことがない人リスト
            List<SomEntity> list = SomEntity.nearestSomEntity(getTargets(), getLocation(), Radius);

            for (SomEntity entity : list){
                entity.addEffect(effect, griphia);
            }

            //一番遠い人が最初
            SomEntity firstTarget = list.get(list.size()-1);
            list.remove(firstTarget);

            int perTick = 2;
            new BukkitRunnable(){
                int tick = 0;
                int circleRadius = 8;
                final int radius = 3;

                SomEntity target = firstTarget;
                CustomLocation circleLocation = firstTarget.getLocation().lower();

                @Override
                public void run(){
                    if (isInvalid()) this.cancel();
                    if (tick < duration*20){

                        //パーティクル
                        particle.circle(getViewers(), circleLocation, circleRadius, 32);
                        particle.circle(getViewers(), target.getLocation(), radius, 16);

                        List<SomEntity> inCircle = SomEntity.nearSomEntity(getTargets(), circleLocation, circleRadius);
                        //該当者が範囲外に出たら
                        if (!inCircle.contains(target)){
                            for (PlayerData viewer : getViewers()){
                                //Out Of Circle Target
                                viewer.death();
                                viewer.removeEffect(effect);
                            }
                            this.cancel();
                        }
                        //サークルに部外者が入ったら
                        for(SomEntity entity : inCircle){
                            if (list.contains(entity)) {
                                for (PlayerData viewer : getViewers()){
                                    //In Circle Others
                                    viewer.death();
                                    viewer.removeEffect(effect);
                                }
                                this.cancel();
                            }
                        }
                        //該当者の近くに部外者がいたらうつす
                        for (SomEntity entity : SomEntity.nearestSomEntity(getTargets(), target.getLocation(), radius)){
                            //まだ呪いを受けたことがない
                            if (list.contains(entity)){
                                target = entity;
                                circleLocation = entity.getLocation().lower();
                                list.remove(entity);
                                circleRadius+=1.5;
                                break;
                            }
                        }

                        if (list.isEmpty()){
                            for (SomEntity entity : getViewers()){
                                entity.sendMessage("§c呪いから抜け出しました");
                                entity.removeEffect(effect);
                            }
                            this.cancel();
                        }

                        tick+=perTick;
                    }else{
                        //時間切れ
                        for (PlayerData viewer : getViewers()){
                            //Time Over
                            viewer.death("§c呪いで焼け爛れました");
                            SomSound.Tick.play(viewer);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),0, perTick);


        }, 60);
    }

    //飽和した魔力
    public void SaturationSpell(){
        sendBossMessage("§c魔法陣の魔力が飽和しています");

        SomTask.syncDelay(()->{
            final int duration = 30;
            final SomEffect effect = new SomEffect("SaturationSpell", "飽和した魔力", false, duration).setRank(SomEffect.Rank.Impossible);
            for (SomEntity entity : getViewers()){
                entity.addEffect(effect, griphia);
            }

            int perTick = 10;
            new BukkitRunnable(){
                final int amount = 5;
                List<SomEntity> spells = spawnSpells(amount);
                int tick = 0;
                boolean flg = true;
                boolean spawnFlg = true;
                boolean timeOver = false;

                @Override
                public void run(){
                    if (isInvalid()){
                        for (SomEntity spell : spells){
                            spell.death();
                        }
                        for (SomEntity entity : getViewers()){
                            entity.removeEffect(effect);
                        }
                        this.cancel();
                    }else{
                        //存在判定
                        flg = true;
                        for (SomEntity entity : spells){
                            if (!entity.isInvalid()){
                                flg = false;
                                break;
                            }
                        }
                        //時間切れじゃない
                        if (tick < duration*20){
                            if (flg){
                                //敵がいない状況
                                for (SomEntity spell : spells){
                                    spell.death();
                                }
                                spells.clear();
                                if (spawnFlg){
                                    //5秒後
                                    SomTask.syncDelay(()->{
                                        if (isInvalid() || timeOver) return;
                                        spells = spawnSpells(amount);
                                    }, 100);
                                    spawnFlg = false;
                                }
                            }else{
                                //敵がいる状況
                                spawnFlg = true;
                            }
                            tick+=perTick;
                        }else{
                            timeOver = true;
                            if (flg){
                                //成功
                                for (SomEntity entity : getViewers()){
                                    entity.sendMessage("§c魔法陣の魔力暴走を阻止しました");
                                }
                                for (SomEntity spell : spells){
                                    spell.death();
                                }
                                this.cancel();
                            }else{
                                //失敗
                                for (SomEntity entity : getViewers()){
                                    makeDamageGriphia(entity, 1000);
                                    //+50%
                                    FlameCount+=5000;
                                    entity.sendMessage("§c魔法陣の魔力が暴走しました");
                                }
                                for (SomEntity spell : spells){
                                    spell.death();
                                }
                                this.cancel();
                            }
                        }
                    }


                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, perTick);
        }, 40);
    }

    private final MobData saturationSpellMob = MobDataLoader.getMobData("スペル");
    public  List<SomEntity> spawnSpells(int amount){

        List<SomEntity> spells = new ArrayList<>();

        for (int i = 0; i < amount; i++){
            //ロケーションを制定
            CustomLocation randomLocation = new CustomLocation(SomCore.World, 0.5, 118, -2499.5);
            //ランダム Θ(radian) + r(distance) 生成
            double randomTheta = randomDouble(0, Math.PI*2);
            //double randomR = randomDouble(FieldRadius-15, FieldRadius);
            double randomR = randomDouble(0, 15);
            randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

            EnemyData enemyData = spawnByBoss(saturationSpellMob, randomLocation);
            spells.add(enemyData);
        }

        return spells;
    }

}
