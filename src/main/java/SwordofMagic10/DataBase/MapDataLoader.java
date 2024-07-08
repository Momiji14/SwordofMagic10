package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.ShopData;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.createLocation;
import static SwordofMagic10.SomCore.Log;

public class MapDataLoader {
    private static final HashMap<String, MapData> mapDataList = new HashMap<>();
    public static MapData getMapData(String id) {
        if (!mapDataList.containsKey(id)) Log("§c存在しないMapDataが参照されました -> " + id, true);
        return mapDataList.get(id);
    }
    public static Collection<MapData> getMapDataList() {
        return mapDataList.values();
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (MapData mapData : getMapDataList()) {
            complete.add(mapData.getId());
        }
        return complete;
    }
    public static void load() {
        mapDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "MapData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                MapData mapData = new MapData();
                mapData.setId(id);
                mapData.setPrefix(data.getString("Prefix"));
                mapData.setSuffix(data.getString("Suffix"));
                mapData.setIcon(Material.valueOf(data.getString("Icon")));
                if (data.isSet("Slot")) mapData.setSlot(data.getInt("Slot"));
                if (data.isSet("StartLocation.x")) mapData.setStartLocation(createLocation(data.getDouble("StartLocation.x"),data.getDouble("StartLocation.y"), data.getDouble("StartLocation.z"), (float) data.getDouble("StartLocation.yaw"), (float) data.getDouble("StartLocation.pitch")));
                if (data.isSet("GoalLocation.x")) mapData.setGoalLocation(createLocation(data.getDouble("GoalLocation.x"),data.getDouble("GoalLocation.y"), data.getDouble("GoalLocation.z"), (float) data.getDouble("GoalLocation.yaw"), (float) data.getDouble("GoalLocation.pitch")));
                if (data.isSet("GateLocation.x")) mapData.setGateLocation(createLocation(data.getDouble("GateLocation.x"),data.getDouble("GateLocation.y"), data.getDouble("GateLocation.z"), (float) data.getDouble("GateLocation.yaw"), (float) data.getDouble("GateLocation.pitch")));
                if (data.isSet("Difficulty")) mapData.setDifficulty(DungeonDifficulty.valueOf(data.getString("Difficulty")));
                mapData.setMinLevel(data.getInt("MinLevel"));
                mapData.setMaxLevel(data.getInt("MaxLevel"));
                mapDataList.put(id, mapData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "MapData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                MapData mapData = mapDataList.get(id);
                if (data.isSet("NextMap")) mapData.setNextMap(mapDataList.get(data.getString("NextMap")));
                if (data.isSet("PrevMap")) mapData.setPrevMap(mapDataList.get(data.getString("PrevMap")));
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }
        Log("§a[MapDataLoader]§b" + mapDataList.size() + "個をロードしました");
    }

    public static void start() {
        for (MapData mapData : mapDataList.values()) {
            mapData.start();
        }
    }
}
