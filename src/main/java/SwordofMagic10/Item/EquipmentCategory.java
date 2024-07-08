package SwordofMagic10.Item;

import SwordofMagic10.Entity.EquipSlot;

import static org.bukkit.Material.SOUL_LANTERN;
import static org.bukkit.Material.WHITE_TERRACOTTA;

public enum EquipmentCategory {
    Sword("斬剣", EquipSlot.MainHand),
    Rod("魔杖", EquipSlot.MainHand),
    Gun("銃砲", EquipSlot.MainHand),
    Mace("聖槌", EquipSlot.MainHand),

    Helmet("兜鎧", EquipSlot.Helmet),
    Chest("胸鎧", EquipSlot.Chest),
    Legs("腰鎧", EquipSlot.Legs),
    Boots("靴鎧", EquipSlot.Boots),

    Trinket("攻飾", EquipSlot.OffHand),
    Shield("守盾", EquipSlot.OffHand),

    AlchemyStone("錬金石", EquipSlot.AlchemyStone),
    Amulet("アミュレット", EquipSlot.Amulet),
    None("未分類", null)
    ;

    private final String display;
    private final EquipSlot equipSlot;

    EquipmentCategory(String display, EquipSlot equipSlot) {
        this.display = display;
        this.equipSlot = equipSlot;
    }

    public String getDisplay() {
        return display;
    }

    public EquipSlot getEquipSlot() {
        return equipSlot;
    }
}
