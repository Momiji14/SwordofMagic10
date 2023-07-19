package SwordofMagic10.Dungeon.Instance;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.DungeonDataLoader;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.DropData;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestDungeonClear;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.*;

public class DungeonInstance implements Cloneable {
    public static final double Radius = 256;
    public static final CustomLocation DungeonEnterNPC = new CustomLocation(World, 120.5, -28, 254.5);
    private static final List<DungeonInstance> dungeonInstance = new ArrayList<>();

    public static List<DungeonInstance> getDungeonInstance() {
        return dungeonInstance;
    }

    private final String id;
    private final Material icon;
    private final String display;
    private DungeonDifficulty difficulty;
    private int index = 0;
    private boolean laterJoin;
    private boolean goalFlag = false;
    private boolean bossFlag = false;
    private boolean clearFlag = false;
    protected int phase = 0;
    protected List<PlayerData> member = new ArrayList<>();
    protected List<EnemyData> enemies = new ArrayList<>();
    private int dungeonLevel;
    private final List<SomEquipment> rewardItem = new ArrayList<>();
    private final HashMap<DungeonDifficulty, DungeonReward> reward = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> baseLevel = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> bossLevel = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> levelSync = new HashMap<>();
    private Location startLocation;
    private Location goalLocation;
    private Location bossStartLocation;
    private Location bossSpawnLocation;
    private MobData bossData;
    private EnemyData bossEnemy;
    private final List<BattleArea> battleArea = new ArrayList<>();
    private Location spawnLocation;
    private int deathCount = 0;

    public DungeonInstance(String id, Material icon, String display, boolean laterJoin) {
        this.id = id;
        this.icon = icon;
        this.display = display;
        this.laterJoin = laterJoin;
        dungeonInstance.add(this);
    }

