package SwordofMagic10.Player.Dungeon.Instance;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.DungeonDataLoader;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.DungeonMenu;
import SwordofMagic10.Entity.Enemy.DropData;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.Dungeon.SpawnerData;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestDungeonClear;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.*;

public class DungeonInstance implements Cloneable {
    public static final double Radius = 256;
    public static final CustomLocation DungeonEnter = new CustomLocation(World, 120.5, -28, 254.5);
    public static final CustomLocation RaidEnter = new CustomLocation(World, 79.5, -29, 165.5);
    private static final List<DungeonInstance> dungeonInstanceList = new ArrayList<>();
    private static final HashMap<String, DungeonInstance> dungeonInstance = new HashMap<>();

    public static List<DungeonInstance> getDungeonInstanceList() {
        return dungeonInstanceList;
    }

    public static DungeonInstance get(String id) {
        return dungeonInstance.get(id);
    }

    public static List<String> complete() {
        List<String> complete = new ArrayList<>();
        for (DungeonInstance instance : dungeonInstanceList) {
            complete.add(instance.id);
        }
        return complete;
    }

    public static void updateJoinStatus() {
        SomTask.timer(() -> {
            SomSQL.delete("DungeonJoinStatus", "Server", ID);
            HashMap<String, HashMap<DungeonDifficulty, Integer>> count = new HashMap<>();
            for (DungeonInstance dungeon : dungeonInstanceList) {
                count.putIfAbsent(dungeon.getId(), new HashMap<>());
                if (dungeon.getDifficulty() != null) {
                    count.get(dungeon.getId()).merge(dungeon.getDifficulty(), dungeon.getMember().size(), Integer::sum);
                }
            }
            count.forEach((dungeon, map) -> map.forEach((difficulty, memberCount) -> SomSQL.setSql("DungeonJoinStatus", new String[]{"Server", "Dungeon", "Difficulty"}, new String[]{ID, dungeon, difficulty.toString()}, "Count", memberCount)));

        }, 50, 50);
    }

    private final String id;
    private final Material icon;
    private final String display;
    private List<String> lore = new ArrayList<>();
    private DungeonDifficulty difficulty;
    private int index = 0;
    private boolean laterJoin;
    private DungeonMenu.MatchType matchType = DungeonMenu.MatchType.Party;
    private boolean goalFlag = false;
    private boolean bossFlag = false;
    private boolean clearFlag = false;
    private Type type = DungeonInstance.Type.Normal;
    private long startTime = 0;
    protected int phase = 0;
    protected List<PlayerData> member = new CopyOnWriteArrayList<>();
    protected List<EnemyData> enemies = new CopyOnWriteArrayList<>();
    private final List<SpawnerData> spawners = new ArrayList<>();
    private ConcurrentHashMap<SpawnerData, List<EnemyData>> spawnerEnemy = new ConcurrentHashMap<>();
    private int dungeonLevel;
    private final List<SomEquipment> rewardItem = new ArrayList<>();
    private List<String> series = new ArrayList<>();
    private final HashMap<DungeonDifficulty, DungeonReward> reward = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> baseLevel = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> bossLevel = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> itemLevel = new HashMap<>();
    private final HashMap<DungeonDifficulty, Integer> levelSync = new HashMap<>();
    private CustomLocation startLocation;
    private CustomLocation goalLocation;
    private CustomLocation bossStartLocation;
    private CustomLocation bossSpawnLocation;
    private MobData bossData;
    private EnemyData bossEnemy;
    private final List<BattleArea> battleArea = new ArrayList<>();
    private CustomLocation spawnLocation;
    private HashMap<PlayerData, Integer> deathCount = new HashMap<>();
    private final MapData mapData;

