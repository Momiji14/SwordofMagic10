package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Dungeon.Instance.BattleArea;
import SwordofMagic10.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Dungeon.Instance.DungeonReward;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.Classes.Classes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.World;

public class DungeonDataLoader {
    private static final HashMap<String, DungeonInstance> dungeonList = new HashMap<>();
    private static final List<DungeonInstance> list = new ArrayList<>();
    public static DungeonInstance getDungeon(String id) {
        if (!dungeonList.containsKey(id)) Log("§c存在しないDungeonが参照されました -> " + id);
        return dungeonList.get(id);
    }

    public static List<DungeonInstance> getDungeonList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (DungeonInstance dungeon : getDungeonList()) {
            complete.add(dungeon.getId());
        }
        return complete;
    }

    public static void load() {
        dungeonList.clear();
        list.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "Dungeon"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                String display = data.getString("Display");
                Material icon = Material.valueOf(data.getString("Icon"));
                DungeonInstance dungeon = new DungeonInstance(id, icon, display, true);
                dungeon.setIndex(data.getInt("Index"));
                String locationKey = "StartLocation.";
                dungeon.setStartLocation(data.getDouble(locationKey + "x"),data.getDouble(locationKey + "y"), data.getDouble(locationKey + "z"), (float) data.getDouble(locationKey + "yaw"), (float) data.getDouble(locationKey + "pitch"));
                locationKey = "GoalLocation.";
                dungeon.setGoalLocation(data.getDouble(locationKey + "x"),data.getDouble(locationKey + "y"), data.getDouble(locationKey + "z"), 0, 0);
                locationKey = "BossStartLocation.";
                dungeon.setBossStartLocation(data.getDouble(locationKey + "x"),data.getDouble(locationKey + "y"), data.getDouble(locationKey + "z"), (float) data.getDouble(locationKey + "yaw"), (float) data.getDouble(locationKey + "pitch"));
                locationKey = "BossSpawnLocation.";
                dungeon.setBossSpawnLocation(data.getDouble(locationKey + "x"),data.getDouble(locationKey + "y"), data.getDouble(locationKey + "z"), (float) data.getDouble(locationKey + "yaw"), (float) data.getDouble(locationKey + "pitch"));
                dungeon.setDungeonLevel(data.getInt("DungeonLevel"));
                dungeon.setBossData(MobDataLoader.getMobData(data.getString("BossData")));
                if (data.isSet("Reward.Series")) {
                    for (String series : data.getStringList("Reward.Series")) {
                        for (SomEquipment equipment : ItemDataLoader.getSeries(series)) {
                            dungeon.addRewardItem(equipment);
                        }
                    }
                }
                for (DungeonDifficulty difficulty : DungeonDifficulty.values()) {
                    if (data.isSet(difficulty + ".BaseLevel")) {
                        dungeon.setBaseLevel(difficulty, data.getInt(difficulty + ".BaseLevel"));
                        dungeon.setBossLevel(difficulty, data.getInt(difficulty + ".BossLevel"));
                        dungeon.setLevelSync(difficulty, data.getInt(difficulty + ".LevelSync"));
                        DungeonReward reward = new DungeonReward();
                        if (data.isSet(difficulty + ".Reward.Exp")) {
                            reward.setExp(data.getInt(difficulty + ".Reward.Exp"));
                        } else {
                            int level = dungeon.getBaseLevel(difficulty);
                            reward.setExp(500 + level*100 + (Classes.getReqExp(level) / (5+level)));
                        }
                        if (data.isSet(difficulty + ".Reward.UpgradeStone.Tier")) {
                            reward.setUpgradeStone(data.getInt(difficulty + ".Reward.UpgradeStone.Tier"), data.getDouble(difficulty + ".Reward.UpgradeStone.Percent"));
                        } else {
                            reward.setUpgradeStone(difficulty.ordinal()+1, 10.2 + dungeon.getIndex());
                        }
                        if (data.isSet(difficulty + ".Reward.TierStone.Tier")) {
                            reward.setTierStone(data.getInt(difficulty + ".Reward.TierStone.Tier"), data.getDouble(difficulty + ".Reward.TierStone.Percent"));
                        } else {
                            reward.setTierStone(difficulty.ordinal()+1, 0.1 + dungeon.getIndex() * 0.01);
                        }
                        if (data.isSet(difficulty + ".Reward.QualityStone.Tier")) {
                            reward.setQualityStone(data.getInt(difficulty + ".Reward.QualityStone.Tier"), data.getDouble(difficulty + ".Reward.QualityStone.Percent"));
                        } else {
                            reward.setQualityStone(difficulty.ordinal()+1, 0.3 + dungeon.getIndex() * 0.01);
                        }
                        dungeon.setReward(difficulty, reward);
                    }
                }
                int i = 0;
                while (data.isSet("BattleArea-" + i + ".EnterRadius")) {
                    BattleArea battleArea = new BattleArea();
                    battleArea.setRadius(data.getDouble("BattleArea-" + i + ".EnterRadius"));
                    locationKey = "BattleArea-" + i + ".EnterLocation.";
                    battleArea.setEnterLocation(new Location(World, data.getDouble(locationKey + "x", 0), data.getDouble(locationKey + "y", 0), data.getDouble(locationKey + "z", 0)));
                    for (String strData : data.getStringList("BattleArea-" + i + ".SpawnEnemy")) {
                        String[] split = strData.split(",");
                        String enemyId = split[0];
                        int count = 0;
                        double radius = 0;
                        Location location = new Location(World, 0, 0, 0);
                        for (String str : split) {
                            String[] split2 = str.split(":");
                            switch (split2[0]) {
                                case "Count" -> count = Integer.parseInt(split2[1]);
                                case "X" -> location.setX(Double.parseDouble(split2[1]));
                                case "Y" -> location.setY(Double.parseDouble(split2[1]));
                                case "Z" -> location.setZ(Double.parseDouble(split2[1]));
                                case "Radius" -> radius = Integer.parseInt(split2[1]);
                            }
                        }
                        battleArea.addTable(new BattleArea.Table(MobDataLoader.getMobData(enemyId), count, location, radius));
                    }
                    dungeon.addBattleArea(battleArea);
                    i++;
                }
                dungeonList.put(id, dungeon);
                list.add(dungeon);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }
        list.sort(Comparator.comparing(dungeon -> dungeon.getBaseLevel(DungeonDifficulty.Easy)));
    }
}
