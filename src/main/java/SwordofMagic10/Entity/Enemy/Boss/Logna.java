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
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Player.Dungeon.DungeonDifficulty.Easy;
import static SwordofMagic10.Player.Dungeon.DungeonDifficulty.Normal;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;

public class Logna extends EnemyBoss {
    private final Logna logna;
    public Logna(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        logna = this;
    }

    //bossAreaRadius = 45
    private final CustomLocation pivot = new CustomLocation(SomCore.World, 1000.5, -26 , 500.5);

    @Override
    public void tick() {
        waterRipples--;
        if (waterRipples <= 0) WaterRipples();
        if (whirlpool && getHealthPercent() <= 0.8) Whirlpool();
        if (drown && getHealthPercent() <= 0.5) Drown();
        if (tsunami && getHealthPercent() <= 0.2) Tsunami();
    }



    private int waterRipples = 20;
    private final int[] waterRipplesRadius = {15, 20, 25, 30};
    private final int[] waterRipplesCount = {3, 4, 5, 6};
    private final int[] waterRipplesWait = {900, 750, 600, 400};
    public void WaterRipples() {
        waterRipples = 20;

        SomTask.run(() -> {
            int skillRadius = waterRipplesRadius[getDifficulty().index()];
            int skillCount = waterRipplesCount[getDifficulty().index()];
            int wait = waterRipplesWait[getDifficulty().index()];

            //パーティクル指定
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, logna);
            final SomParticle damageCircleParticle = new SomParticle(Particle.BUBBLE_POP, logna);
            //その場の座標取得
            CustomLocation location = getLocation().addY(0.2);
            //一発ごとに進む半径の距離
            int eachRadius = skillRadius / skillCount;

            //最初の予測円
            predictCircleParticle.circle(getViewers(Radius), location, eachRadius, 16);
            SomTask.wait(wait);

            for (int i = 0; i < skillRadius; i = i + eachRadius){

                //ダメージ円パーティクル
                damageCircleParticle.circle(getViewers(Radius), location, i, 16);
                damageCircleParticle.circle(getViewers(Radius), location, i + eachRadius, 16);
                //予測円パーティクル
                if (i + eachRadius < skillRadius) {
                    predictCircleParticle.circle(getViewers(Radius), location, i + eachRadius, 16);
                    predictCircleParticle.circle(getViewers(Radius), location, i + eachRadius * 2, 16);
                }

                //ダメージ処理
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, i + eachRadius)) {
                    double distance = location.distance(entity.getLocation());
                    //該当範囲にいるプレイヤーのみ
                    if (i < distance) {
                        Damage.makeDamage(this, entity, DamageEffect.Water, DamageOrigin.MAT, 2);
                        SomSound.Drown.play(entity);
                    }
                }
                SomTask.wait(wait);
            }
        });
    }



    private boolean whirlpool = true;
    private final String[] whirlpoolMessage = {"§c「渦は時間通りに流れます。流れに逆らってはいけません」"};
    private final int[] WhirlpoolTolerance = {90, 75, 60, 45};
    private final int WhirlpoolDuration = 10; //秒

    public void Whirlpool() {
        whirlpool = false;
        if (getDifficulty() == Easy){
            return;
        }
        sendBossSkillMessage(whirlpoolMessage);

        SomTask.delay(() -> {
            HashMap<PlayerData, Float> whirlpoolLastYaw = new HashMap<>();
            HashMap<PlayerData, Float> whirlpoolFirstYaw = new HashMap<>();
            HashMap<PlayerData, Boolean> whirlpoolClear = new HashMap<>();
            SomEffect effect = new SomEffect("Whirlpool", "渦水", false, WhirlpoolDuration).setRank(SomEffect.Rank.Impossible);

            for (PlayerData viewer : getViewers(Radius)) {
                viewer.sendMessage("§c§n渦水§aから逃れてください");
                whirlpoolClear.put(viewer, false);
                whirlpoolFirstYaw.put(viewer, viewer.getLocation().getYaw());

                float nowYaw = (viewer.getLocation().getYaw() - whirlpoolFirstYaw.get(viewer)) % 360F;
                whirlpoolLastYaw.put(viewer, nowYaw);
                //デバフ追加
                viewer.addEffect(effect, logna);
            }
            Logna logna = this;
            new BukkitRunnable() {
                int tick = 0;
                final SomParticle particle = new SomParticle(Particle.BUBBLE_POP, logna);

                @Override
                public void run() {
                    if (isInvalid()) this.cancel();
                    if (WhirlpoolDuration*20 > tick) {
                        for (PlayerData viewer : getViewers(Radius)) {
                            if (!whirlpoolFirstYaw.containsKey(viewer) || !whirlpoolLastYaw.containsKey(viewer)) {
                                whirlpoolClear.put(viewer, false);
                                whirlpoolFirstYaw.put(viewer, viewer.getLocation().getYaw());
                                float nowYaw = (viewer.getLocation().getYaw() - whirlpoolFirstYaw.get(viewer)) % 360;
                                whirlpoolLastYaw.put(viewer, nowYaw);
                            }
                            if (!viewer.isDeath() && !whirlpoolClear.get(viewer)) {
                                //パーティクル表示
                                particle.circle(viewer.getViewers(), viewer.getHipsLocation(),1, 8);
                                //現在のyaw(オフセット付き)
                                float nowYaw = Math.floorMod((int) (viewer.getLocation().getYaw() - whirlpoolFirstYaw.get(viewer)), 360);
                                //前回のyaw(オフセット付き)
                                float lastYaw = whirlpoolLastYaw.get(viewer);

                                float difYaw = nowYaw - lastYaw;
//                                viewer.sendMessage("mod(("+ nowYaw +" = "+ viewer.getLocation().getYaw() +" - "+ whirlpoolFirstYaw.get(viewer)+ "), 360)");

                                if (difYaw < -3 || WhirlpoolTolerance[getDifficulty().index()] < difYaw) {
                                    Damage.makeDamage(logna, viewer, DamageEffect.Water, DamageOrigin.MAT, 1);
                                }else{
                                    whirlpoolLastYaw.put(viewer, nowYaw);
                                    if (340 < nowYaw){
                                        viewer.removeEffect("Whirlpool");
                                        viewer.sendMessage("§c§n渦水§aから逃れました", SomSound.Tick);
                                        whirlpoolClear.put(viewer, true);
                                    }
                                }

                            }
                        }
                        tick++;
                    } else {
                        for (PlayerData viewer : getViewers(Radius)) {
                            if (!whirlpoolClear.get(viewer) && !viewer.isDeath()) {
                                    if (getDifficulty() == Normal) Damage.makeDamage(logna, viewer, DamageEffect.Water, DamageOrigin.MAT, 4);
                                    else viewer.death("§c§n渦水§aに§4呑まれ§aました");
                            }
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, 1);
        }, 50);
    }



    private boolean drown = true;
    private final String[] drownMessage = {"§c「時は流れ、風化し、全て潮になる」", "§c「肺に水が満たされる」", "§c「見捨てられたことは水に流しましょう」"};
    private final int[] drownRadius = {6, 6, 6, 6};
    private final int[] drownDuration = {15000, 20000, 23000, 25000};
    private final int[] drownTickCount = {60, 160, 276, 400};
    private final int[] drownPredictWait = {1000, 1000, 1000, 1000};
    public void Drown() {
        drown = false;
        sendBossSkillMessage(drownMessage);
        SomTask.delay(() -> {
            int wait = drownDuration[getDifficulty().index()]/drownTickCount[getDifficulty().index()];
            //同じ難易度内でも毎回radius違うとおもろいかもね
            for (int i = 0; i < drownTickCount[getDifficulty().index()]; i++) {
                if (isInvalid()) return;
                DrownTick(drownRadius[getDifficulty().index()], drownPredictWait[getDifficulty().index()]);
                SomTask.wait(wait);
            }
        }, 40);
    }

    public void DrownTick(int radius, int wait){
    //radius: ダメージ円の半径, wait: 予測円->ダメージ円のwait
        SomTask.run(() -> {
            //パーティクル指定
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, logna);
            final SomParticle damageCircleParticle = new SomParticle(Particle.BUBBLE_POP, logna).setVectorUp();

            //ランダムなロケーションを制定
            CustomLocation randomLocation = pivot.clone().addY(0.3);
            //ランダム Θ(radian) + r(distance) 生成
            double randomTheta = randomDouble(0, Math.PI*2);
            double randomR = randomDouble(0, 45 - radius/1.5);
            randomLocation.addXZ(Math.cos(randomTheta) * randomR, Math.sin(randomTheta) * randomR);

            //予測円パーティクル
            predictCircleParticle.circle(getViewers(Radius), randomLocation, radius, 32);
            SomTask.wait(wait);

            //ダメージ円パーティクル
            damageCircleParticle.circle(getViewers(Radius), randomLocation, radius, 32);
            //範囲外ダメージパーティクル
            damageCircleParticle.circle(getViewers(Radius), pivot, 45, 32);
            //ダメージ処理
            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), randomLocation, radius)) {
                if (getDifficulty() == Easy) Damage.makeDamage(this, entity, DamageEffect.Water, DamageOrigin.MAT, 1);
                else Damage.makeDamage(this, entity, DamageEffect.Water, DamageOrigin.MAT, 1);
                SomSound.Drown.play(entity);
            }
            //範囲外ダメージ処理
            for (SomEntity entity : getViewers(Radius)) {
                double distance = pivot.distance(entity.getLocation());
                if ( 45 < distance ){
                    Damage.makeDamage(this, entity, DamageEffect.Water, DamageOrigin.MAT, 4);
                    SomSound.Drown.play(entity);
                }
            }
        });
    }



    private boolean tsunami = true;
    private final String[] tsunamiMessage = { "§c「ここで私が死んでも、地図に載ることはないでしょう」", /*"§c「海の前で人間は無力です」",*/ "§c「あの時、雰囲気に飲まれてなければ」"};
    private final int[] tsunamiDuration = {10000, 15000, 20000, 25000};
    private final int[] tsunamiTickCount = {5, 10, 20, 30};
    private final int[] tsunamiSpeedWait = {200, 150, 120, 100};
    public void Tsunami() {
        tsunami = false;
        sendBossSkillMessage(tsunamiMessage);
        SomTask.delay(() -> {
            int wait = tsunamiDuration[getDifficulty().index()] / tsunamiTickCount[getDifficulty().index()];
            for (int i = 0; i < tsunamiTickCount[getDifficulty().index()]; i++) {
                if (isInvalid()) return;
                TsunamiTick(tsunamiSpeedWait[getDifficulty().index()]);
                SomTask.wait(wait);
            }
        }, 40);
    }

    public void TsunamiTick(int wait){
        SomTask.run(() -> {

            //パーティクル指定
            final SomParticle damageCircleParticle = new SomParticle(Color.AQUA, logna);
            //一発ごとに進む半径の距離
            int eachRadius = 1;

            for (int i = 0; i < 90; i = i + eachRadius){
                //Location取得
                CustomLocation location = getLocation().addY(0.2);
                //ダメージ円パーティクル
                damageCircleParticle.circle(getViewers(Radius), location, i, 32);
                damageCircleParticle.circle(getViewers(Radius), location, i + eachRadius, 32);

                //ダメージ処理
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, i + eachRadius)) {
                    double distance = location.distance(entity.getLocation());
                    //該当範囲にいるプレイヤーのみ
                    if (i < distance && entity.getLivingEntity().isOnGround()) {
                        Damage.makeDamage(this, entity, DamageEffect.Water, DamageOrigin.MAT, 4);
                        SomSound.Drown.play(entity);
                    }
                }
                //waitが早くなるとTsunamiのスピードが早くなる
                SomTask.wait(wait);
            }
        });
    }
}
