package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.Gathering.*;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GatheringDrop implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            List<String> message = new ArrayList<>();
            List<GatheringTable> tables = new ArrayList<>();
            switch (GatheringMenu.Type.valueOf(args[0])) {
                case Mining -> Mining.Table.values().forEach(tables::addAll);
                case Lumber -> Lumber.Table.values().forEach(tables::addAll);
                case Collect -> Collect.Table.values().forEach(tables::addAll);
                case Fishing -> tables.addAll(Fishing.Table);
                case Hunting -> Hunting.Table.values().forEach(tables::addAll);
            }
            for (GatheringTable table : tables) {
                String text = "§7・§r" + table.item().getColorDisplay() + " §a" + table.percent()*100 + "%";
                if (!message.contains(text)) message.add(text);
            }
            playerData.sendMessage(message, SomSound.Tick);
        } else playerData.sendMessage("§e/gatheringDrop <type>");
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                complete.add(type.toString());
            }
        }
        return complete;
    }
}
