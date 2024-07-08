package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Item.SomWorker;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.scale;
import static SwordofMagic10.Player.Gathering.GatheringMenu.GatheringTime;

public record GatheringTable(SomItem item, double percent) {
    public static final double AdjustPower = 0.2;
    public static void gathering(PlayerData playerData, GatheringMenu.Type type, List<GatheringTable> tableList, SomTool tool) {
        double multiply = 1;
        switch (type) {
            case Mining -> multiply = 7;
            case Lumber -> multiply = 5;
            case Collect -> multiply = 3;
            case Fishing -> multiply = 20 + (Math.max(0, tool.getPlus()-10) * 0.5);
            case Hunting -> multiply = 8;
        }
        multiply *= 1 + playerData.getGatheringMenu().overBonus(type);
        int level = playerData.getGatheringMenu().getLevel(type);
        for (GatheringTable table : tableList) {
            double power = (1 + (tool.getPower() * 0.1) + (level * 0.01)) * tool.getMultiply(table.item().getId()) * multiply * AdjustPower;
            int amount = give(table, power);
            if (amount > 0) {
                playerData.getItemInventory().add(table.item(), amount, table.percent()*power);
            }
        }
        playerData.getGatheringMenu().addExp(type, multiply);
        tool.addExp((int) ((1+tool.getPower()*0.5)*multiply));
    }

    public static void gathering(PlayerData playerData, GatheringMenu.Type type, List<GatheringTable> tableList, SomWorker worker) {
        int level = worker.getLevel(type);
        for (GatheringTable table : tableList) {
            int amount = give(table, worker.getPower(type));
            if (amount > 0) playerData.getGatheringMenu().addGatheringItem(table.item(), amount);
        }
        worker.addExp(type, GatheringMenu.getExp(level) * GatheringTime * 0.1);
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
