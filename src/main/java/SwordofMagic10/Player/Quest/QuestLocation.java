package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

public class QuestLocation extends QuestPhase implements Cloneable {
    private Location location;
    private double radius;

    public QuestLocation(QuestData questData) {
        super(questData);
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
    public SomJson toJson() {
        SomJson json = new SomJson();
        return json;
    }

    @Override
    public QuestPhase fromJson(SomJson json) {
        return clone();
    }

    @Override
    public QuestLocation clone() {
        QuestLocation clone = (QuestLocation) super.clone();
        return clone;
    }
}
