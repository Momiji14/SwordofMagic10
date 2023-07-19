package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Item.SomWorker;
import SwordofMagic10.Player.PlayerData;

import java.util.List;

import static SwordofMagic10.Component.Function.randomDouble;

public record GatheringTable(SomItem item, double percent) {
    public static void gathering(PlayerData playerData, GatheringMenu.Type type, List<GatheringTable> tableList, int addExp, SomTool tool) {
        int exp = 0;
        for (GatheringTable table : tableList) {
            int amount = give(table, tool.getPower() * (1 + tool.getMultiply(table.item().getId())));
            exp += addExp * amount;
            if (amount > 0) playerData.getItemInventory().add(table.item(), amount);
        }
        playerData.getGatheringMenu().addExp(type, exp);
    }

    public static void gathering(PlayerData playerData, GatheringMenu.Type type, List<GatheringTable> tableList, int addExp, SomWorker worker) {
        int exp = 0;
        for (GatheringTable table : tableList) {
            int amount = give(table, 1 + worker.getLevel(type)*0.05);
            exp += addExp * amount;
            if (amount > 0) playerData.getGatheringMenu().getGatheringItem().merge(table.item().getId(), amount, Integer::sum);
        }
        worker.addExp(type, exp);
    }

    private static int give(GatheringTable table, double power) {
        double percent = table.percent() * power;
        int amount = 0;
        while (percent >= 1) {
            percent -= 1;
            amount++;
        }
        if (randomDouble(0, 1) < percent) {
            amount++;
        }
        return amount;
    }
}
