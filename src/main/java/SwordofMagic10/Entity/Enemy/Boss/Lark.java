package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
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

import java.util.Collection;

import static SwordofMagic10.Player.Dungeon.DungeonDifficulty.*;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.SomCore.Log;

public class Lark extends EnemyBoss{

    private final Lark lark;
    public Lark(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        lark = this;
    }

    private final CustomLocation pivot = new CustomLocation(SomCore.World, 2500.5, -20 , 500.5);

    @Override
    public void tick(){
        if (fieldGimmick) FieldGimmick();

        changeField--;
        noControl--;

        if (changeField <= 0) ChangeField();
        if (noControl <= 0) NoControl();

        if (canNotEscape && getHealthPercent() <= 0.7) CanNotEscape();
        if (cling && getHealthPercent() <= 0.5) Cling(true);
        if ((2 < getDifficulty().index()) && getHealthPercent() <= 0.3) cling = true;
        if ((2 < getDifficulty().index()) && getHealthPercent() <= 0.3) Cling(false);
        if (noConsideration && getHealthPercent() <= 0.3) NoConsideration();
    }

    private boolean fieldGimmick = true;
    public void FieldGimmick(){
        fieldGimmick = false;
        new BukkitRunnable(){
            final double[] NonBreathDuration = {0.5, 1, 1.5, 2.0};
            final SomEffect effect = new SomEffect("NonBreath", "息を殺す", false, NonBreathDuration[getDifficulty().index()]).setStun(true).setSilence("All");

            @Override
            public void run(){
                if (isInvalid()) this.cancel();
                for (SomEntity entity : getViewers(Radius)){
                    if (!entity.hasEffect("LowerBreath")) entity.addEffect(effect, lark);
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(),100, 40);
    }

    private final int FieldCT = 30;
    private int changeField = 0;
    private int nowField = -1;
    private final int[] fieldDeferment = {5, 3, 2, 1};//秒 フィールドからフィールドへ映るための猶予
    //effect
    private final SomEffect LowerBreath = new SomEffect("LowerBreath", "息を潜める", true, 1);
    public void ChangeField(){
        changeField = FieldCT;

        SomTask.run(()->{
            //パーティクル
            final SomParticle lineParticle = new SomParticle(Particle.END_ROD, lark).setVectorUp().setRandomSpeed(0.1f);
            //Location
            final CustomLocation location = pivot.clone();
            nowField++;
            switch (nowField % 4){
                case 0 -> location.set(2497, -19.7, 504).setYaw(0);
                case 1 -> location.set(2497, -19.7, 497).setYaw(90);
                case 2 -> location.set(2504, -19.7, 497).setYaw(180);
                case 3 -> location.set(2504, -19.7, 504).setYaw(270);
            }
            for (int i = 0; i < FieldCT*4 + fieldDeferment[getDifficulty().index()]*4; i++){
                if (isInvalid()) return;
                lineParticle.line(getViewers(Radius), location, 30);
                lineParticle.line(getViewers(Radius), location.clone().addYaw(90), 30);
                for (SomEntity entity : getViewers(Radius)){
                    float BaseYaw = location.getYaw();
                    float LookYaw = location.clone().lookLocation(entity.getLocation()).getYaw();
                    float diffYaw = BaseYaw - LookYaw;
                    if (-90 < diffYaw && diffYaw < 0){
                        entity.addEffect(LowerBreath, lark);
                    }
                }
                SomTask.wait(250);
            }
        });
    }



    //無制御 真下魔法陣
    private  int noControl = 32;
    private final float[] noControlRadius = {4, 4.5f, 5, 6};
    private final int[] noControlDuration = {10, 15, 25, 30};//秒
    public void NoControl(){
        noControl = 30;
        SomTask.run(()->{
            //パーティクル
            final SomParticle predictCircleParticle = new SomParticle(Color.RED, lark);
            final SomParticle damageCircleParticle = new SomParticle(Particle.SPELL_WITCH, lark);
            //Location
            CustomLocation location = getLocation();
            //予測円
            predictCircleParticle.circle(getViewers(Radius), getLocation(), noControlRadius[getDifficulty().index()], 16);
            SomTask.wait(1500);
            //魔法陣生成
            DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(this, location, noControlRadius[getDifficulty().index()], 10, noControlDuration[getDifficulty().index()]*20);
            magicCircle.setRunnable(() -> {
                SomTask.run(() -> {
                    predictCircleParticle.circle(getViewers(Radius), location, noControlRadius[getDifficulty().index()], 16);
                    damageCircleParticle.circleFill(getViewers(Radius), location, noControlRadius[getDifficulty().index()], 8);
                });
                for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, noControlRadius[getDifficulty().index()])) {
                    Damage.makeDamage(this, entity, DamageEffect.Dark, DamageOrigin.MAT, 1);
                    SomTask.wait(50);
                }
            });
            magicCircle.run();
        });
    }



