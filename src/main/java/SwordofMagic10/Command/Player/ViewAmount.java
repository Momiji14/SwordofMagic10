package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ViewAmount implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            try {
                if (args[0].equalsIgnoreCase("delete")) {
                    playerData.getViewAmount().clear();
                    SomSound.Tick.play(playerData);
                } else if (ItemDataLoader.getComplete().contains(args[0])) {
                    if (playerData.getViewAmount().containsKey(args[0])) {
                        playerData.getViewAmount().remove(args[0]);
                        playerData.sendMessage("§e" + args[0] + "§aを§c削除§aしました", SomSound.Tick);
                    } else {
                        SomItem item = ItemDataLoader.getItemData(args[0]);
                        int tier = args.length >= 2 ? Integer.parseInt(args[1]) : item.getTier();
                        playerData.getViewAmount().put(item.getId(), tier);
                        playerData.sendMessage("§e" + args[0] + "§aを§b追加§aしました", SomSound.Tick);
                    }
                } else {
                    playerData.sendMessage("§a存在しない§eアイテム§aです", SomSound.Nope);
                }
                return true;
            } catch (Exception e) {
                playerData.sendMessage("§e/viewAmount <ItemID> [<Tier>]", SomSound.Nope);
            }
        } else {
            playerData.sendMessage("§e/viewAmount <ItemID> [<Tier>]", SomSound.Nope);
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        switch (args.length) {
            case 1 -> {
                return ItemDataLoader.getComplete();
            }
            case 2 -> {
                return Collections.singletonList("Tier");
            }
        }
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return null;
    }
}
