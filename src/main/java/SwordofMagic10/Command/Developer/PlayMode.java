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

public class PlayMode implements SomCommand, SomTabComplete {

    private final HashMap<Player, ItemStack[]> creativeInventory = new HashMap<>();

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (playerData.isPlayMode()) {
            playerData.saveSql();
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().clear();
            if (creativeInventory.containsKey(player)) {
                player.getInventory().setContents(creativeInventory.get(player));
            }
            playerData.getSetting().setPlayMode(false);
        } else {
            creativeInventory.put(player, player.getInventory().getContents());
            player.setGameMode(GameMode.ADVENTURE);
            playerData.loadSql();
            playerData.getSetting().setPlayMode(true);
        }
        playerData.sendMessage("PlayMode: " + playerData.isPlayMode(), SomSound.Tick);
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

