package SwordofMagic10.Item;

import SwordofMagic10.Entity.StatusType;

import java.util.HashMap;

public class SomCook extends SomItem {
    private HashMap<StatusType, Double> fixed = new HashMap<>();
    private HashMap<StatusType, Double> multiply = new HashMap<>();

    public void setFixed(StatusType statusType, double value) {
        fixed.put(statusType, value);
    }

    public double getFixed(StatusType statusType) {
        return fixed.getOrDefault(statusType, 0.0);
    }

    public HashMap<StatusType, Double> getFixed() {
        return fixed;
    }

    public void setMultiply(StatusType statusType, double value) {
        multiply.put(statusType, value);
    }

    public double getMultiply(StatusType statusType) {
        return multiply.getOrDefault(statusType, 0.0);
    }

    public HashMap<StatusType, Double> getMultiply() {
        return multiply;
    }
}
