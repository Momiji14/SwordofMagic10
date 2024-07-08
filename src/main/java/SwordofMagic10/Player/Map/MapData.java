package SwordofMagic10.Player.Map;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.DungeonMenu;
import SwordofMagic10.Player.Dungeon.SpawnerData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.QuickGUI.GateMenu;
import SwordofMagic10.Player.QuickGUI.QuickGUI;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Player.QuickGUI.GateMenu.MapClearCheck;

public class MapData {
    private String id;
    private String prefix;
    private String suffix;
    private Material icon;
    private Integer slot;
    private MapData nextMap;
    private MapData prevMap;
    private DungeonDifficulty difficulty;
    private int minLevel;
    private int maxLevel;
    private CustomLocation startLocation;
    private CustomLocation goalLocation;
    private CustomLocation gateLocation;
    private final List<SpawnerData> spawners = new ArrayList<>();
    private final HashMap<SpawnerData, List<EnemyData>> enemies = new HashMap<>();
    private final Set<PlayerData> playerList = new CopyOnWriteArraySet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDisplay() {
        return prefix+suffix;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public boolean hasSlot() {
        return slot != null;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean hasNextMap() {
        return nextMap != null;
    }

    public MapData getNextMap() {
        return nextMap;
    }

    public void setNextMap(MapData nextMap) {
        this.nextMap = nextMap;
    }

    public boolean hasPrevMap() {
        return prevMap != null;
    }

    public MapData getPrevMap() {
        return prevMap;
    }

    public void setPrevMap(MapData prevMap) {
        this.prevMap = prevMap;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public CustomLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(CustomLocation startLocation) {
        this.startLocation = startLocation;
    }

    public CustomLocation getGoalLocation() {
        return goalLocation;
    }

    public void setGoalLocation(CustomLocation goalLocation) {
        this.goalLocation = goalLocation;
    }

    public CustomLocation getGateLocation() {
        return gateLocation;
    }

    public void setGateLocation(CustomLocation gateLocation) {
        this.gateLocation = gateLocation;
    }

    public void addSpawner(SpawnerData spawnerData) {
        spawners.add(spawnerData);
    }

    public List<EnemyData> getEnemies() {
        List<EnemyData> enemyList = new ArrayList<>();
        enemies.values().forEach(enemyList::addAll);
        return enemyList;
    }

    public Set<PlayerData> getPlayerList() {
        playerList.removeIf(playerData -> !playerData.isOnline());
        Set<PlayerData> list = new HashSet<>(playerList);
        list.removeIf(playerData -> playerData.getDungeonMenu().isInDungeon());
        return list;
    }

    public void teleportStart(PlayerData playerData) {
        playerData.teleport(startLocation);
        enter(playerData);
    }

    public void teleportGoal(PlayerData playerData) {
        playerData.teleport(goalLocation);
        enter(playerData);
    }

    public void teleportGate(PlayerData playerData) {
        playerData.teleport(gateLocation);
        enter(playerData);
    }

    public String levelText() {
        return "§eLv" + (minLevel == maxLevel ? minLevel : minLevel + "~" + maxLevel);
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(icon).setCustomData("MapData", id);
        item.setDisplay(getDisplay());
        item.addLore("§7・" + levelText());
        return item;
    }

    public void enter(PlayerData playerData) {
        MapData currentMap = playerData.getMapData();
        if (currentMap != this) {
            if (currentMap != null) currentMap.playerList.remove(playerData);
            playerData.setMapData(this);
            playerList.add(playerData);
        }
        if (!enemies.isEmpty()) {
            SomTask.sync(() -> enemies.values().forEach(enemies -> enemies.forEach(enemyData -> playerData.getPlayer().showEntity(SomCore.plugin(), enemyData.getLivingEntity()))));
        }
        SomTask.delay(() -> {
            playerData.sendTitle("§e" + getDisplay(), levelText(), 10, 60, 10);
            SomSound.Warp.play(playerData);
        }, 2);
    }

    public void useGate(PlayerData playerData) {
        if (hasNextMap() && getGoalLocation().distance(playerData.getLocation()) < 2) {
            if (MapClearCheck(playerData, nextMap)) {
                if (playerData.getRawLevel() >= nextMap.getMinLevel()) {
                    nextMap.teleportStart(playerData);
                    return;
                } else playerData.sendMessage("§eレベル§aが足りないため§cボス§a倒して移動してください", SomSound.Nope);
            }
            playerData.getDungeonMenu().open(false);
        } else if (hasPrevMap() && getStartLocation().distance(playerData.getLocation()) < 2) {
            prevMap.teleportGoal(playerData);
        } else if (gateLocation != null && gateLocation.distance(playerData.getLocation()) < 2) {
            GateMenu.open(playerData);
        }
    }

    public void start() {
        if (!spawners.isEmpty()) {
            for (SpawnerData spawnerData : spawners) {
                enemies.put(spawnerData, new ArrayList<>());
            }
            final MapData mapData = this;
            new BukkitRunnable() {
                final int difLevel = maxLevel - minLevel;
                @Override
                public void run() {
                    for (SpawnerData spawnerData : spawners) {
                        List<PlayerData> players = SomEntity.nearPlayer(getPlayerList(), spawnerData.getLocation(), spawnerData.getRadius() + 12);
                        if (!players.isEmpty()) {
                            if (spawnerData.getMaxEnemy() >= enemies.get(spawnerData).size() + spawnerData.getPerSpawn()) {
                                double minOffsetLevel = spawnerData.getMinOffsetLevel();
                                double maxOffsetLevel = spawnerData.getMinOffsetLevel();
                                for (int i = 0; i < spawnerData.getPerSpawn(); i++) {
                                    double levelMultiply = minOffsetLevel == maxOffsetLevel ? minOffsetLevel : randomDouble(minOffsetLevel, maxOffsetLevel);
                                    int level = (int) (minLevel + (difLevel * levelMultiply));
                                    CustomLocation location = spawnerData.getLocation().clone();
                                    location.setPitch(0);
                                    location.setYaw((float) randomDouble(-180, 180));
                                    location.add(location.getDirection().multiply(randomDouble(0, spawnerData.getRadius())));
                                    SomTask.sync(() -> {
                                        EnemyData enemyData = EnemyData.spawn(spawnerData.randomMobData(), level, getDifficulty(), location, getPlayerList(), mapData);
                                        enemies.get(spawnerData).add(enemyData);
                                    });
                                }
                            }
                        } else {
                            enemies.get(spawnerData).forEach(EnemyData::delete);
                        }
                        enemies.get(spawnerData).removeIf(EnemyData::isInvalid);
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 30, 30);
        }
        if (gateLocation != null) {
            new BukkitRunnable() {
                double i = 0;
                final SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, null);

                @Override
                public void run() {
                    for (int j = 0; j < 4; j++) {
                        double x = Math.cos(i) * 2;
                        double z = Math.sin(i) * 2;
                        Location[] locations = {
                                gateLocation.clone().add(x, 0, z),
                                gateLocation.clone().add(-x, 0, -z),
                        };
                        particle.setVector(SomParticle.VectorUp);
                        particle.setSpeed(0.15f);
                        for (Location loc : locations) {
                            particle.spawn(SomEntity.nearPlayer(getPlayerList(), gateLocation, 32), loc);
                        }
                        i += 0.05;
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 1);
        }

        if (hasNextMap()) {
            new BukkitRunnable() {
                double i = 0;
                final SomParticle particle = new SomParticle(Particle.SPELL_WITCH, null);

                @Override
                public void run() {
                    for (int j = 0; j < 4; j++) {
                        double x = Math.cos(i) * 2;
                        double z = Math.sin(i) * 2;
                        Location[] locations = {
                                goalLocation.clone().add(x, 0, z),
                                goalLocation.clone().add(-x, 0, -z),
                        };
                        particle.setVector(SomParticle.VectorUp);
                        particle.setSpeed(0.15f);
                        for (Location loc : locations) {
                            particle.spawn(SomEntity.nearPlayer(getPlayerList(), goalLocation, 32), loc);
                        }
                        i += 0.05;
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 1);
        }

        if (hasPrevMap()) {
            new BukkitRunnable() {
                double i = 0;
                final SomParticle particle = new SomParticle(Particle.SPELL_WITCH, null);

                @Override
                public void run() {
                    for (int j = 0; j < 4; j++) {
                        double x = Math.cos(i) * 2;
                        double z = Math.sin(i) * 2;
                        Location[] locations = {
                                startLocation.clone().add(x, 0, z),
                                startLocation.clone().add(-x, 0, -z),
                        };
                        particle.setVector(SomParticle.VectorUp);
                        particle.setSpeed(0.15f);
                        for (Location loc : locations) {
                            particle.spawn(SomEntity.nearPlayer(getPlayerList(), startLocation, 32), loc);
                        }
                        i += 0.05;
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 1);
        }
    }
}
