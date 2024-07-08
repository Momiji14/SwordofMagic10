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
import java.util.Collections;
import java.util.HashMap;

import static SwordofMagic10.Player.Dungeon.DungeonDifficulty.Easy;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.SomCore.Log;

public class Clam extends EnemyBoss {

    private final Clam clam;
    public Clam(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
        clam = this;
    }

    //    Radius = 25~30くらい
    private final CustomLocation pivot = new CustomLocation(SomCore.World, 2000.5, -19 , 500.5);

    @Override
    public void tick(){
        simpleBash--;

        if (simpleBash <= 0) SimpleBash();

        if (yourChoice && getHealthPercent() <= 0.8) YourChoice();
        if (tornado && getHealthPercent() <= 0.5) Tornado();
        if (disaster && getHealthPercent() <= 0.3) Disaster();
    }

    private int simpleBash = 15;
    private final double[] simpleBashKnockBack = {1, 1.5, 2, 2.5};

    public void SimpleBash(){
        simpleBash = 15;

        SomTask.run(() -> {
            final SomParticle predictParticle = new SomParticle(Color.RED, clam);
            final SomEffect stun = SomEffect.List.Stun.getEffect();

            //Stun追加
            addEffect(stun, clam);
            //ヘイトへの向き取得
            CustomLocation location = getEyeLocation().clone();
            location.setPitch(0);
            location.lookLocation(getTarget().getEyeLocation());
            //予測線
            predictParticle.line(getViewers(Radius), location, 6, 1.5, 4);
            SomTask.wait(750);
            removeEffect(stun);
            //突進
            setVelocity(location.getDirection().normalize().setY(0.25));
            SomRay ray = SomRay.rayLocationEntity(getTargets(), location, 5, 1.5, false);
            for (SomEntity hitEntity : ray.getHitEntities()) {
                Damage.makeDamage(getEnemyData(), hitEntity, DamageEffect.None, DamageOrigin.ATK, 2, simpleBashKnockBack[getDifficulty().index()]);
                SomSound.Bash.play(hitEntity);
            }
        });
    }



