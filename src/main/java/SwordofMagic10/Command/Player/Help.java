package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        playerData.getHelpMenu().open();
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}
