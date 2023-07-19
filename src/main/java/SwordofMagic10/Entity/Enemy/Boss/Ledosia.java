package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.*;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Dungeon.Instance.DungeonInstance.Radius;

public class Ledosia extends EnemyBoss {

    public Ledosia(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        super(mobData, level, difficulty, location, viewers);
    }

    private final CustomLocation pivot = new CustomLocation(SomCore.World, 500.5, -10 , 500.5);

    @Override
    public void tick() {
        shellingWait--;
        if (shellingWait <= 0) shelling();
        if (snipeDance && getHealthPercent() <= 0.8) SnipeDance();
        if (russianRoulette && getHealthPercent() <= 0.5) RussianRoulette();
        if (areaOfRisk && getHealthPercent() <= 0.2) AreaOfRisk();
    }

    private int shellingWait = 10;
    private final int[] shellingTick = {1, 3, 5, 8};
    public void shelling() {
        shellingWait = 10;
        SomTask.run(() -> {
            SomParticle particle = new SomParticle(Color.RED);
            particle.setTime(1000);
            CustomLocation location = getTarget().getLocation();
            particle.line(getViewers(), getEyeLocation(), location, 0.5);
            for (SomEntity entity : SomEntity.nearSomEntity(getTargets(), location, 3)) {
                Damage.makeDamage(this, entity, DamageEffect.Fire, DamageOrigin.MAT, 2);
            }
        });
        SomTask.run(() -> {
            for (int i = 0; i < shellingTick[getDifficulty().ordinal()]; i++) {
                AreaOfRiskTick();
                SomTask.wait(150);
            }
        });
    }

    private boolean snipeDance = true;
    private final String[] snipeDanceMessage = {"§c「誰かを殺すことで明日を生きられる」", "§c「生きるために引き金を引く」", "§c「いつ自分が同じ目に合うか怯えながら暮らす」"};
    private final int[] snipeDanceTick = {10, 50, 100, 200};
    private final int[] snipeDanceWait = {500, 200, 100, 50};
    public void SnipeDance() {
        snipeDance = false;
        sendBossSkillMessage(snipeDanceMessage);
        SomTask.delay(() -> {
            for (int i = 0; i < snipeDanceTick[getDifficulty().ordinal()]; i++) {
                if (isInvalid()) return;
                SnipeDanceTick();
                SomTask.wait(snipeDanceWait[getDifficulty().ordinal()]);
            }
        }, 60);
    }

    public void SnipeDanceTick() {
        for (PlayerData target : getViewers(Radius)) {
            SomTask.run(() -> {
                final SomParticle shotParticle = new SomParticle(Particle.FIREWORKS_SPARK);
                final SomParticle predictParticle = new SomParticle(Color.RED);
                CustomLocation start = getEyeLocation().clone();
                start.lookLocation(target.getEyeLocation());
                SomTask.wait(750);
                SomRay ray = SomRay.rayLocationEntity(getTargets(), start, 70, 1.5, true);
                predictParticle.line(getViewers(Radius), start, 70, 1.5, 5);
                shotParticle.line(getViewers(Radius), start, 70, 1.5, 5);
                for (SomEntity hitEntity : ray.getHitEntities()) {
                    Damage.makeDamage(getEnemyData(), hitEntity, DamageEffect.Fire, DamageOrigin.MAT, 0.25);
                    SomSound.Fire.play(hitEntity);
                }
            });
        }
    }

    private boolean areaOfRisk = true;
    private final String[] areaOfRiskMessage = {"§c「皆自分だけが安全だと思い込む」", "§c「安心は自分で手に入れるものだった」", "§c「息を吸う暇もない」"};
    private final int[] areaOfRiskTick = {25, 85, 175, 300};
    private final int[] areaOfRiskWait = {1000, 250, 125, 75};
    public void AreaOfRisk() {
        areaOfRisk = false;
        sendBossSkillMessage(areaOfRiskMessage);
        SomTask.delay(() -> {
            for (int i = 0; i < areaOfRiskTick[getDifficulty().ordinal()]; i++) {
                if (isInvalid()) return;
                AreaOfRiskTick();
                SomTask.wait(areaOfRiskWait[getDifficulty().ordinal()]);
            }
        }, 60);
    }

