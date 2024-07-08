package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static SwordofMagic10.Command.Player.Trade.invComplete;
public class ViewItem implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int index = Integer.parseInt(args[0].split(":")[0]);
                if (playerData.getItemInventory().getInventory().size() > index) {
                    SomItemStack stack = playerData.getItemInventory().getInventory().get(index);
                    SomText text = SomText.create("[").add(stack.toSomText()).add("]");
                    SomCore.sendMessageComponent(player, text.toComponent());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerData.sendMessage("§e/viewItem <スロット番号>");
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

