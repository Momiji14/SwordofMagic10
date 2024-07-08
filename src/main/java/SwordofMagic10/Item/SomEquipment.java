package SwordofMagic10.Item;

import SwordofMagic10.Component.Function;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SomEquipment extends SomEquip implements SomStatus, Cloneable {
    public static final int MaxRuneSlot = 4;
    private final static int[] ReqExp = new int[Classes.MaxLevel];

    static {
        ReqExp[0] = 30;
        for (int i = 1; i < ReqExp.length; i++) {
            ReqExp[i] = (int) Math.ceil(ReqExp[i-1] * 1.1) + 400;
        }
    }

    public static int getReqExp(int level) {
        return ReqExp[Function.MinMax(level, 1, Classes.MaxLevel)-1];
    }

    private HashMap<StatusType, Double> status = new HashMap<>();
    private EquipmentCategory equipmentCategory;
    private List<SomRune> rune = new ArrayList<>();
    private int runeSlot = 0;
    private String series;

    public SomEquipment() {
        setItemCategory(ItemCategory.Equipment);
    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    public double getStatusLevelSync(StatusType statusType, int levelSync, int tierSync) {
        double value = getStatus(statusType) * (1 + (Math.min(levelSync, getLevel())-1) * 0.08) * (0.75 + getQuality()*0.5) * Math.pow(1.35, Math.min(tierSync, getTier())-1) * (1 + getPlus()*0.1);
        for (SomRune rune : getRune()) {
            value += rune.getStatusLevelSync(statusType, levelSync, tierSync);
        }
        return value;
    }

    public HashMap<StatusType, Double> getStatusLevelSync(int levelSync, int tierSync) {
        HashMap<StatusType, Double> status = new HashMap<>();
        for (StatusType statusType : StatusType.BaseStatus()) {
            status.put(statusType, getStatusLevelSync(statusType, levelSync, tierSync));
        }
        return status;
    }

    public EquipmentCategory getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public int getRuneSlot() {
        return Math.min(runeSlot + getTier(), 4);
    }

    public void setRuneSlot(int runeSlot) {
        this.runeSlot = runeSlot;
    }

    public List<SomRune> getRune() {
        return rune;
    }

    public SomRune getRune(int index) {
        if (index >= 0 && rune.size() > index) {
            return rune.get(index);
        } else return null;
    }

    public void removeRune(int index) {
        rune.remove(index);
    }

    public void addRune(SomRune rune) {
        this.rune.add(rune);
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public boolean hasSeries() {
        return series != null;
    }

    @Override
    public SomEquipment clone() {
        SomEquipment clone = (SomEquipment) super.clone();
        clone.status = new HashMap<>(status);
        clone.rune = new ArrayList<>(rune);
        clone.runeSlot = runeSlot;
        clone.setLevel(getLevel());
        clone.setExp(getExp());
        clone.setQuality(getQuality());
        return clone;
    }

    @Override
    public EquipSlot getEquipSlot() {
        return equipmentCategory.getEquipSlot();
    }
}
