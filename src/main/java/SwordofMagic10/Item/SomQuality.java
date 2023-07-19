package SwordofMagic10.Item;

import static SwordofMagic10.Component.Function.randomDouble;

public abstract class SomQuality extends SomItem implements Cloneable {

    private double quality = 0.5;
    private int level = 1;
    private int exp = 0;

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void randomQuality() {
        randomQuality(0, 1);
    }

    public void randomQuality(double min, double max) {
        setQuality(randomDouble(min, max));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    public String getQualityDisplay() {
        Rank rank = getQualityRank();
        return rank.getColor() + rank.getDisplay();
    }

    public Rank getQualityRank() {
        if (getQuality() >= 0.9999) {
            return Rank.Impossible;
        } else if (getQuality() >= 0.99) {
            return Rank.Mythical;
        } else if (getQuality() >= 0.95) {
            return Rank.Legendary;
        } else if (getQuality() >= 0.9) {
            return Rank.Master;
        } else if (getQuality() >= 0.75) {
            return Rank.Rare;
        } else if (getQuality() >= 0.5) {
            return Rank.Uncommon;
        } else if (getQuality() >= 0.35) {
            return Rank.Common;
        } else if (getQuality() >= 0.15) {
            return Rank.Degrade;
        } else {
            return Rank.Broken;
        }
    }

    @Override
    public SomQuality clone() {
        SomQuality clone = (SomQuality) super.clone();
        clone.quality = getQuality();
        clone.level = getLevel();
        return clone;
    }

    public enum Rank {
        Impossible("不可能", "§0§n"),
        Mythical("神話級", "§4"),
        Legendary("伝説的", "§d"),
        Master("傑作", "§b"),
        Rare("高級", "§e"),
        Uncommon("非凡", "§9"),
        Common("平凡", "§f"),
        Degrade("劣化", "§7"),
        Broken("失敗", "§8"),
        ;

        private final String display;
        private final String color;

        Rank(String display, String color) {
            this.display = display;
            this.color = color;
        }

        public String getDisplay() {
            return display;
        }

        public String getColor() {
            return color;
        }
        public String getColorDisplay() {
            return color + display;
        }
    }
}