    //ついてくる魔法陣
    private boolean canNotEscape = true;
    private final String[] canNotEscapeMessage = {"§c「この場所からは逃げられない」", "§c「この憎悪からは逃げられない」", "§c「この恐怖からは逃げられない」"};
    private final int[] canNotEscapeDuration = {10, 15, 25, 30};//秒
    private final int[] canNotEscapeRadius = {2, 3, 4, 5};//半径

    public void CanNotEscape(){
        canNotEscape = false;
        sendBossSkillMessage(canNotEscapeMessage);

        SomTask.delay(()->{
            //パーティクル
            final SomParticle damageCircleParticle = new SomParticle(Particle.SMOKE_LARGE, lark);

            for (SomEntity entity : getViewers(Radius)) {
                if (entity.isDeath()) continue;
                DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(lark, entity.getLocation(), canNotEscapeRadius[getDifficulty().index()], 5, canNotEscapeDuration[getDifficulty().index()]*20);
                magicCircle.setRunnable(() -> {
                    CustomLocation location = entity.getLocation();
                    location.setY(-19.8);
                    magicCircle.setLocation(location);
                    //1.5秒後
                    SomTask.wait(1500);
                    damageCircleParticle.circleFill(getViewers(Radius), location, canNotEscapeRadius[getDifficulty().index()], 8);
                    //ダメージ判定
                    for (SomEntity target : SomEntity.nearSomEntity(getTargets(), location, canNotEscapeRadius[getDifficulty().index()])){
                        Damage.makeDamage(this, target, DamageEffect.Dark, DamageOrigin.MAT, 4);
                    }
                });
                magicCircle.run();
            }
        }, 40);
    }



    //縋る
    private  boolean cling = true;
    private final String[] clingMessage = {"§c「縋ることに意味はないかもしれない」", "§c「この安心も偽物なのだろうか」", "§c「いつでも差し出せるように?」"};
    private final int[] clingDelay = {50, 40, 20, 10};//tick
    private final float[] clingRadius = {8, 7.5f, 7, 6};
    private final int[] clingDuration = {10, 20, 30, 40};//秒
    private final int[] clingDeBuffDuration = {2, 5, 15, 20};//秒
    public void Cling(boolean message){
        cling = false;
        if (message) sendBossSkillMessage(clingMessage);

        SomTask.delay(()->{
            final SomParticle clingCircleParticle = new SomParticle(Particle.END_ROD, lark);
            final SomEffect effect = new SomEffect("Fear", "恐怖", false, clingDeBuffDuration[getDifficulty().index()]).setSilence("All");

            for (int i = 0; i < clingDuration[getDifficulty().index()]*2; i++){
                if (isInvalid()) return;
                clingCircleParticle.circle(getViewers(Radius), getLocation(), clingRadius[getDifficulty().index()], 32);
                for (SomEntity entity : getViewers(Radius)){
                    double distance = getLocation().distance(entity.getLocation());
                    if (clingRadius[getDifficulty().index()] < distance) entity.addEffect(effect, lark);
                }
                SomTask.wait(500);
            }
        }, clingDelay[getDifficulty().index()]);

    }

    //無考慮 安全ではない
    private boolean noConsideration = true;
    private final String[] noConsiderationMessage = {"§c「次は誰の番だ?」", "§c「足音が近づいてくる」", "§c「嫌らしい笑みを浮かべている」"};
    private final float[] noConsiderationWait = {4, 3.5f, 3, 2};//秒
    public void NoConsideration(){
        noConsideration = false;
        sendBossSkillMessage(noConsiderationMessage);

        SomTask.delay(()->{
            final SomParticle noConsiderationCircleParticle = new SomParticle(Particle.END_ROD, lark);
            int radius = 3;

            //予測円
            for (int i = 0; i < noConsiderationWait[getDifficulty().index()]*2; i++){
                if (isInvalid()) return;
                noConsiderationCircleParticle.circle(getViewers(Radius), pivot.addY(0.2), radius, 32);
                for (PlayerData target : getViewers(Radius)){
                    SomSound.Tick.play(target);
                }
                SomTask.wait(500);
            }
            for (PlayerData target : getViewers(Radius)){
                double distance = pivot.distance(target.getLocation());
                if( radius < distance ) target.death("§c貴方の番です");
                else target.sendMessage("§a息を潜めている", SomSound.Tick);
            }
            //wait
            for (int i = 0; i < noConsiderationWait[getDifficulty().index()]*2; i++){
                if (isInvalid()) return;
                for (PlayerData target : getViewers(Radius)){
                    SomSound.Tick.play(target);
                }
                SomTask.wait(500);
            }
            for (PlayerData target : getViewers(Radius)){
                if (target.hasEffect("NonBreath") || target.hasEffect("Fear")){
                    if (getDifficulty() == Easy) Damage.makeDamage(this, target, DamageEffect.Dark, DamageOrigin.MAT, 24);
                    else target.death("§c貴方の番です");
                }
                else target.sendMessage("§a今日も人間でいられる", SomSound.Tick);
            }
        },20);
    }
}
