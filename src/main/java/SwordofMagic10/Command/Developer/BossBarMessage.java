package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BossBarMessage implements SomCommand {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            for (PlayerData playerData : PlayerData.getPlayerList()) {
                playerData.sendBossBarMessage(args[0], Integer.parseInt(args[1]), false);
            }
        } else {
            sender.sendMessage("§e/bossBarMessage <text> <time>");
        }
        return true;
    }

}

