package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SomRestart implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        for (Player onlinePlayer : SomCore.getPlayers()) {
            onlinePlayer.kick(Component.text("§eサーバーを再起動します"));
        }
        Bukkit.setWhitelist(true);
        return true;
    }
}

