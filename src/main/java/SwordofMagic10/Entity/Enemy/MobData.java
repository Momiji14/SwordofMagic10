package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.HowToGet;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobData implements SomStatus, HowToGet {
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

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return baseStatus;
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

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    public void updateStatus() {
        baseStatus.putIfAbsent(StatusType.MaxHealth, 100.0);
        baseStatus.putIfAbsent(StatusType.MaxMana, 100.0);
        baseStatus.putIfAbsent(StatusType.HealthRegen, 1.0);
        baseStatus.putIfAbsent(StatusType.ManaRegen, 1.0);
        baseStatus.putIfAbsent(StatusType.ATK, 10.0);
        baseStatus.putIfAbsent(StatusType.MAT, 10.0);
        baseStatus.putIfAbsent(StatusType.DEF, 10.0);
        baseStatus.putIfAbsent(StatusType.MDF, 10.0);
        baseStatus.putIfAbsent(StatusType.SPT, 10.0);
        baseStatus.putIfAbsent(StatusType.Critical, 5.0);
        baseStatus.putIfAbsent(StatusType.CriticalDamage, 5.0);
        baseStatus.putIfAbsent(StatusType.CriticalResist, 5.0);
        baseStatus.putIfAbsent(StatusType.DamageMultiply, 1.0);
        baseStatus.putIfAbsent(StatusType.DamageResist, 1.0);
        switch (getTier()) {
            case Normal -> baseStatus.putIfAbsent(StatusType.Movement, 200.0);
            case MiddleBoss -> baseStatus.putIfAbsent(StatusType.Movement, 250.0);
            case Boss -> baseStatus.putIfAbsent(StatusType.Movement, 300.0);
            case WorldRaidBoss, LegendRaidBoss -> baseStatus.putIfAbsent(StatusType.Movement, 350.0);
        }
    }

    public enum Tier {
        WorldRaidBoss(100),
        LegendRaidBoss(100),
        Boss(10),
        MiddleBoss(5),
        Normal(1),
        ;

        private final double multiply;

        Tier(double multiply) {
            this.multiply = multiply;
        }

        public double getMultiply() {
            return multiply;
        }
    }
}
