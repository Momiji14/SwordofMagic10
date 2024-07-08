package SwordofMagic10.Entity;

import static org.bukkit.Material.SOUL_LANTERN;
import static org.bukkit.Material.WHITE_TERRACOTTA;

public enum EquipSlot {
    MainHand("メインハンド"),
    OffHand("オフハンド"),
    Helmet("頭"),
    Chest("胴"),
    Legs("脚"),
    Boots("足"),
    AlchemyStone("錬金石"),
    Amulet("アミュレット"),
    ;

    private final String display;

    EquipSlot(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
