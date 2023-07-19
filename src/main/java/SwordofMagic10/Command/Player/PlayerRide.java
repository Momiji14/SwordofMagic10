package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerRide implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        Entity entity = player.getTargetEntity(5);
        if (entity instanceof Player target && target.isOnline()) {
            PlayerData targetData = PlayerData.get(target);
            if (targetData.getSetting().isRideAble()) {
                target.addPassenger(player);
            } else {
                playerData.sendMessage(targetData.getDisplayName() + "§aが§eライド§aを§c無効§aにしています", SomSound.Nope);
            }
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}
