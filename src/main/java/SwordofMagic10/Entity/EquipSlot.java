package SwordofMagic10.Entity;

public enum EquipSlot {
    MainHand("メインハンド"),
    OffHand("オフハンド"),
    Helmet("頭"),
    Chest("胴"),
    Legs("脚"),
    Boots("足"),
    Amulet("願封"),
    ;

    private final String display;

    EquipSlot(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
