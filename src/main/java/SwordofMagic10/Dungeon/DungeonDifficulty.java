package SwordofMagic10.Dungeon;

import org.bukkit.Material;

public enum DungeonDifficulty {
    Easy(Material.LIME_WOOL),
    Normal(Material.GREEN_WOOL),
    Hard(Material.YELLOW_WOOL),
    Expert(Material.ORANGE_WOOL),
    ;

    private final Material icon;

    DungeonDifficulty(Material icon) {
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }
}
