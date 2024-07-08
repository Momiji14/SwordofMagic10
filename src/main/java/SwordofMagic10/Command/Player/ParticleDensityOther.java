package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.Function;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParticleDensityOther implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int density = Function.MinMax(Integer.parseInt(args[0]), 0, 100);
                playerData.getSetting().setParticleDensityOther(density);
                return true;
            } catch (Exception ignore) {}
        }
        playerData.sendMessage("§e/particleDensityOther <0~100>");
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}

