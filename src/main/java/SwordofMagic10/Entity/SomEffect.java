package SwordofMagic10.Entity;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomPotion;
import SwordofMagic10.Player.Skill.SomSkill;

import java.util.HashMap;
import java.util.UUID;

public class SomEffect implements SomStatus.Basic, Cloneable {

    public enum List {
        Stun(new SomEffect("Stun", "スタン", false, 0).setStun(true).setSilence("All")),
        Silence(new SomEffect("Silence", "沈黙", false, 0).setSilence("All")),
        Freeze(new SomEffect("Freeze", "氷結", false, 0).setStun(true).setSilence("All")),
        Slow(new SomEffect("Slow", "スロー", false, 0).setMultiply(StatusType.Movement, -0.75)),
        Invincible(new SomEffect("Invincible", "無敵", true, 1).setInvincible(true)),
        ;

        private final SomEffect effect;

        List(SomEffect effect) {
            this.effect = effect;
        }

        public SomEffect getEffect() {
            return effect.clone();
        }
    }

    private final String id;
    private final String display;
    private SomEntity owner;
    private boolean isBuff;
    private Rank rank = Rank.Normal;
    private double time;
    private int stack = 1;
    private boolean statusUpdate = false;
    private boolean invincible = false;
    private boolean stun = false;
    private String silence = null;
    private boolean extend = false;
    private final double[] doubleData = new double[4];
    private HashMap<StatusType, Double> status = new HashMap<>();
    private HashMap<StatusType, Double> basicStatus = new HashMap<>();
    private final DeleteFlag deleteFlag = new DeleteFlag();
    private final UUID uuid = UUID.randomUUID();
    private SomPotion formItem = null;

    public SomEffect(String id, String display, boolean isBuff, double time) {
        this.id = id;
        this.display = display;
        this.isBuff = isBuff;
        this.time = time;
    }
    public SomEffect(String id, String display, boolean isBuff, double time, int stack) {
        this.id = id;
        this.display = display;
        this.isBuff = isBuff;
        this.time = time;
        this.stack = stack;
    }

    public SomEffect(SomSkill skill, boolean isBuff) {
        this.id = skill.getId();
        this.display = skill.getDisplay();
        this.isBuff = isBuff;
        this.time = skill.getDuration();
        owner = skill.getPlayerData();
        skill.getStatus().forEach(((statusType, value) -> {
            switch (statusType) {
                case DamageResist, CastTime, CoolTime, RigidTime -> setMultiply(statusType, 1-value);
                default -> setMultiply(statusType, value);
            }
        }));
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isDelete() {
        return deleteFlag.isDelete();
    }

    public void delete() {
        deleteFlag.delete();
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public String getColorDisplay() {
        return (isBuff() ? "§e" : "§c") + getDisplay();
    }

    public SomEntity getOwner() {
        return owner;
    }

    public SomEffect setOwner(SomEntity owner) {
        this.owner = owner;
        return this;
    }

    public boolean isBuff() {
        return isBuff;
    }

    public void setBuff(boolean buff) {
        isBuff = buff;
    }

    public Rank getRank() {
        return rank;
    }

    public SomEffect setRank(Rank rank) {
        this.rank = rank;
        return this;
    }

    public double getTime() {
        return time;
    }

    public SomEffect setTime(double time) {
        this.time = time;
        return this;
    }

    public void addTime(int time) {
        this.time += time;
    }

    public int getStack() {
        return stack;
    }

    public SomEffect setStack(int stack) {
        this.stack = stack;
        return this;
    }
    public SomEffect addStack(int stack) {
        this.stack += stack;
        return this;
    }

    public boolean isStatusUpdate() {
        return getFixed().size() > 0 || getMultiply().size() > 0 || statusUpdate;
    }

    public SomEffect setStatusUpdate(boolean statusUpdate) {
        this.statusUpdate = statusUpdate;
        return this;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public SomEffect setInvincible(boolean invincible) {
        this.invincible = invincible;
        return this;
    }

    public boolean isStun() {
        return stun;
    }

    public SomEffect setStun(boolean stun) {
        this.stun = stun;
        if (stun) setStatus(StatusType.Movement, -1.0);
        return this;
    }

    public String isSilence() {
        return silence;
    }

    public SomEffect setSilence(String silence) {
        this.silence = silence;
        return this;
    }

    public boolean isExtend() {
        return extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }

    public boolean hasFromItem() {
        return formItem != null;
    }

    public SomPotion getFormItem() {
        return formItem;
    }

    public void setFormItem(SomPotion formItem) {
        this.formItem = formItem;
    }

    public SomEffect setDoubleData(int i, double value) {
        doubleData[i] = value;
        return this;
    }

    public double[] getDoubleData() {
        return doubleData;
    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    @Override
    public HashMap<StatusType, Double> getBasicStatus() {
        return basicStatus;
    }

    public HashMap<StatusType, Double> getMultiply() {
        return status;
    }

    public HashMap<StatusType, Double> getFixed() {
        return basicStatus;
    }

    public SomEffect setMultiply(StatusType status, double value) {
        SomStatus.Basic.super.setStatus(status, value);
        return this;
    }

    public SomEffect setFixed(StatusType status, double value) {
        SomStatus.Basic.super.setBasicStatus(status, value);
        return this;
    }

    @Override
    public SomEffect clone() {
        try {
            SomEffect clone = (SomEffect) super.clone();
            clone.time = time;
            clone.stack = stack;
            clone.status = new HashMap<>(status);
            clone.basicStatus = new HashMap<>(basicStatus);
            clone.extend = extend;
            // TODO: このクローンが元の内部を変更できないようにミュータブルな状態をここにコピーします
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class Toggle extends SomEffect implements Cloneable {

        public Toggle(String id, String display, boolean isBuff) {
            super(id, display, isBuff, -1);
        }

        public Toggle(String id, String display, boolean isBuff, int stack) {
            super(id, display, isBuff, -1, stack);
        }

        public Toggle(SomSkill skill, boolean isBuff) {
            super(skill, isBuff);
            setTime(-1);
        }

        @Override
        public Toggle clone() {
            return (Toggle) super.clone();
        }
    }

    public static class Area extends SomEffect implements Cloneable {

        private CustomLocation location;
        private double radius;

        public Area(String id, String display, boolean isBuff) {
            super(id, display, isBuff, -1);
        }

        public Area(String id, String display, boolean isBuff, int stack) {
            super(id, display, isBuff, -1, stack);
        }

        public Area(SomSkill skill, boolean isBuff) {
            super(skill, isBuff);
            setTime(-1);
        }

        public CustomLocation getLocation() {
            return location;
        }

        public void setLocation(CustomLocation location) {
            this.location = location;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        public Area clone() {
            return (Area) super.clone();
        }
    }

    public enum Rank {
        Normal("一般"),
        High("高級"),
        Impossible("不可"),
        ;

        private final String display;

        Rank(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    public static class DeleteFlag {
        private boolean delete = false;

        public void delete() {
            this.delete = true;
        }

        public boolean isDelete() {
            return delete;
        }
    }
}
