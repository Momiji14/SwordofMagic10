package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static SwordofMagic10.Component.Function.scale;

public class ReqGatheringExp implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int level = Integer.parseInt(args[0]);
                playerData.sendMessage("§eLv" + level + "§7: §a" + scale(GatheringMenu.getReqExp(level)));
            } catch (Exception ignore) {}
        } else {
            for (int i = 1; i <= Classes.MaxLevel; i++) {
                playerData.sendMessage("§eLv" + i + "§7: §a" + scale(GatheringMenu.getReqExp(i)));
            }
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}
