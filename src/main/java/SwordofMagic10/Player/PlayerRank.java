package SwordofMagic10.Player;

public enum PlayerRank {

    Normal("N", "§7", 6, 100, 300, 9, 0.05),
    Iron("I", "§f", 8, 150, 400, 12, 0.04), //1500
    Gold("G", "§e", 12, 200, 500, 15, 0.035), //3000
    Diamond("D", "§b", 16, 250, 600, 18, 0.03), //5000
    Emerald("V", "§a", 24, 300, 700, 21, 0.025), //7000
    Premium("P", "§d", 72, 400, 1000, 27, 0.02), //10000
    ;

    private final String prefix;
    private final String color;
    private final int workerLimit;
    private final int inventorySlot;
    private final int storageSlot;
    private final int questSize;
    private final double commission;

    PlayerRank(String prefix, String color, int workerLimit, int inventorySlot, int storageSlot, int questSize, double commission) {
        this.prefix = prefix;
        this.color = color;
        this.workerLimit = workerLimit;
        this.inventorySlot = inventorySlot;
        this.storageSlot = storageSlot;
        this.questSize = questSize;
        this.commission = commission;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getColor() {
        return color;
    }

    public String getColorPrefix() {
        return color + prefix;
    }

    public String getDisplay() {
        return color + this;
    }

    public int getWorkerLimit() {
        return workerLimit;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public int getStorageSlot() {
        return storageSlot;
    }

    public int getQuestSize() {
        return questSize;
    }

    public double getCommission() {
        return commission;
    }
}
