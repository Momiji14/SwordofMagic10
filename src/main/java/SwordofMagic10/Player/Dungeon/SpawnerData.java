package SwordofMagic10.Player.Dungeon;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Map.MapData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static SwordofMagic10.Component.Function.randomInt;

public class SpawnerData {
    private String id;
    private final List<MobData> mobData = new ArrayList<>();
    private double minOffsetLevel = 0;
    private double maxOffsetLevel = 0;
    private String dungeonID;
    private MapData mapData;
    private double radius;
    private double radiusY;
    private int maxEnemy;
    private int perSpawn;
    private CustomLocation location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRadius() {
        return radius;
    }

    public List<MobData> getMobData() {
        return mobData;
    }

    public void addMobData(MobData mobData) {
        this.mobData.add(mobData);
    }

    public MobData randomMobData() {
        return mobData.get(randomInt(0, mobData.size()));
    }

    public double getMinOffsetLevel() {
        return minOffsetLevel;
    }

    public void setMinOffsetLevel(double minOffsetLevel) {
        this.minOffsetLevel = minOffsetLevel;
    }

    public double getMaxOffsetLevel() {
        return maxOffsetLevel;
    }

    public void setMaxOffsetLevel(double maxOffsetLevel) {
        this.maxOffsetLevel = maxOffsetLevel;
    }

    public String getDungeonID() {
        return dungeonID;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(double radiusY) {
        this.radiusY = radiusY;
    }

    public int getMaxEnemy() {
        return maxEnemy;
    }

    public void setMaxEnemy(int maxEnemy) {
        this.maxEnemy = maxEnemy;
    }

    public int getPerSpawn() {
        return perSpawn;
    }

    public void setPerSpawn(int perSpawn) {
        this.perSpawn = perSpawn;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }
}
