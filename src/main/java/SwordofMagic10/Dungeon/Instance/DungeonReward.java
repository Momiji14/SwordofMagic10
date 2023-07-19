package SwordofMagic10.Dungeon.Instance;

import SwordofMagic10.Component.SomText;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;

import java.util.HashMap;

import static SwordofMagic10.Component.Function.randomDouble;

public class DungeonReward {
    private int exp = 0;
    private final HashMap<Integer, Double> upgradeStone = new HashMap<>();
    private final HashMap<Integer, Double> tierStone = new HashMap<>();
    private final HashMap<Integer, Double> qualityStone = new HashMap<>();

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public HashMap<Integer, Double> getUpgradeStone() {
        return upgradeStone;
    }

    public double getUpgradeStone(int tier) {
        return upgradeStone.getOrDefault(tier, 0.0);
    }

    public void setUpgradeStone(int tier, double percent) {
        upgradeStone.put(tier, percent);
    }

    public HashMap<Integer, Double> getTierStone() {
        return tierStone;
    }

    public double getTierStone(int tier) {
        return tierStone.getOrDefault(tier, 0.0);
    }

    public void setTierStone(int tier, double percent) {
        tierStone.put(tier, percent);
    }

    public HashMap<Integer, Double> getQualityStone() {
        return qualityStone;
    }

    public double getQualityStone(double tier) {
        return qualityStone.getOrDefault(tier, 0.0);
    }

    public void setQualityStone(int tier, double percent) {
        qualityStone.put(tier, percent);
    }

    public void give(PlayerData playerData) {
        upgradeStone.forEach((tier, percent) -> {
            SomItem item = ItemDataLoader.getItemData("精錬石");
            item.setTier(tier);
            int amount = 0;
            while (percent >= 1) {
                amount++;
                percent--;
            }
            if (randomDouble(0, 1) < percent) {
                amount++;
            }
            if (amount > 0) {
                playerData.getItemInventory().add(item, amount);
                playerData.sendSomText(SomText.create("§b[+]§r").addText(item.toSomText(amount)));
            }
        });
        tierStone.forEach((tier, percent) -> {
            SomItem item = ItemDataLoader.getItemData("昇級石");
            item.setTier(tier);
            int amount = 0;
            while (percent >= 1) {
                amount++;
                percent--;
            }
            if (randomDouble(0, 1) < percent) {
                amount++;
            }
            if (amount > 0) {
                playerData.getItemInventory().add(item, amount);
                playerData.sendSomText(SomText.create("§b[+]§r").addText(item.toSomText(amount)));
            }
        });
        qualityStone.forEach((tier, percent) -> {
            SomItem item = ItemDataLoader.getItemData("品質変更石");
            item.setTier(tier);
            int amount = 0;
            while (percent >= 1) {
                amount++;
                percent--;
            }
            if (randomDouble(0, 1) < percent) {
                amount++;
            }
            if (amount > 0) {
                playerData.getItemInventory().add(item, amount);
                playerData.sendSomText(SomText.create("§b[+]§r").addText(item.toSomText(amount)));
            }
        });
        playerData.getClasses().addExp(getExp());
    }

}
