package SwordofMagic10.Item;

import SwordofMagic10.Component.Function;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;

import java.util.HashMap;

public class SomAlchemyStone extends SomEquip implements SomStatus, Cloneable {

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
    private final HashMap<String, Integer> expMap = new HashMap<>();
    private double durable = 100.0;

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    public double getStatusLevelSync(StatusType statusType, int levelSync, int tierSync) {
        return getStatus(statusType) * (1 + (Math.min(levelSync, getLevel())-1) * 0.08) * (0.75 + getQuality()*0.5) * (1+(Math.pow(1.35, Math.min(tierSync, getTier())-1)-1)) * (1 + getPlus()*0.1);
    }

    public HashMap<StatusType, Double> getStatusLevelSync(int levelSync, int tierSync) {
        HashMap<StatusType, Double> status = new HashMap<>();
        for (StatusType statusType : StatusType.BaseStatus()) {
            status.put(statusType, getStatusLevelSync(statusType, levelSync, tierSync));
        }
        return status;
    }

    public int getExpMap(String id) {
        return expMap.getOrDefault(id, 0);
    }

    public HashMap<String, Integer> getExpMap() {
        return expMap;
    }

    public void setExpMap(String id, int exp) {
        expMap.put(id, exp);
    }

    public double getDurable() {
        return durable;
    }

    public void setDurable(double durable) {
        this.durable = durable;
    }

    @Override
    public SomAlchemyStone clone() {
        SomAlchemyStone clone = (SomAlchemyStone) super.clone();
        clone.status = new HashMap<>(status);
        clone.setLevel(getLevel());
        clone.setExp(getExp());
        clone.setQuality(getQuality());
        clone.setDurable(getDurable());
        return clone;
    }

    @Override
    public EquipSlot getEquipSlot() {
        return EquipSlot.AlchemyStone;
    }

    @Override
    public EquipmentCategory getEquipmentCategory() {
        return EquipmentCategory.AlchemyStone;
    }
}
