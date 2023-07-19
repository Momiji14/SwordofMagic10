package SwordofMagic10.Entity;

import SwordofMagic10.Player.Skill.SomSkill;

import java.util.HashMap;

public class SomEffect implements SomStatus, Cloneable {

    public enum List {
        Stun(new SomEffect("Stun", "スタン", false, 0).setStun(true).setSilence("All")),
        Silence(new SomEffect("Silence", "沈黙", false, 0).setSilence("All")),
        Freeze(new SomEffect("Freeze", "凍結", false, 0).setStun(true).setSilence("All")),
        Slow(new SomEffect("Slow", "スロー", false, 0).setStatusReturn(StatusType.Movement, -0.5)),
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
    private int time;
    private boolean isToggle = false;
    private int stack = 1;
    private boolean statusUpdate = false;
    private boolean invincible = false;
    private boolean stun = false;
    private String silence = null;
    private boolean extend = false;
    private final double[] doubleData = new double[4];
    private HashMap<StatusType, Double> status = new HashMap<>();

    public SomEffect(String id, String display, boolean isBuff, int time) {
        this.id = id;
        this.display = display;
        this.isBuff = isBuff;
        this.time = time;
    }
    public SomEffect(String id, String display, boolean isBuff, int time, int stack) {
        this.id = id;
        this.display = display;
        this.isBuff = isBuff;
        this.time = time;
        this.stack = stack;
    }

    public SomEffect(String id, String display, boolean isBuff, boolean toggle) {
        this.id = id;
        this.display = display;
        this.isBuff = isBuff;
        this.time = 1;
        this.isToggle = toggle;
    }

    public SomEffect(SomSkill skill, boolean isBuff) {
        this.id = skill.getId();
        this.display = skill.getDisplay();
        this.isBuff = isBuff;
        this.time = skill.getDuration();
        skill.getStatus().forEach(this::setStatus);
    }


    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public SomEntity getOwner() {
        return owner;
    }

    public void setOwner(SomEntity owner) {
        this.owner = owner;
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

    public int getTime() {
        return time;
    }

    public SomEffect setTime(int time) {
        this.time = time;
        return this;
    }

    public void addTime(int time) {
        this.time += time;
    }

    public boolean isToggle() {
        return isToggle;
    }

    public SomEffect setToggle(boolean toggle) {
        isToggle = toggle;
        return this;
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
        return statusUpdate;
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

    public SomEffect setStatusReturn(StatusType status, double value) {
        SomStatus.super.setStatus(status, value);
        return this;
    }

    @Override
    public SomEffect clone() {
        try {
            SomEffect clone = (SomEffect) super.clone();
            clone.time = time;
            clone.stack = stack;
            clone.status = new HashMap<>(status);
            clone.extend = extend;
            // TODO: このクローンが元の内部を変更できないようにミュータブルな状態をここにコピーします
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
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
}
