package SwordofMagic10.Dungeon.Instance;

import SwordofMagic10.Entity.Enemy.MobData;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class BattleArea {
    private Location enterLocation;
    private double radius;
    private final List<Table> table = new ArrayList<>();

    public Location getEnterLocation() {
        return enterLocation;
    }

    public void setEnterLocation(Location enterLocation) {
        this.enterLocation = enterLocation;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Table> getTable() {
        return table;
    }

    public void addTable(Table table) {
        this.table.add(table);
    }

    public record Table(MobData mobdata, int count, Location location, double radius) {}
}