    //覚え
    public boolean yourChoice = true;
    private final String[] yourChoiceMessage = {"§c「斧を振りかざす」", "§c「生態系を壊す」", "§c「氷を溶かす」"};
    private final int[] yourChoiceDuration = {15, 30, 60, 120};
    public void YourChoice(){
        yourChoice = false;
        sendBossSkillMessage(yourChoiceMessage);

        SomTask.delay(()->{
            int radius = 4;
            //エフェクト
            final SomEffect effect = new SomEffect("YourChoice", "選択可能", false, 5);
            final SomEffect c1effect = new SomEffect("Choice1", "選択肢[沈黙]", false, yourChoiceDuration[getDifficulty().index()]).setSilence("All").setRank(SomEffect.Rank.High);
            final SomEffect c2effect = new SomEffect("Choice2", "選択肢[スタン]", false, yourChoiceDuration[getDifficulty().index()]).setStun(true).setRank(SomEffect.Rank.High);
            //パーティクル
            final SomParticle c1PredictParticle = new SomParticle(Color.BLACK, clam).setVectorUp().setRandomSpeed(1f);
            final SomParticle c2PredictParticle = new SomParticle(Color.WHITE, clam).setVectorUp().setRandomSpeed(1f);
            //Location
            final CustomLocation c1location = new CustomLocation(SomCore.World, 2000.5, -18.7, 492.5);
            final CustomLocation c2location = new CustomLocation(SomCore.World, 2000.5, -18.7, 508.5);
            //予告
            for (SomEntity entity : getViewers(Radius)){
                entity.sendMessage("§c選択して下さい", SomSound.Tick);
                entity.addEffect(effect, clam);
            }
            //猶予
            for (int i = 0; i < 10; i++){
                c1PredictParticle.circle(getViewers(Radius), c1location, radius, 16);
                c2PredictParticle.circle(getViewers(Radius), c2location, radius, 16);
                SomTask.wait(500);
            }
            //判定
            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), c1location, radius)){
                entity.addEffect(c1effect, clam);
            }
            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), c2location, radius)){
                entity.addEffect(c2effect, clam);
            }
            for (SomEntity entity : getViewers(Radius)){
                if (!entity.hasEffect(c1effect) && !entity.hasEffect(c2effect)){
                    entity.sendMessage("§c第三の選択肢ですか?");
                    Damage.makeDamage(this, entity, DamageEffect.None, DamageOrigin.MAT, 24);
                }
            }

            new BukkitRunnable(){
                @Override
                public void run(){
                    if (isInvalid()){
                        for (SomEntity entity : getViewers(Radius)){
                            if (entity.hasEffect(c1effect)) entity.removeEffect(c1effect);
                            if (entity.hasEffect(c2effect)) entity.removeEffect(c2effect);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, 10);

        }, 15);
    }



    private boolean tornado = true;
    private final String[] tornadoMessage = {"§c「悪く思わないでください」", "§c「私達は悪くありません」"};
    private final int[] tornadoAmount = {1, 2, 3, 4}; //円周１つに付きなんこ
    public void Tornado(){
        tornado = false;
        sendBossSkillMessage(tornadoMessage);


        SomTask.delay(()->{
            final SomParticle predictParticle = new SomParticle(Color.RED, clam).setVectorUp().setRandomSpeed(5f);
            final SomParticle tornadoParticle = new SomParticle(Particle.CRIT, clam).setVectorUp().setRandomSpeed(5f);
            final SomEffect effect = new SomEffect("NonAir", "空の空気", false, 5).setSilence("All");

            double theta = 0;//start
            int tornadoRadius = 3;

            //時間のfor
            for (int i = 0; i < 4; i++){
                //radiusのfor
                for (int j = 1; j <= 6; j++){
                    int nowRadius = (tornadoRadius*2) * j;
                    //radiusごとのtornadoのfor
                    for (int k = 0; k < tornadoAmount[getDifficulty().index()]; k++){
                        CustomLocation location = pivot.clone().addY(0.5);
                        double thetaForLocation = (theta + (Math.PI*2 / tornadoAmount[getDifficulty().index()]) * k);
                        location.addXZ(Math.cos(thetaForLocation) * nowRadius, Math.sin(thetaForLocation) * nowRadius);

                        predictParticle.circle(getViewers(Radius), location, tornadoRadius, 16);
                    }
                }
                SomTask.wait(250);
            }

            Clam clam = this;
            new BukkitRunnable(){
                double theta = 0;

                @Override
                public void run(){
                    for (SomEntity entity : getViewers(Radius)){
                        if (-14 < entity.getLocation().getY()){
                            entity.addEffect(effect, clam);
                        }
                    }
                    if (isInvalid()) this.cancel();
                    //radiusのfor
                    for (int j = 1; j <= 6; j++){
                        int nowRadius = (tornadoRadius*2) * j;
                        //radiusごとのtornadoのfor
                        for (int k = 0; k < tornadoAmount[getDifficulty().index()]; k++){
                            CustomLocation location = pivot.clone().addY(0.5);
                            double thetaForLocation = (theta + (Math.PI*2 / tornadoAmount[getDifficulty().index()]) * k);
                            if (j % 2 == 0) thetaForLocation = -thetaForLocation;
                            location.addXZ(Math.cos(thetaForLocation) * nowRadius, Math.sin(thetaForLocation) * nowRadius);
                            //パーティクル
                            tornadoParticle.circle(getViewers(Radius), location, tornadoRadius, 32);
                            //ダメージ処理
                            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, tornadoRadius)) {
                                Damage.makeDamage(clam, entity, DamageEffect.None, DamageOrigin.ATK, 0.5);
                                SomSound.Slash.play(entity);
                            }
                        }
                    }
                    //角度追加
                    theta += Math.PI / 10;
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(),1, 4);

        },30);
    }



    //上昇
    private boolean disaster = true;
    private final String[] disasterMessage = {"§c「土砂流れ」", "§c「洪水」", "§c「雪崩」"};
    private final int[] disasterHeight = {10, 15, 25, 33};
    private final float[] disasterSpeed = {1f, 1.5f, 2f, 2.5f}; //ブロック/秒
    public void Disaster(){
        disaster = false;
        HashMap<PlayerData, Integer> indexEachPlayer = sendBossSkillMessage(disasterMessage);

        SomTask.delay(()->{
            final SomParticle blownDisasterParticle = new SomParticle(Color.fromRGB(139, 69, 19), clam);
            final SomParticle blueDisasterParticle = new SomParticle(Color.BLUE, clam);
            final SomParticle whiteDisasterParticle = new SomParticle(Color.WHITE, clam);
            CustomLocation location = pivot.clone();

            for (int i = 0; i < disasterHeight[getDifficulty().index()]*disasterSpeed[getDifficulty().index()]; i++){
                if (isInvalid()) return;
                location.addY(1 / disasterSpeed[getDifficulty().index()]);
                for (PlayerData entity : getViewers(Radius)){
                    int index = indexEachPlayer.get(entity);
                    switch (index){
                        case 0 -> blownDisasterParticle.circleFill(Collections.singleton(entity), location, 30, 48);
                        case 1 -> blueDisasterParticle.circleFill(Collections.singleton(entity), location, 30, 48);
                        case 2 -> whiteDisasterParticle.circleFill(Collections.singleton(entity), location, 30, 48);
                        default -> {}
                    }
                    if (entity.getLocation().getY() < location.getY()){
                        Damage.makeDamage(this, entity, DamageEffect.None, DamageOrigin.ATK, 4);
                        if (i == disasterHeight[getDifficulty().index()]*disasterSpeed[getDifficulty().index()]-1 && getDifficulty() != Easy) entity.death("§c§n災害§aに&c巻き込まれました");
                    }
                }
                SomTask.wait(1000/disasterSpeed[getDifficulty().index()]);
            }


        },60);
    }

}
