package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class QuestLocation extends QuestPhase implements Cloneable {
    private Location location;
    private double radius;

    public QuestLocation(QuestData questData) {
        super(questData);
    }

    @Override
    public List<String> sidebarLine(PlayerData playerData) {
        List<String> list = new ArrayList<>();
        list.add("§7・§e" + getLocationDisplay() + "§aへ向かう");
        return list;
    }

    public String getLocationDisplay() {
        return "x" + location.getBlockX() + ",y" + location.getBlockY() + ",z" + location.getBlockZ();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        return playerData.getLocation().distance(getLocation()) < getRadius();
    }

    @Override
    public QuestLocation clone() {
        QuestLocation clone = (QuestLocation) super.clone();
        return clone;
    }
}
