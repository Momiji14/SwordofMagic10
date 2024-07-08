package SwordofMagic10.Item;

import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Skill.SkillParameterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomInt;

public class SomAmulet extends SomEquip implements SomStatus, Cloneable {
    private int BottleSlot;
    private double maxMultiply;
    private HashMap<StatusType, Double> status = new HashMap<>();
    private List<Bottle> bottles = new ArrayList<>();

    public int getBottleSlot() {
        return BottleSlot;
    }

    public void setBottleSlot(int bottleSlot) {
        BottleSlot = bottleSlot;
    }

    public double getMaxMultiply() {
        return maxMultiply;
    }

    public void setMaxMultiply(double maxMultiply) {
        this.maxMultiply = maxMultiply;
    }

    public List<Bottle> getBottles() {
        return bottles;
    }

    public Bottle getBottle(int index) {
        return bottles.get(index);
    }

    public void addBottle(Bottle bottle) {
        bottles.add(bottle);
    }

    public void removeBottle(int index) {
        bottles.remove(index);
    }

    public void randomStatus() {
        status.clear();
        if (maxMultiply > 0) {
            int max = randomInt(1, 5);
            for (int i = 0; i < max; i++) {
                StatusType statusType = StatusType.AmuletStatus()[randomInt(0, StatusType.AmuletStatus().length)];
                setStatus(statusType, randomDouble(0, getMaxMultiply()));
            }
        }
    }

    public double getAmuletStatus(StatusType statusType) {
        return getStatus(statusType) * getPlus() * getQuality();
    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    @Override
    public EquipSlot getEquipSlot() {
        return EquipSlot.Amulet;
    }

    @Override
    public EquipmentCategory getEquipmentCategory() {
        return EquipmentCategory.Amulet;
    }

    @Override
    public SomAmulet clone() {
        SomAmulet clone = (SomAmulet) super.clone();
        clone.status = new HashMap<>(status);
        clone.bottles = new ArrayList<>(bottles);
        return clone;
    }

    public static class Bottle extends SomItem implements SomStatus {
        private final String bottleCategory;

        private final HashMap<SkillParameterType, Double> parameter = new HashMap<>();
        private final HashMap<StatusType, Double> status = new HashMap<>();

        public Bottle(String bottleCategory) {
            this.bottleCategory = bottleCategory;
        }

        public void setParameter(SkillParameterType parameterType, double value) {
            parameter.put(parameterType, value);
        }

        public HashMap<SkillParameterType, Double> getParameter() {
            return parameter;
        }

        public double getParameter(SkillParameterType parameterType) {
            return parameter.getOrDefault(parameterType, 0.0);
        }

        public void setStatus(StatusType statusType, double value) {
            status.put(statusType, value);
        }

        @Override
        public HashMap<StatusType, Double> getStatus() {
            return status;
        }

        public double getStatus(StatusType statusType) {
            return status.getOrDefault(statusType, 0.0);
        }

        public String getBottleCategory() {
            return bottleCategory;
        }
    }
}