    public void AreaOfRiskTick() {
        final SomParticle shotParticle = new SomParticle(Particle.FIREWORKS_SPARK);
        final SomParticle predictParticle = new SomParticle(Color.RED);
        final SomParticle circleParticle = new SomParticle(Particle.LAVA);
        circleParticle.setVectorUp();
        SomTask.run(() -> {
            List<SomEntity> list = new ArrayList<>(getViewers(pivot, Radius));
            list.removeAll(getViewers(pivot, 35));
            for (SomEntity hitEntity : list) {
                Damage.makeDamage(getEnemyData(), hitEntity, DamageEffect.Fire, DamageOrigin.MAT, 0.60);
                SomSound.Fire.play(hitEntity);
            }

            circleParticle.circle(getViewers(Radius), pivot, 35, 16);
            SomSound.Tick.play(getViewers(Radius));
            double random = randomDouble(0, Math.PI*2);
            CustomLocation start = pivot.clone().addY(1.8);
            start.addXZ(Math.cos(random) * 35, Math.sin(random) * 35);
            boolean homing = false;
            for (PlayerData viewer : getViewers(Radius)) {
                if (viewer.getLocation().distance(start) > 35) {
                    start.lookLocation(viewer.getLocation());
                    homing = true;
                }
            }
            if (!homing) {
                start.lookLocation(pivot);
                start.addYaw((float) randomDouble(-10, 10));
            }
            start.setPitch(0);

            for (int j = 0; j < 3; j++) {
                predictParticle.line(getViewers(Radius), start, 70, 0.25, 4);
                SomTask.wait(250);
            }

            SomSound.Handgun.play(getViewers(Radius));
            SomRay ray = SomRay.rayLocationEntity(getTargets(), start, 70, 0.25, true);
            shotParticle.line(getViewers(Radius), start, ray.getOriginPosition(), 0.25, 3);
            for (SomEntity hitEntity : ray.getHitEntities()) {
                Damage.makeDamage(getEnemyData(), hitEntity, DamageEffect.Fire, DamageOrigin.ATK, 0.70);
            }
        });
    }

    private boolean russianRoulette = true;
    private final Location[] russianRouletteLocation = {
            new Location(SomCore.World, 474.5, -9.9, 500.5),
            new Location(SomCore.World, 477.5, -9.9, 487.5),
            new Location(SomCore.World, 487.5, -9.9, 477.5),
            new Location(SomCore.World, 500.5, -9.9, 474.5),
    };
    private final String[] russianRouletteColor = {"§d", "§c", "§6", "§e"};
    private final String[] russianRouletteMessage = {"§c「危険には価値がある」", "§c「大きなリターンにはリスクがつくものです」", "§c「自分の運命を人に任せるのですか？」"};
    public void RussianRoulette() {
        russianRoulette = false;
        sendBossSkillMessage(russianRouletteMessage);
        SomTask.delay(() -> {
            Collection<PlayerData> players = getViewers(Radius);
            HashMap<PlayerData, Integer> index = new HashMap<>();
            SomTask.sync(() -> {
                for (PlayerData playerData : players) {
                    switch (getDifficulty()) {
                        case Easy -> playerData.sendBossBarMessage("§d魔法陣§aの外へ出るな", 120, true);
                        case Normal, Hard, Expert -> {
                            index.put(playerData, randomInt(0, 4));
                            playerData.sendBossBarMessage("§e指定§aの" + russianRouletteColor[index.get(playerData)] + "魔法陣§aへ§c避難§aしてください",120, true);
                            playerData.teleport(new Location(SomCore.World, 500.5, -9.9, 525, -180, 0));
                        }
                    }
                }
                BukkitTask task;
                if (getDifficulty() == DungeonDifficulty.Normal) {
                    SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK);
                    task = SomTask.timer(() -> {
                        for (PlayerData playerData : players) {
                            particle.circle(Collections.singleton(playerData), russianRouletteLocation[index.get(playerData)], 5);
                        }
                    }, 5);
                } else {
                    task = null;
                }
                SomTask.delay(() -> {
                    if (isInvalid()) return;
                    if (task != null) task.cancel();
                    if (getDifficulty() == DungeonDifficulty.Easy) {
                        for (PlayerData playerData : players) {
                            if (pivot.distance(playerData.getLocation()) > 35) {
                                playerData.death("§c§nロシアンルーレット§aにより§4死亡§aしました");
                            } else {
                                playerData.sendMessage("§d魔法陣§aが§c§nロシアンルーレット§aによる§4§n死§aを防ぎました", SomSound.Tick);
                            }
                        }
                    } else  {
                        for (PlayerData playerData : players) {
                            if (russianRouletteLocation[index.get(playerData)].distance(playerData.getLocation()) > 5) {
                                playerData.death("§c§nロシアンルーレット§aにより§4死亡§aしました");
                            } else {
                                playerData.sendMessage(russianRouletteColor[index.get(playerData)] + "魔法陣§aが§c§nロシアンルーレット§aによる§4§n死§aを防ぎました", SomSound.Tick);
                            }
                        }
                    }
                }, 120);
            });
        }, 60);
    }
}
