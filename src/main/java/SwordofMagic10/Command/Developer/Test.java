package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.SomBlockParticle;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Test implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        SomBlockParticle particle = new SomBlockParticle(Material.REDSTONE_BLOCK);
        particle.line(playerData.getViewers(), playerData.getEyeLocation(), 25, 0.25);
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}

