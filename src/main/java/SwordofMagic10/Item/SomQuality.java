package SwordofMagic10.Item;

import SwordofMagic10.Player.Gathering.GatheringMenu;
import org.bukkit.Material;

import static SwordofMagic10.Component.Function.randomDouble;

public abstract class SomQuality extends SomItem implements Cloneable {

    private double quality = 0.5;
    private int level = 1;
    private int rawLevel = 1;
    private int exp = 0;

    public String getLevelDisplay() {
        return level + (rawLevel != level ? " §8(" + rawLevel + ")" : "");
    }

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
        if (this.level == 1) setRawLevel(level);
        this.level = level;
    }

    public int getRawLevel() {
        return rawLevel;
    }

    public void setRawLevel(int rawLevel) {
        this.rawLevel = rawLevel;
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
        } else if (getQuality() >= 0.01) {
            return Rank.Broken;
        } else {
            return Rank.Decay;
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
        Impossible("不可能", "§0§n", Material.STRUCTURE_BLOCK),
        Mythical("神話級", "§4", Material.END_CRYSTAL),
        Legendary("伝説的", "§d", Material.AMETHYST_CLUSTER),
        Master("傑作", "§b", Material.DIAMOND),
        Rare("高級", "§e", Material.GOLD_INGOT),
        Uncommon("非凡", "§9", Material.LAPIS_LAZULI),
        Common("平凡", "§f", Material.CALCITE),
        Degrade("劣化", "§7", Material.COBBLESTONE),
        Broken("失敗", "§8", Material.MUD),
        Decay("崩壊", "§c", Material.REDSTONE_BLOCK),
        ;

        private final String display;
        private final String color;
        private final Material icon;

        Rank(String display, String color, Material icon) {
            this.display = display;
            this.color = color;
            this.icon = icon;
        }

        public String getDisplay() {
            return display;
        }

        public String getColor() {
            return color;
        }

        public Material getIcon() {
            return icon;
        }

        public String getColorDisplay() {
            return color + display;
        }

        public Rank next() {
            if (this.ordinal()+1 < Rank.values().length) {
                return Rank.values()[this.ordinal()+1];
            } else return Rank.values()[0];
        }

        public Rank back() {
            if (this.ordinal()-1 >= 0) {
                return Rank.values()[this.ordinal()-1];
            } else return Rank.values()[Rank.values().length-1];
        }
    }
}