    public String getId() {
        return id;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplay() {
        return display;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLaterJoin() {
        return laterJoin;
    }

    public boolean isGoalFlag() {
        return goalFlag;
    }

    public boolean isBossFlag() {
        return bossFlag;
    }

    public boolean isClearFlag() {
        return clearFlag;
    }

    public boolean isJoinAble() {
        return laterJoin && !bossFlag && !clearFlag;
    }

    public void setLaterJoin(boolean laterJoin) {
        this.laterJoin = laterJoin;
    }

    public List<PlayerData> getMember() {
        return member;
    }

    public List<PlayerData> getAliveMember() {
        List<PlayerData> aliveMember = new ArrayList<>();
        for (PlayerData playerData : member) {
            if (!playerData.isDeath()) aliveMember.add(playerData);
        }
        return aliveMember;
    }

    public void addMember(PlayerData playerData) {
        member.add(playerData);
    }

    public List<DungeonDifficulty> activeDifficulty() {
        List<DungeonDifficulty> list = new ArrayList<>();
        for (DungeonDifficulty difficulty : DungeonDifficulty.values()) {
            if (baseLevel.keySet().contains(difficulty)) {
                list.add(difficulty);
            }
        }
        return list;
    }

    public int getDungeonLevel() {
        return dungeonLevel;
    }

    public void setDungeonLevel(int dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
    }

    public int getBaseLevel() {
        return baseLevel.get(getDifficulty());
    }

    public int getBaseLevel(DungeonDifficulty difficulty) {
        return baseLevel.get(difficulty);
    }

    public void setBaseLevel(DungeonDifficulty difficulty, int baseLevel) {
        this.baseLevel.put(difficulty, baseLevel);
    }

    public int getBossLevel() {
        return bossLevel.get(getDifficulty());
    }

    public int getBossLevel(DungeonDifficulty difficulty) {
        return bossLevel.get(difficulty);
    }

    public void setBossLevel(DungeonDifficulty difficulty, int bossLevel) {
        this.bossLevel.put(difficulty, bossLevel);
    }

    public int getLevelSync() {
        return levelSync.get(getDifficulty());
    }

    public int getLevelSync(DungeonDifficulty difficulty) {
        return levelSync.get(difficulty);
    }

    public void setLevelSync(DungeonDifficulty difficulty, int levelSync) {
        this.levelSync.put(difficulty, levelSync);
    }

    public List<SomEquipment> getRewardItem() {
        return rewardItem;
    }

    public void addRewardItem(SomEquipment item) {
        rewardItem.add(item);
    }

    public DungeonReward getReward() {
        return reward.get(getDifficulty());
    }

    public DungeonReward getReward(DungeonDifficulty difficulty) {
        return reward.get(difficulty);
    }

    public void setReward(DungeonDifficulty difficulty, DungeonReward reward) {
        this.reward.put(difficulty, reward);
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(double x, double y, double z, float yaw, float pitch) {
        startLocation = createLocation(x, y, z, yaw, pitch);
        spawnLocation = startLocation;
    }

    public Location getGoalLocation() {
        return goalLocation;
    }

    public void setGoalLocation(double x, double y, double z, float yaw, float pitch) {
        goalLocation = createLocation(x, y, z, yaw, pitch);
    }

    public Location getBossStartLocation() {
        return bossStartLocation;
    }

    public void setBossStartLocation(double x, double y, double z, float yaw, float pitch) {
        bossStartLocation = createLocation(x, y, z, yaw, pitch);
    }

    public Location getBossSpawnLocation() {
        return bossSpawnLocation;
    }

    public void setBossSpawnLocation(double x, double y, double z, float yaw, float pitch) {
        bossSpawnLocation = createLocation(x, y, z, yaw, pitch);
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void addDeathCount(int deathCount) {
        this.deathCount += deathCount;
    }

    public MobData getBossData() {
        return bossData;
    }

    public void setBossData(MobData bossData) {
        this.bossData = bossData;
    }

    public List<BattleArea> getBattleArea() {
        return battleArea;
    }

    public void addBattleArea(BattleArea battleArea) {
        this.battleArea.add(battleArea);
    }

    public void setSpawn(Location location) {
        for (PlayerData playerData : member) {
            playerData.setPlayerSpawn(location);
        }
        spawnLocation = location;
    }

    public void joinPlayer(PlayerData playerData) {
        playerData.setLevelSync(getLevelSync());
        playerData.setPlayerSpawn(spawnLocation);
        playerData.getDungeonMenu().setDungeon(this);
        playerData.updateStatus();
        addMember(playerData);
        sendMessage(playerData.getDisplayName() + "§aが§b参加§aしました", SomSound.Tick);
        SomTask.sync(() -> {
            playerData.teleport(spawnLocation);
            for (EnemyData enemy : enemies) {
                playerData.getPlayer().showEntity(SomCore.plugin(), enemy.getLivingEntity());
            }
            for (PlayerData member : member) {
                member.getPlayer().showPlayer(SomCore.plugin(), playerData.getPlayer());
            }
        });
        dungeonStart();
    }

    public void leavePlayer(PlayerData playerData) {
        resetPlayer(playerData, false);
        if (member.contains(playerData)) {
            for (PlayerData member : member) {
                member.sendMessage(playerData.getDisplayName() + "§aが§cリタイア§aしました");
                SomSound.Tick.play(member);
            }
            member.remove(playerData);
        }
        if (member.size() == 0) {
            deleteInstance();
        }
    }

    public void resetPlayer(PlayerData playerData, boolean retry) {
        playerData.heal();
        playerData.resetLevelSync();
        playerData.setLocationLock(false);
        playerData.setPlayerSpawn(SpawnLocation);
        playerData.getDungeonMenu().setDungeon(null);
        playerData.updateStatus();
        SomTask.sync(() -> {
            for (PlayerData player : PlayerData.getPlayerList()) {
                if (player != playerData) {
                    playerData.getPlayer().showPlayer(SomCore.plugin(), player.getPlayer());
                }
            }
            if (retry) {
                playerData.getDungeonMenu().joinDungeon(DungeonDataLoader.getDungeonList().get(getIndex()), getDifficulty());
            } else {
                playerData.teleport(DungeonEnterNPC);
            }
        });
    }

    public void deleteInstance() {
        dungeonTask.cancel();
        if (warpGateTask != null) warpGateTask.cancel();
        for (EnemyData enemy : enemies) {
            enemy.delete();
        }
        DungeonInstance.getDungeonInstance().remove(this);
    }

    public List<EnemyData> getEnemies() {
        return enemies;
    }

    private boolean startTrigger = false;
    private int startCount;
    protected BukkitTask dungeonTask;
    private BukkitTask warpGateTask;
    public void dungeonStart() {
        if (!startTrigger) {
            double levelDiff = getBossLevel() - getBaseLevel();
            double totalCount = 0;
            for (BattleArea area : getBattleArea()) {
                for (BattleArea.Table table : area.getTable()) {
                    totalCount += table.count();
                }
            }
            perLevel = levelDiff / totalCount;
            startTrigger = true;
            startCount = 5;
            for (PlayerData playerData : member) {
                playerData.setLocationLock(true);
                playerData.sendTitle("§aReady", "", 0, 30, 0);
            }
            dungeonTask = SomTask.syncTimer(() -> {
                if (startCount > 0) {
                    for (PlayerData playerData : member) {
                        playerData.setLocationLock(true);
                        playerData.sendTitle("§a" + startCount, "", 0, 30, 0);
                        SomSound.Tick.play(playerData);
                    }
                } else if (startCount == 0) {
                    for (PlayerData playerData : member) {
                        playerData.setLocationLock(false);
                        playerData.sendTitle("§aStart !", "", 0, 30, 10);
                        SomSound.BossSpawn.play(playerData);
                    }
                    dungeonTask.cancel();
                    dungeonTask();
                }
                startCount--;
            }, 20, 20);
        }
    }

    private double i = 0;
    private SomParticle particle = new SomParticle(Color.RED);

    public void dungeonTask() {
        dungeonTask = SomTask.syncTimer(() -> {
            enemies.removeIf(EnemyData::isInvalid);
            battleAreaTick();
            if (bossEnemy != null) {
                if (bossEnemy.isDeath()) {
                    sendBossAreaClear();
                    deleteInstance();
                    SomTask.delay(this::dungeonClear, 50);
                } else if (bossEnemy.isInvalid()) {
                    setSpawn(getStartLocation());
                    bossEnemy = null;
                    bossFlag = false;
                    sendMessage("§cボス戦中§aの§eプレイヤー§aが§4全滅§aしたため§eリセット§aされます", SomSound.Tick);
                }
            }
            if (member.size() == 0) {
                deleteInstance();
            }
        }, 20);
        Location location = getGoalLocation();
        warpGateTask = SomTask.timer(() -> {
            for (int j = 0; j < 4; j++) {
                double x = Math.cos(i) * 2;
                double z = Math.sin(i) * 2;
                Location[] locations = {
                        location.clone().add(x, 0, z),
                        location.clone().add(-x, 0, -z),
                };
                particle.setVector(SomParticle.VectorUp);
                particle.setSpeed(0.15f);
                for (Location loc : locations) {
                    particle.spawn(member, loc);
                }
                i += 0.05;
            }
        }, 1);
    }

    public void dungeonClear() {
        clearFlag = true;
        for (PlayerData playerData : member) {
            playerData.sendTitle("§b§nDungeon Clear !", playerData.getDungeonMenu().isAutoRetry() ? "§eリトライします" : "§e街へ帰還します", 20, 60, 0);
            double percent = 0.2;
            if (randomDouble(0, 1) < percent) {
                SomEquipment equipment = rewardItem.get(randomInt(0, rewardItem.size()));
                equipment.setLevel(getBossLevel());
                equipment.setTier(getDifficulty().ordinal()+1);
                equipment.randomQuality();
                equipment.setPlus(randomInt(0, 10+1));
                playerData.getItemInventory().add(equipment, 1);
                String percentText = " §7(" + scale((percent/rewardItem.size())*100, -1) + "%)";
                playerData.sendSomText(SomText.create("§b[+]§r").addText(equipment.toSomText(1)).addText(percentText));
            }
            getReward().give(playerData);
            for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                if (somQuest.getNowPhase() instanceof QuestDungeonClear questDungeonClear) {
                    questDungeonClear.clear(this);
                }
            }
            SomSound.Level.play(playerData);
        }
        SomTask.delay(() -> {
            for (PlayerData playerData : member) {
                resetPlayer(playerData, playerData.getDungeonMenu().isAutoRetry());
            }
        }, 80);
    }

    public void activeGoal() {
        particle = new SomParticle(Particle.SPELL_WITCH);
        goalFlag = true;
    }

    public synchronized void enterBossGate(PlayerData playerData) {
        Player player = playerData.getPlayer();
        if (getGoalLocation().distance(player.getLocation()) < 2) {
            if (goalFlag) {
                playerData.teleport(getBossStartLocation());
                if (!bossFlag) {
                    bossFlag = true;
                    setSpawn(getGoalLocation());
                    SomTask.syncDelay(this::bossSpawn, 20);
                }
            } else {
                playerData.sendMessage("§aすべての§eダンジョンクエスト§aを§bクリア§aしていません", SomSound.Nope);
            }
        }
    }

    public void bossSpawn() {
        if (bossEnemy == null) {
            sendBossArea();
            try {
                bossEnemy = EnemyData.spawn(getBossData(), getBossLevel(), getDifficulty(), getBossSpawnLocation(), member);
                enemies.add(bossEnemy);
            } catch (Exception e) {
                e.printStackTrace();
                bossEnemy = null;
                bossFlag = false;
                sendMessage("§cエラー§aにより§cBOSS§aが§b召喚§aされませんでした", SomSound.Nope);
            }
        }
    }

    public void sendMessage(String message, SomSound sound) {
        for (PlayerData playerData : member) {
            playerData.sendMessage(message, sound);
        }
    }

    private int battleAreaIndex = 0;
    private boolean enterBattleArea = false;
    private double perLevel = 0;
    private int spawnCount = 0;
    public void battleAreaTick() {
        if (enterBattleArea) {
            if (enemies.size() == 0) {
                sendBattleAreaClear();
                battleAreaIndex++;
                enterBattleArea = false;
                if (getBattleArea().size() <= battleAreaIndex) {
                    activeGoal();
                }
            }
        } else if (getBattleArea().size() > battleAreaIndex) {
            BattleArea battleArea = getBattleArea().get(battleAreaIndex);
            for (PlayerData playerData : member) {
                if (playerData.getLocation().distance(battleArea.getEnterLocation()) < battleArea.getRadius()) {
                    sendBattleArea();
                    setSpawn(battleArea.getEnterLocation());
                    for (BattleArea.Table table : battleArea.getTable()) {
                        for (int i = 0; i < table.count(); i++) {
                            int level = getBaseLevel() + (int) Math.floor(perLevel * spawnCount);
                            dungeonEnemySpawn(table.mobdata(), 1, level, table.location(), table.radius());
                            spawnCount++;
                        }

                    }
                    enterBattleArea = true;
                    return;
                }
            }
        }
    }

    public void dungeonEnemySpawn(MobData mobData, int count, int level, double x, double y, double z, double radius) {
        dungeonEnemySpawn(mobData, count, level, new Location(World, x, y, z), radius);
    }

    public void dungeonEnemySpawn(MobData mobData, int count, int level, Location location, double radius) {
        for (int i = 0; i < count; i++) {
            Location spawn = location.clone();
            if (radius != 0) {
                spawn.setPitch(0);
                spawn.setYaw((float) randomDouble(-180, 180));
                spawn.add(spawn.getDirection().multiply(randomDouble(0, radius)));
            }
            EnemyData enemyData = EnemyData.spawn(mobData, level, difficulty, spawn, member);
            getReward().getUpgradeStone().forEach((tier, amount) -> {
                SomItem item = ItemDataLoader.getItemData("精錬石");
                item.setTier(tier);
                DropData dropData = new DropData();
                dropData.setItem(item);
                dropData.setMaxAmount(1);
                dropData.setMinAmount(1);
                dropData.setPercent(amount/100f);
                enemyData.getDropDataList().add(dropData);
            });
            getReward().getTierStone().forEach((tier, amount) -> {
                SomItem item = ItemDataLoader.getItemData("昇級石");
                item.setTier(tier);
                DropData dropData = new DropData();
                dropData.setItem(item);
                dropData.setMaxAmount(1);
                dropData.setMinAmount(1);
                dropData.setPercent(amount/100f);
                enemyData.getDropDataList().add(dropData);
            });
            getReward().getQualityStone().forEach((tier, amount) -> {
                SomItem item = ItemDataLoader.getItemData("品質変更石");
                item.setTier(tier);
                DropData dropData = new DropData();
                dropData.setItem(item);
                dropData.setMaxAmount(1);
                dropData.setMinAmount(1);
                dropData.setPercent(amount/100f);
                enemyData.getDropDataList().add(dropData);
            });
            enemies.add(enemyData);
        }
    }

    public void areaTrigger(double x, double y, double z, double radius, Runnable runnable) {
        areaTrigger(new Location(World, x, y, z), radius, runnable);
    }

    public void areaTrigger(Location location, double radius, Runnable runnable) {
        for (PlayerData playerData : member) {
            if (playerData.getLocation().distance(location) < radius) {
                runnable.run();
                return;
            }
        }
    }

    public void sendBattleArea() {
        for (PlayerData member : member) {
            member.sendTitle("§e§nBattle Area", "§cエネミーを殲滅しろ", 5, 20, 5);
            SomSound.BossSpawn.play(member);
        }
    }

    public void sendBossArea() {
        for (PlayerData member : member) {
            member.sendTitle("§4§nBoss Area", "§cボスを討伐しろ", 5, 20, 5);
            SomSound.BossSpawn.play(member);
        }
    }

    public void sendBattleAreaClear() {
        for (PlayerData member : member) {
            member.sendTitle("§b§nBattle Area Clear", "", 5, 20, 5);
            SomSound.Level.play(member);
        }
    }

    public void sendBossAreaClear() {
        for (PlayerData member : member) {
            member.sendTitle("§b§nBoss Area Clear", "", 5, 20, 5);
            SomSound.Level.play(member);
        }
    }

    @Override
    public DungeonInstance clone() {
        try {
            DungeonInstance clone = (DungeonInstance) super.clone();
            clone.startTrigger = false;
            clone.member = new ArrayList<>();
            clone.enemies = new ArrayList<>();
            clone.bossEnemy = null;
            clone.goalFlag = false;
            clone.bossFlag = false;
            clone.clearFlag = false;
            clone.phase = 0;
            clone.deathCount = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
