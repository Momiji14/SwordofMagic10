package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class FlySpeed implements SomCommand, SomTabComplete {


    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        try {
            float speed = Float.parseFloat(args[0]);
            player.setFlySpeed(speed);
            playerData.sendMessage("FlySpeed: " + player.getFlySpeed(), SomSound.Tick);
        } catch (Exception ignore) {
            playerData.sendMessage("§e/flySpeed <speed>");
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return SomTabComplete.getPlayerList();
    }
}

