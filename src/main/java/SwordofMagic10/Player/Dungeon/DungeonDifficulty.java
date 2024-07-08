package SwordofMagic10.Player.Dungeon;

import org.bukkit.Material;

public enum DungeonDifficulty {
    Easy(Material.LIME_WOOL, 1, 1),
    Normal(Material.GREEN_WOOL, 1.5, 20),
    Hard(Material.YELLOW_WOOL, 4, 35),
    Expert(Material.ORANGE_WOOL, 10, 50),
    Extreme(Material.RED_WOOL, 18.75, 65),
    Ultimate(Material.PINK_WOOL, 30, 75),
    NightMare(Material.MAGENTA_WOOL, 40, 85),
    Lunatic(Material.PURPLE_WOOL, 50, 95),
    ;

    private final Material icon;
    private final double multiply;
    private final int level;

    DungeonDifficulty(Material icon, double multiply, int level) {
        this.icon = icon;
        this.multiply = multiply;
        this.level = level;
    }

    public Material getIcon() {
        return icon;
    }

    public double getMultiply() {
        return multiply;
    }

    public int getLevel() {
        return level;
    }

    public int index() {
        return index(Expert);
    }

    public int index(DungeonDifficulty max) {
        return Math.min(ordinal(), max.ordinal());
    }

    public static DungeonDifficulty fromLevel(int level) {
        for (int i = 0; i < DungeonDifficulty.values().length-1; i++) {
            if (DungeonDifficulty.values()[i+1].getLevel() > level) {
                return DungeonDifficulty.values()[i];
            }
        }
        if (DungeonDifficulty.values()[DungeonDifficulty.values().length - 1].getLevel() <= level){
            return DungeonDifficulty.values()[DungeonDifficulty.values().length - 1];
        }else{
            return Easy;
        }
    }
}