    public DungeonInstance(String id, Material icon, String display, boolean laterJoin) {
        this.id = id;
        this.icon = icon;
        this.display = display;
        this.laterJoin = laterJoin;
        dungeonInstanceList.add(this);
        dungeonInstance.put(id, this);
        mapData = MapDataLoader.getMapData(id);
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

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
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

    public boolean isGoalFlag() {
        return goalFlag;
    }

    public boolean isBossFlag() {
        return bossFlag;
    }

    public boolean isClearFlag() {
        return clearFlag;
    }

    public boolean isLegendRaid() {
        return type == DungeonInstance.Type.LegendRaid;
    }

    public boolean isFreeSearch() {
        return type == DungeonInstance.Type.FreeSearch;
    }

    public long getStartTime() {
        return startTime;
    }

    public void resetStartTime() {
        startTime = System.currentTimeMillis();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isJoinAble() {
        return !bossFlag && isPartyJoinAble();
    }

    public boolean isPartyJoinAble() {
        if (isLegendRaid() && bossFlag) {
            return false;
        }
        return !clearFlag && matchType != DungeonMenu.MatchType.Solo;
    }

    public boolean isLaterJoin() {
        return laterJoin;
    }

    public void setLaterJoin(boolean laterJoin) {
        this.laterJoin = laterJoin;
    }

    public DungeonMenu.MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(DungeonMenu.MatchType matchType) {
        this.matchType = matchType;
        if (matchType == DungeonMenu.MatchType.FreeSearch) {
            setType(DungeonInstance.Type.FreeSearch);
        }
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

    public int getItemLevel() {
        return itemLevel.get(getDifficulty());
    }

    public int getItemLevel(DungeonDifficulty difficulty) {
        return itemLevel.get(difficulty);
    }

    public void setItemLevel(DungeonDifficulty difficulty, int itemLevel) {
        this.itemLevel.put(difficulty, itemLevel);
    }

    public List<SomEquipment> getRewardItem() {
        return rewardItem;
    }

    public void addRewardItem(SomEquipment item) {
        rewardItem.add(item);
    }

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
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

    public CustomLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(double x, double y, double z, float yaw, float pitch) {
        startLocation = createLocation(x, y, z, yaw, pitch);
        spawnLocation = startLocation;
    }

    public CustomLocation getGoalLocation() {
        return goalLocation;
    }

    public void setGoalLocation(double x, double y, double z, float yaw, float pitch) {
        goalLocation = createLocation(x, y, z, yaw, pitch);
    }

    public CustomLocation getBossStartLocation() {
        return bossStartLocation;
    }

    public void setBossStartLocation(double x, double y, double z, float yaw, float pitch) {
        bossStartLocation = createLocation(x, y, z, yaw, pitch);
    }

    public CustomLocation getBossSpawnLocation() {
        return bossSpawnLocation;
    }

    public void setBossSpawnLocation(double x, double y, double z, float yaw, float pitch) {
        bossSpawnLocation = createLocation(x, y, z, yaw, pitch);
    }

    public int getDeathCount(PlayerData playerData) {
        return deathCount.getOrDefault(playerData, 0);
    }

    public void addDeathCount(PlayerData playerData) {
        deathCount.merge(playerData, 1, Integer::sum);
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

    public void addSpawner(SpawnerData spawnerData) {
        spawners.add(spawnerData);
        spawnerEnemy.put(spawnerData, new ArrayList<>());
    }

    public void setSpawn(CustomLocation location) {
        for (PlayerData playerData : member) {
            playerData.setPlayerSpawn(location);
        }
        spawnLocation = location;
    }

    public void joinPlayer(PlayerData playerData) {
        playerData.setLevelSync(getLevelSync());
        playerData.setTierSync(getDifficulty().ordinal()+1);
        playerData.setPlayerSpawn(spawnLocation);
        playerData.getDungeonMenu().setDungeon(this);
        playerData.updateStatus();
        addMember(playerData);
        sendMessage(playerData.getDisplayName() + "§aが§b参加§aしました", SomSound.Tick);
        SomTask.sync(() -> {
            playerData.teleport(spawnLocation.clone().lookLocation(member.get(0).getLocation()));
            for (EnemyData enemy : enemies) {
                playerData.getPlayer().showEntity(SomCore.plugin(), enemy.getLivingEntity());
            }
            for (PlayerData member : member) {
                member.getPlayer().showPlayer(SomCore.plugin(), playerData.getPlayer());
            }
            for (EnemyData enemy : mapData.getEnemies()) {
                playerData.getPlayer().hideEntity(SomCore.plugin(), enemy.getLivingEntity());
            }
            if (type != DungeonInstance.Type.FreeSearch) {
                countBar.addPlayer(playerData.getPlayer());
                for (SpawnerData spawner : spawners) {
                    for (EnemyData enemy : spawnerEnemy.get(spawner)) {
                        playerData.getPlayer().showEntity(SomCore.plugin(), enemy.getLivingEntity());
                    }
                }
            }
        });
        dungeonStart();
    }

    public void leavePlayer(PlayerData playerData) {
        resetPlayer(playerData, false, false);
        if (member.contains(playerData)) {
            member.remove(playerData);
            playerData.sendMessage( "§cリタイア§aしました");
            sendMessage(playerData.getDisplayName() + "§aが§cリタイア§aしました", SomSound.Tick);
        }
        if (member.isEmpty()) {
            deleteInstance();
        }
    }

    public void resetPlayer(PlayerData playerData, boolean isClear, boolean retry) {
        playerData.resetLevelSync();
        playerData.resetTierSync();
        playerData.resetLocationLock();
        playerData.getDungeonMenu().setDungeon(null);
        playerData.updateStatus();
        playerData.heal();
        countBar.removePlayer(playerData.getPlayer());
        SomTask.sync(() -> {
            for (PlayerData player : PlayerData.getPlayerList()) {
                if (player != playerData) {
                    playerData.getPlayer().showPlayer(SomCore.plugin(), player.getPlayer());
                }
            }
            if (retry) {
                playerData.getDungeonMenu().joinDungeon(DungeonDataLoader.getDungeon(id), getDifficulty());
            } else if (isClear && playerData.getMapData().hasNextMap()) {
                playerData.getMapData().getNextMap().teleportStart(playerData);
            } else {
                CustomLocation location = isLegendRaid() ? RaidEnter : playerData.getMapData().getGateLocation();
                playerData.teleport(location);
                playerData.setPlayerSpawn(location);
            }
        });
    }

    public void deleteInstance() {
        dungeonTask.cancel();
        if (warpGateTask != null) warpGateTask.cancel();
        for (EnemyData enemy : enemies) {
            enemy.delete();
        }
        DungeonInstance.getDungeonInstanceList().remove(this);
    }

    public void breakInstance() {
        SomTask.delay(() -> {
            for (PlayerData member : getMember()) {
                if (member.isDeath()) {
                    member.setRespawn(true);
                }
                resetPlayer(member, false, false);
            }
        }, 200);
        deleteInstance();
    }

    public List<EnemyData> getEnemies() {
        return enemies;
    }

    public void addEnemies(EnemyData enemyData){
        enemies.add(enemyData);
    }

    private boolean startTrigger = false;
    private int startCount;
    protected BukkitTask dungeonTask;
    private BukkitTask warpGateTask;
    public void dungeonStart() {
        if (!startTrigger) {
            startTrigger = true;
            if (type == DungeonInstance.Type.FreeSearch) {
                dungeonTask = new BukkitRunnable() {
                    int index = 0;
                    @Override
                    public void run() {
                        spawnerEnemy.values().forEach(list -> list.removeIf(EnemyData::isInvalid));
                        enemies.removeIf(EnemyData::isInvalid);
                        for (int i = index; i < spawners.size(); i++) {
                            index++;
                            SpawnerData spawnerData = spawners.get(i);
                            CustomLocation spawnLocation = spawnerData.getLocation().clone();
                            spawnLocation.addY(spawnerData.getRadiusY());
                            if (!SomEntity.nearPlayer(member, spawnLocation, spawnerData.getRadius() + 12).isEmpty()) {
                                if (enemies.size() < 10 && spawnerData.getMaxEnemy() - spawnerEnemy.get(spawnerData).size() >= spawnerData.getPerSpawn()) {
                                    SomTask.sync(() -> {
                                        List<EnemyData> enemyList = dungeonEnemySpawn(spawnerData.randomMobData(), spawnerData.getPerSpawn(), getBaseLevel(), spawnLocation, spawnerData.getRadius());
                                        spawnerEnemy.get(spawnerData).addAll(enemyList);
                                        enemies.addAll(enemyList);
                                    });
                                    break;
                                }
                            } else {
                                for (EnemyData enemyData : spawnerEnemy.get(spawnerData)) {
                                    enemyData.delete();
                                }
                            }
                        }
                        if (spawners.size()-1 <= index) index = 0;
                        if (member.isEmpty()) {
                            deleteInstance();
                        }
                    }
                }.runTaskTimerAsynchronously(SomCore.plugin(), 30, 30);
            } else {
                double levelDiff = getBossLevel() - getBaseLevel();
                double totalCount = 0;
                for (BattleArea area : getBattleArea()) {
                    for (BattleArea.Table table : area.getTable()) {
                        totalCount += table.count();
                    }
                }
                perLevel = levelDiff / totalCount;
                startCount = 5;
                for (PlayerData playerData : member) {
                    playerData.setLocationLock(startLocation);
                    playerData.sendTitle("§aReady", "", 0, 50, 5);
                    playerData.getSkillManager().clearCoolTime();
                }
                dungeonTask = SomTask.syncTimer(() -> {
                    if (startCount > 0) {
                        for (PlayerData playerData : member) {
                            playerData.setLocationLock(startLocation);
                            playerData.sendTitle("§a" + startCount, "", 0, 20, 5);
                            SomSound.Tick.play(playerData);
                        }
                    } else if (startCount == 0) {
                        for (PlayerData playerData : member) {
                            playerData.resetLocationLock();
                            playerData.sendTitle("§aStart !", "", 0, 30, 10);
                            SomSound.BossSpawn.play(playerData);
                        }
                        dungeonTask.cancel();
                        dungeonTask();
                    }
                    startCount--;
                }, 50, 20);
            }
        }
    }

    private double i = 0;
    private SomParticle particle = new SomParticle(Color.RED, null);

    public void dungeonTask() {
        startTime = System.currentTimeMillis();
        dungeonTask = SomTask.syncTimer(() -> {
            enemies.removeIf(EnemyData::isInvalid);
            battleAreaTick();
            boolean allDeath = true;
            for (PlayerData playerData : getMember()) {
                if (!playerData.isInvalid()) {
                    allDeath = false;
                    break;
                }
            }
            if (bossEnemy != null) {
                if (bossEnemy.isDeath()) {
                    sendBossAreaClear();
                    deleteInstance();
                    SomTask.delay(this::dungeonClear, 50);
                } else {
                    if (allDeath || bossEnemy.isInvalid()) {
                        bossEnemy.delete();
                        bossEnemy = null;
                        bossFlag = false;
                        sendMessage("§cボス戦中§aの§eプレイヤー§aが§4全滅§aしたため§eリセット§aされます", SomSound.Tick);
                        for (PlayerData playerData : member) {
                            if (playerData.getSetting().isDefeatMessage()) playerData.sendMessage("§a勝てないときは§e「ギミックの理解・装備の強化・バフアイテムの使用」§aが§c重要§aだ！");
                            playerData.getSkillManager().clearCoolTime();
                            playerData.clearEffectNonPotion();
                        }
                    }
                }
            } else if (!enemies.isEmpty()) {
                if (!countBar.isVisible()) countBar.setVisible(true);
                countBar.setTitle("§c残存エネミー数 " + enemies.size());
            } else if (countBar.isVisible()){
                countBar.setVisible(false);
            }
            if (member.isEmpty()) {
                deleteInstance();
            }
            if (isLegendRaid() && allDeath) {
                breakInstance();
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

    public static final String DungeonWorldRecord = "DungeonWorldRecord";
    public static final String[] priKey = new String[]{"Dungeon", "Difficulty"};
    private String[] priValue() {
        return new String[]{id, difficulty.toString()};
    }
    private double clearTime = 0;

    public void clearTime() {
        clearTime = (System.currentTimeMillis()-startTime)/1000.0;
    }

    public void dungeonClear() {
        clearFlag = true;
        boolean isNew;
        if (SomSQL.existSql(DungeonWorldRecord, priKey, priValue())) {
            isNew = SomSQL.getDouble(DungeonWorldRecord, priKey, priValue(), "Time") > clearTime;
        } else isNew = true;
        if (isNew) {
            StringBuilder builder = new StringBuilder();
            SomJson json = new SomJson();
            for (PlayerData playerData : member) {
                builder.append(playerData.getDisplayName());
                json.addArray("UUID", playerData.getUUIDAsString());
                json.addArray("Username", playerData.getUsername());
                if (member.get(member.size()-1) != playerData) {
                    builder.append(",");
                }
            }
            SomText text = SomText.create(builder.toString()).add("§aの§eパーティ§aが§c" + getDisplay() + getDifficulty() + "§aの§eワールドレコード§aを§b更新§aしました §e[" + scale(clearTime, 3) + "秒]");
            SomCore.globalMessageComponent(text);
            SomSQL.setSql(DungeonWorldRecord, priKey, priValue(), "Time", clearTime);
            SomSQL.setSql(DungeonWorldRecord, priKey, priValue(), "Party", json.toString());
        }
        for (PlayerData playerData : member) {
            try {
                playerData.sendTitle("§b§nDungeon Clear !", playerData.getDungeonMenu().isAutoRetry() ? "§eリトライします" : "§e次の階層へ移動します", 20, 60, 0);
                playerData.getDungeonMenu().updateClearTime(this, clearTime);
                equipmentDrop(playerData, 1.0);
                getReward().give(playerData, getBossLevel());
                playerData.getStatistics().add(Statistics.Type.DungeonClear, 1);
                for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                    if (somQuest.getNowPhase() instanceof QuestDungeonClear questDungeonClear) {
                        questDungeonClear.clear(this);
                    }
                }
                playerData.sendMessage("§e" + display + "[" + difficulty + "]§aを§bクリア§aしました [" + scale(clearTime, 3) + "秒]", SomSound.BossDefeat);
            } catch (Exception e) {
                playerData.sendMessage("§cダンジョンクリア処理中にエラーが発生しました", SomSound.Nope);
                e.printStackTrace();
            }
        }
        SomTask.delay(() -> {
            for (PlayerData playerData : member) {
                resetPlayer(playerData, true, playerData.getDungeonMenu().isAutoRetry());
            }
        }, 80);
    }

    public void equipmentDrop(PlayerData playerData, double percent) {
        if (randomDouble(0, 1) <= percent) {
            SomEquipment equipment = rewardItem.get(randomInt(0, rewardItem.size()));
            equipment.setLevel(getItemLevel());
            equipment.setPlus(randomInt(0, 11));
            equipment.setTier(getDifficulty().ordinal() + 1);
            playerData.getItemInventory().add(equipment, 1, percent);
        }
    }

    public void activeGoal() {
        particle = new SomParticle(Particle.SPELL_WITCH, null);
        goalFlag = true;
    }

    public synchronized void enterBossGate(PlayerData playerData) {
        if (type != DungeonInstance.Type.FreeSearch) {
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
    }

    public void bossSpawn() {
        if (bossEnemy == null) {
            sendBossArea();
            try {
                bossEnemy = EnemyData.spawn(getBossData(), getBossLevel(), getDifficulty(), getBossSpawnLocation(), member, mapData, this);
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
    private BossBar countBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
    public void battleAreaTick() {
        if (getBattleArea().isEmpty()) activeGoal();
        if (enterBattleArea) {
            if (!enemies.isEmpty() && enemies.size() <= 5) {
                for (EnemyData enemy : enemies) {
                    enemy.getLivingEntity().setGlowing(true);
                }
            } else if (enemies.isEmpty()) {
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
                            enemies.addAll(dungeonEnemySpawn(table.mobdata(), 1, level, table.location(), table.radius()));
                            spawnCount++;
                        }
                    }
                    enterBattleArea = true;
                    return;
                }
            }
        }
    }

    public List<EnemyData> dungeonEnemySpawn(MobData mobData, int count, int level, double x, double y, double z, double radius) {
        return dungeonEnemySpawn(mobData, count, level, new Location(World, x, y, z), radius);
    }

    public List<EnemyData> dungeonEnemySpawn(MobData mobData, int count, int level, Location location, double radius) {
        List<EnemyData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Location spawn = location.clone();
            if (radius != 0) {
                spawn.setPitch(0);
                spawn.setYaw((float) randomDouble(-180, 180));
                spawn.add(spawn.getDirection().multiply(randomDouble(0, radius)));
            }
            EnemyData enemyData = EnemyData.spawn(mobData, level, difficulty, spawn, member, mapData, this);
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
            list.add(enemyData);
        }
        return list;
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
            clone.spawnerEnemy = new ConcurrentHashMap<>();
            for (SpawnerData spawnerData : clone.spawners) {
                clone.spawnerEnemy.put(spawnerData, new ArrayList<>());
            }
            clone.bossEnemy = null;
            clone.goalFlag = false;
            clone.bossFlag = false;
            clone.clearFlag = false;
            clone.phase = 0;
            clone.deathCount = new HashMap<>();
            clone.countBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
            clone.startTime = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum Type {
        Normal,
        LegendRaid,
        FreeSearch,
    }
}
