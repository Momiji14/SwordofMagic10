package SwordofMagic10.Player.Dungeon.Instance;

import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;

import java.util.HashMap;

import static SwordofMagic10.Component.Config.AdjustExp;
import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.scale;

public class DungeonReward {
    private double exp = 0;
    private int mel = 0;
    private final HashMap<Integer, Double> upgradeStone = new HashMap<>();
    private final HashMap<Integer, Double> tierStone = new HashMap<>();
    private final HashMap<Integer, Double> qualityStone = new HashMap<>();

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public int getMel() {
        return mel;
    }

    public void setMel(int mel) {
        this.mel = mel;
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

    public void give(PlayerData playerData, int dungeonLevel) {
        upgradeStone.forEach((tier, percent) -> {
            double rawPercent = percent;
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
                playerData.getItemInventory().add(item, amount, rawPercent);
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
                playerData.getItemInventory().add(item, amount, percent);
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
                playerData.getItemInventory().add(item, amount, percent);
            }
        });

        playerData.getClasses().addExp(getExp() * AdjustExp(playerData, dungeonLevel));
        playerData.addMel(getMel());
        playerData.sendMessage("§eメル報酬 +" + getMel());
    }

}
