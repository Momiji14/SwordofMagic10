package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.*;
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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.SomCore.Log;

public class Gargil extends EnemyBoss{
    private final Gargil gargil;
    public Gargil(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        gargil = this;
    }

    //radius 40くらい
    private final CustomLocation pivot = new CustomLocation(SomCore.World, 3010.5, -22 , 491.5);
    @Override
    public void tick(){
        if (fieldGimmick) FieldGimmick();

        throwWeb--;

        if (throwWeb <= 0) ThrowWeb();

        if (crossing && getHealthPercent() <= 0.9) Crossing(true);
        if (salvationThread && getHealthPercent() <= 0.6) SalvationThread();
        if (contradictionOfDisgust && getHealthPercent() <= 0.3) ContradictionOfDisgust();
        if ((2 < getDifficulty().index()) && lastCrossing && getHealthPercent() <= 0.15) Crossing(false);

    }

    boolean fieldGimmick = true;
    //dont stop on web
    public void FieldGimmick(){
        fieldGimmick = false;;

        final int[] webDuration = {1, 2, 4, 8};
        final SomEffect effect;

        if (getDifficulty() == DungeonDifficulty.Easy || getDifficulty() == DungeonDifficulty.Normal) effect = new SomEffect("Web", "蜘蛛糸", false, webDuration[getDifficulty().index()]).setMultiply(StatusType.Movement, -0.9);
        else effect = new SomEffect("Web", "蜘蛛糸", false, webDuration[getDifficulty().index()]).setStun(true);

        new BukkitRunnable(){

            @Override
            public void run(){
                if (isInvalid()) this.cancel();
                for (PlayerData viewer : getViewers(Radius)){
                    if (viewer.getMovementSpeed() == 0){
                        viewer.addEffect(effect, gargil);
                    }
                    if(-15 < viewer.getLocation().getY() && 300 < viewer.getLocation().getZ() && !viewer.isDeath()){
                        Damage.makeDamage(gargil, viewer, DamageEffect.None, DamageOrigin.MAT, 24);
                    }
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),50, 10);
    }

    //通常攻撃
    int throwWeb = 10;
    final int[] restraintDuration = {2, 4, 6, 12};
    final int[] restraintWait = {1000, 1000, 1000, 1000};
    public void ThrowWeb(){
        throwWeb = 10;

        SomTask.run(() -> {
            final SomParticle predictParticle = new SomParticle(Color.RED, gargil);
            final SomParticle rayParticle = new SomParticle(Color.WHITE, gargil);
            final SomEffect stun = SomEffect.List.Stun.getEffect();
            final SomEffect effect;
            if (getDifficulty() == DungeonDifficulty.Easy || getDifficulty() == DungeonDifficulty.Normal) effect = new SomEffect("Restraint", "束縛", false, restraintDuration[getDifficulty().index()]).setMultiply(StatusType.Movement, -0.9).setSilence("All");
            else effect = new SomEffect("Restraint", "束縛", false, restraintDuration[getDifficulty().index()]).setStun(true).setSilence("All");

            //Stun追加
            addEffect(stun, gargil);
            //ヘイトへの向き取得
            CustomLocation location = getLocation().clone().addY(1.5);
            location.lookLocation(getTarget().getEyeLocation());
            location.setPitch(0);
            location.addYaw(-30);
            //予測線
            for (int i = 0; i < 3; i++){
                CustomLocation predictLocation = location.clone();
                predictParticle.line(getViewers(Radius), predictLocation.addYaw(30*i), 40, 1, 4);
            }
            SomTask.wait(restraintWait[getDifficulty().index()]);
            //蜘蛛の巣
            CustomLocation rayLocation = location.clone();
            for (int i = 0; i < 3; i++){
                SomRay ray = SomRay.rayLocationEntity(getTargets(), rayLocation, 40, 1, true);
                rayParticle.line(getViewers(Radius), rayLocation, ray.getOriginPosition(), 1, 4);
                if (ray.isHitEntity()) {
                    Damage.makeDamage(getEnemyData(), ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, 2);
                    ray.getHitEntity().addEffect(effect, gargil);
                    SomSound.Web.play(ray.getHitEntity());
                }
                rayLocation.addYaw(30);
            }
            removeEffect(stun);
        });
    }

    //交差する糸 (交差する想い)
    boolean crossing = true;
    boolean lastCrossing = true;
    final String[] crossingMessage = {"§c「交差する思考」", "§c「攻撃を止めてくれ」", "§c「話し合おう」",};
    final int[] crossingDuration = {10, 15, 20, 25};
    final float[] crossingWidth = {1, 1.5f, 2, 3}; // radius
    public void Crossing(boolean first){
        crossing = false;
        final String[] lastMessage = {"§c「絶対に逃さない」"};
        if (first) sendBossSkillMessage(crossingMessage);
        else{
            lastCrossing = false;
            sendBossSkillMessage(lastMessage);
        }

        SomTask.delay(()->{
            //パーティクル
            final SomParticle damageLineParticle = new SomParticle(Color.GRAY, gargil);

            final int perTick = 5;
            new BukkitRunnable(){
                int tick = 0;

                @Override
                public void run(){
                    if ((crossingDuration[getDifficulty().index()]*20 < tick && first) || isInvalid()) this.cancel();

                    for (PlayerData entity : getViewers(Radius)){
                        if (entity.isDeath()) continue;
                        SomTask.run(()->{
                            CustomLocation location = entity.getLocation();
                            location.setY(-21.8);
                            location.setPitch(0);
                            //1秒後
                            SomTask.wait(1000);
                            float pos = crossingWidth[getDifficulty().index()];
                            for (int j = 0; j < 4; j++){
                                location.setYaw(90 * j);
                                CustomLocation clonedLocation = location.clone();
                                if (j % 2 == 0){
                                    damageLineParticle.line(getViewers(Radius), clonedLocation.addXZ(pos,0), 40, 1, 4);
                                    damageLineParticle.line(getViewers(Radius), clonedLocation.addXZ(-pos*2,0), 40, 1, 4);
                                }else{
                                    damageLineParticle.line(getViewers(Radius), clonedLocation.addXZ(0,pos), 40, 1, 4);
                                    damageLineParticle.line(getViewers(Radius), clonedLocation.addXZ(0,-pos*2), 40, 1, 4);
                                }
                                SomRay ray = SomRay.rayLocationEntity(getTargets(), location.clone().addY(1.5), 40, pos-0.25, false);
                                //ダメージ判定
                                for (SomEntity target : ray.getHitEntities()){
                                    if (target.getLivingEntity().isOnGround() && first) Damage.makeDamage(gargil, target, DamageEffect.None, DamageOrigin.MAT, 4);
                                    else if (target.getLivingEntity().isOnGround() && !first)Damage.makeDamage(gargil, target, DamageEffect.None, DamageOrigin.MAT, 0.5);
                                }
                                for (SomEntity target : getTargets()){
                                    if (!target.getLivingEntity().isOnGround() && target.hasEffect("Web")) Damage.makeDamage(gargil, target, DamageEffect.None, DamageOrigin.MAT, 24);
                                }
                            }
                        });
                    }
                    tick+=perTick;
                }

            }.runTaskTimerAsynchronously(SomCore.plugin(),1, perTick);
        }, 40);
    }

    //救いの糸 (和解の手を差し伸べる)
    boolean salvationThread = true;
    final String[] salvationThreadMessage = {"§c「手を差し伸べる」", "§c「和解を望む」", "§c「救いの道」",};
    final int[] salvationThreadDuration = {15, 10, 6, 3};
    public void SalvationThread(){
        salvationThread = false;
        sendBossSkillMessage(salvationThreadMessage);

        SomTask.delay(()->{
            final SomParticle threadParticle = new SomParticle(Particle.END_ROD, gargil);
            final SomEffect failedEffect = new SomEffect("NonSalvation", "様子見", false, salvationThreadDuration[getDifficulty().index()]).setRank(SomEffect.Rank.Impossible);
            final SomEffect salvationEffect = new SomEffect("Salvation", "和解?", true, salvationThreadDuration[getDifficulty().index()]+1);

            HashMap<CustomLocation, Boolean> threadCondition = new HashMap<>();
            threadCondition.put(pivot.clone(), false);
            threadCondition.put(new CustomLocation(SomCore.World, 3003.5, -22 , 522.5), false);
            threadCondition.put(new CustomLocation(SomCore.World, 2981.5, -22 , 494.5), false);
            threadCondition.put(new CustomLocation(SomCore.World, 3017.5, -22 , 466.5), false);
            threadCondition.put(new CustomLocation(SomCore.World, 3035.5, -22 , 492.5), false);

            for (SomEntity entity : getViewers(Radius)){
                entity.addEffect(failedEffect, gargil);
            }
            new BukkitRunnable(){
                int tick = 0;
                final double radius = 0.5;

                @Override
                public void run(){
                    if (isInvalid()){
                        for (SomEntity entity : getViewers()){
                            if (entity.hasEffect(failedEffect)) entity.removeEffect(failedEffect);
                        }
                        this.cancel();
                    }
                    if (tick < salvationThreadDuration[getDifficulty().index()]*10){
                        threadCondition.forEach((location, flg)->{
                            if (flg) return;
                            threadParticle.circle(getViewers(), location, radius, 8);

                            CustomLocation thread = location.clone();
                            thread.setPitch(-90);
                            threadParticle.line(getViewers(), thread,20);

                            for (SomEntity entity : getViewers(Radius)){
                                double distance = location.distance(entity.getLocation());
                                if (distance < radius+0.5 && !entity.hasEffect(salvationEffect)){
                                    threadCondition.replace(location, true);
                                    entity.addEffect(salvationEffect, gargil);
                                    entity.removeEffect(failedEffect);
                                };
                            }
                        });
                        tick++;
                    }else{
                        for (PlayerData entity : getViewers(Radius)){
                            if (!entity.hasEffect(salvationEffect)) entity.death("§c貴方は差し伸べられた手を無下にしました");
                            else{
                                entity.removeEffect(salvationEffect);
                                entity.sendMessage("§a本当に手を取るつもりは無いですよね?");
                            }
                            SomSound.Tick.play(entity);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 2);

        },40);
    }

    //嫌悪の矛盾 敵沸かす
    boolean contradictionOfDisgust = true;
    final String[] contradictionOfDisgustMessage = {"§c「お互い様」", "§c「変わることではない」", "§c「結局相容れない」", };
    final int[] contradictionOfDisgustCount = {1, 2, 3, 4}; //*6
    private final List<EnemyData> contradictionOfDisgustEnemies = new ArrayList<>();

    public void ContradictionOfDisgust(){
        contradictionOfDisgust = false;
        sendBossSkillMessage(contradictionOfDisgustMessage);

        SomTask.syncDelay(()->{
            CustomLocation location = pivot.clone();
            MobData mobData = null;
            for (int i = 0; i < contradictionOfDisgustCount[getDifficulty().index()]*6; i++){
                switch (i % 6){
                    case 0 -> location.set(2990.5, -22, 491.5);
                    case 1 -> location.set(2990.5, -22, 512.5);
                    case 2 -> location.set(3009.5, -22, 515.5);
                    case 3 -> location.set(3032.5, -22, 490.5);
                    case 4 -> location.set(3028.5, -22, 475.5);
                    case 5 -> location.set(3015.5, -22, 468.5);
                }
                switch (i % 4){
                    case 0 -> mobData = MobDataLoader.getMobData("ダンク");
                    case 1 -> mobData = MobDataLoader.getMobData("ブレット");
                    case 2 -> mobData = MobDataLoader.getMobData("メールア");
                    case 3 -> mobData = MobDataLoader.getMobData("ルーフ");
                }
                contradictionOfDisgustEnemies.add(spawnByBoss(mobData, location));
            }
            new BukkitRunnable() {
                final SomEffect effect = SomEffect.List.Invincible.getEffect().setRank(SomEffect.Rank.Impossible);
                boolean flg = true;

                @Override
                public void run() {
                    if (isInvalid()){
                        delete();
                        this.cancel();
                    }
                    flg = true;
                    for (EnemyData enemy : contradictionOfDisgustEnemies){
                        if (!enemy.isInvalid()){
                            flg = false;
                            break;
                        }
                    }
                    if (!flg) addEffect(effect, gargil);
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 1);
        }, 20);
    }

    @Override
    public void delete() {
        for (EnemyData enemy : contradictionOfDisgustEnemies) {
            enemy.delete();
        }
        super.delete();
    }
}
