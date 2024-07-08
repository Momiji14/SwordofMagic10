package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static SwordofMagic10.Command.Player.Trade.invComplete;

public class Favorite implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int index = Integer.parseInt(args[0].split(":")[0]);
                if (playerData.getItemInventory().getInventory().size() > index) {
                    SomItemStack stack = playerData.getItemInventory().getInventory().get(index);
                    stack.getItem().setFavorite(!stack.getItem().isFavorite());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerData.sendMessage("§e/favorite <スロット番号>");
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        if (args.length == 1) return invComplete(playerData);
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return null;
    }
}

