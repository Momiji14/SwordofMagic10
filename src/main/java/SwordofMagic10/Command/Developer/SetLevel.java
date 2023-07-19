package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLevel implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int level = Integer.parseInt(args[0]);
                ClassType mainClass = playerData.getClasses().getMainClass();
                playerData.getClasses().setLevel(mainClass, level);
                playerData.sendMessage(mainClass.getColorDisplay() + " §eLv" + level);
                return true;
            } catch (Exception ignore) {}
        }
        playerData.sendMessage("§e/setLevel <level>");
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}

