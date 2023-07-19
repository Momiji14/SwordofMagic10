package SwordofMagic10.Item;

import SwordofMagic10.Entity.EquipSlot;

public enum EquipmentCategory {
    Sword("斬剣", EquipSlot.MainHand),
    Rod("魔杖", EquipSlot.MainHand),
    Gun("銃砲", EquipSlot.MainHand),
    Mace("聖槌", EquipSlot.MainHand),

    Helmet("兜鎧", EquipSlot.Helmet),
    Chest("胸鎧", EquipSlot.Chest),
    Legs("腰鎧", EquipSlot.Legs),
    Boots("靴鎧", EquipSlot.Boots),

    Amulet("願封", EquipSlot.Amulet),
    Trinket("攻飾", EquipSlot.OffHand),
    Shield("守盾", EquipSlot.OffHand),
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
