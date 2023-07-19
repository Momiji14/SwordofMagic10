package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.HowToGet;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobData implements HowToGet {
    private String Id;
    private String display;
    private EntityType entityType;
    private Disguise disguise;
    private int size = 0;
    private double collisionSize = 0;
    private double collisionSizeY = 0;
    private double movement = 1;
    private int exp = 10;
    private Tier tier = Tier.Normal;
    private String customClass;
    private final HashMap<StatusType, Double> baseStatus = new HashMap<>();
    private final List<DropData> dropDataList = new ArrayList<>();

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Disguise getDisguise() {
        return disguise;
    }

    public void setDisguise(Disguise disguise) {
        this.disguise = disguise;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getCollisionSize() {
        return collisionSize;
    }

    public void setCollisionSize(double collisionSize) {
        this.collisionSize = collisionSize;
    }

    public double getCollisionSizeY() {
        return collisionSizeY;
    }

    public void setCollisionSizeY(double collisionSizeY) {
        this.collisionSizeY = collisionSizeY;
    }

    public double getMovement() {
        return movement;
    }

    public void setMovement(double movement) {
        this.movement = movement;
    }

    public int getExp() {
        return exp;
    }

    public double getStatus(StatusType type) {
        if (type == null) return 0;
        return baseStatus.getOrDefault(type, 0d);
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }
    public String getCustomClass() {
        return customClass;
    }

    public void setCustomClass(String customClass) {
        this.customClass = customClass;
    }

    public void setStatus(StatusType type, double value) {
        baseStatus.put(type, value);
    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    public enum Tier {
        RaidBoss,
        Boss,
        MiddleBoss,
        Normal,
    }
}
