package SwordofMagic10.Item;

import SwordofMagic10.Player.Gathering.GatheringMenu;

import java.util.HashMap;

import static SwordofMagic10.Player.Gathering.GatheringMenu.getReqExp;

public class SomWorker extends SomItem implements Cloneable {
    private HashMap<GatheringMenu.Type, Integer> level = new HashMap<>();
    private HashMap<GatheringMenu.Type, Integer> exp = new HashMap<>();
    private GatheringMenu.Type type = GatheringMenu.Type.Produce;

    public int getLevel(GatheringMenu.Type type) {
        if (!level.containsKey(type)) level.put(type, 1);
        return level.get(type);
    }

    public void setLevel(GatheringMenu.Type type, int level) {
        this.level.put(type, level);
    }

    public void addLevel(GatheringMenu.Type type, int level) {
        this.level.merge(type, level, Integer::sum);
    }

    public int getExp(GatheringMenu.Type type) {
        if (!exp.containsKey(type)) exp.put(type, 0);
        return exp.get(type);
    }

    public void setExp(GatheringMenu.Type type, int exp) {
        this.exp.put(type, exp);
    }

    public void addExp(GatheringMenu.Type type, int addExp) {
        int reqExp = getReqExp(getLevel(type));
        int addLevel = 0;
        int currentExp = getExp(type);
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            reqExp = getReqExp(getLevel(type) + addLevel);
        }
        if (addLevel > 0) {
            addLevel(type, addLevel);
        }
        setExp(type, currentExp);
    }

    public GatheringMenu.Type getType() {
        return type;
    }

    public void setType(GatheringMenu.Type type) {
        this.type = type;
    }

    @Override
    public SomWorker clone() {
        SomWorker clone = (SomWorker) super.clone();
        clone.level = new HashMap<>(level);
        clone.exp = new HashMap<>(exp);
        clone.type = getType();
        return clone;
    }
}
