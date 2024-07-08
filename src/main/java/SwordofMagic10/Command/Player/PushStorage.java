package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PushStorage implements SomCommand, SomTabComplete {
    public static PlayerRank Rank = PlayerRank.Iron;
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (playerData.hasRank(Rank)) {
            if (args.length >= 1) {
                try {
                    if (ItemDataLoader.getComplete().contains(args[0])) {
                        playerData.getSetting().togglePushStorage(args[0]);
                    } else {
                        playerData.sendMessage("§a存在しない§eアイテム§aです", SomSound.Nope);
                    }
                    return true;
                } catch (Exception e) {
                    playerData.sendMessage("§e/pushStorage <ItemID>", SomSound.Nope);
                }
            } else {
                playerData.sendMessage("§e/pushStorage <ItemID>", SomSound.Nope);
            }
        } else playerData.sendMessageReqRank(Rank);
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
