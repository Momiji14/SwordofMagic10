package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Player.Achievement.AchievementData;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Dungeon.SpawnerData;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.loreText;
import static SwordofMagic10.SomCore.Log;

public class SpawnerDataLoader {


    private static final HashMap<String, SpawnerData> spawnerDataList = new HashMap<>();
    private static final List<SpawnerData> list = new ArrayList<>();

    public static SpawnerData getSpawnerData(String id) {
        if (!spawnerDataList.containsKey(id)) {
            Log("§c存在しないtSpawnerDataが参照されました -> " + id);
            return null;
        }
        return spawnerDataList.get(id);
    }

    public static List<SpawnerData> getSpawnerDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (SpawnerData spawnerData : getSpawnerDataList()) {
            complete.add(spawnerData.getId());
        }
        return complete;
    }

    private static final String[] dungeon = new String[]{"Luoria", "Bordera", "Nortles", "Welicia", "Tarneta", "Apogalve", "Eltafar", "Serenia", "Billher", "Pacifilim"};
    public static void load() {
        spawnerDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "SpawnerData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                SpawnerData spawnerData = new SpawnerData();
                spawnerData.setId(id);
                if (data.isList("MobData")) {
                    for (String mobData : data.getStringList("MobData")) {
                        spawnerData.addMobData(MobDataLoader.getMobData(mobData));
                    }
                } else spawnerData.addMobData(MobDataLoader.getMobData(data.getString("MobData")));
                if (data.isSet("MapData")) {
                    MapData mapData = MapDataLoader.getMapData(data.getString("MapData"));
                    spawnerData.setMapData(mapData);
                    mapData.addSpawner(spawnerData);
                }
                if (data.isSet("OffsetLevel")) {
                    double offsetLevel = data.getDouble("OffsetLevel");
                    spawnerData.setMinOffsetLevel(offsetLevel);
                    spawnerData.setMaxOffsetLevel(offsetLevel);
                }
                if (data.isSet("MinOffsetLevel")) spawnerData.setMinOffsetLevel(data.getDouble("MinOffsetLevel"));
                if (data.isSet("MaxOffsetLevel")) spawnerData.setMaxOffsetLevel(data.getDouble("MaxOffsetLevel"));
                spawnerData.setRadius(data.getDouble("Radius"));
                spawnerData.setRadiusY(data.getDouble("RadiusY"));
                //spawnerData.setMaxEnemy(data.getInt("MaxEnemy"));
                spawnerData.setMaxEnemy((int) (spawnerData.getRadius()/4+4));
                spawnerData.setPerSpawn(data.getInt("PerSpawn"));
                spawnerData.setLocation(new CustomLocation(SomCore.World, data.getDouble("Location.x"), data.getDouble("Location.y"), data.getDouble("Location.z")));
                spawnerDataList.put(id, spawnerData);
                list.add(spawnerData);

                int index = (int) ((spawnerData.getLocation().x()-250)/500);
                if (DungeonInstance.complete().contains(dungeon[index])) {
                    DungeonInstance.get(dungeon[index]).addSpawner(spawnerData);
                    spawnerData.setDungeonID(dungeon[index]);
                }
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        list.sort(Comparator.comparing(SpawnerData::getId));
        Log("§a[SpawnerDataLoader]§b" + spawnerDataList.size() + "個をロードしました");
    }
}
