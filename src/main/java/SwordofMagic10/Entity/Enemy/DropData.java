package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Item.SomItem;

public class DropData {
    private SomItem item;
    private int minAmount;
    private int maxAmount;
    private double percent;

    public SomItem getItem() {
        return item.clone();
    }

    public DropData setItem(SomItem item) {
        this.item = item;
        return this;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public DropData setMinAmount(int minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public DropData setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public double getPercent() {
        return percent;
    }

    public DropData setPercent(double percent) {
        this.percent = percent;
        return this;
    }
}
