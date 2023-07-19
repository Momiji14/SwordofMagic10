package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.Function;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParticleDensity implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 1) {
            try {
                int density = Function.MinMax(Integer.parseInt(args[0]), 0, 100);
                playerData.getSetting().setParticleDensity(density);
                return true;
            } catch (Exception ignore) {}
        }
        playerData.sendMessage("§e/particleDensity <0~100>");
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}

