package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomRune;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GetItem implements SomCommand, SomTabComplete {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            int amount = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
            SomItem item = ItemDataLoader.getItemData(args[0]);
            if (args.length >= 3) {
                item.setTier(Integer.parseInt(args[2]));
            }
            if (args.length >= 4) {
                int level = Integer.parseInt(args[3]);
                if (item instanceof SomEquipment equipment) {
                    equipment.setLevel(level);
                } else if (item instanceof SomRune rune) {
                    rune.setLevel(level);
                }
            }
            playerData.getItemInventory().add(item, amount);
        }
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
        return ItemDataLoader.getComplete();
    }
}

