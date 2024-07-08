package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Component.Updater;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static SwordofMagic10.SomCore.Log;

public class SomReload implements SomCommand {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        SomCore.SomReload = true;
        for (Player player : SomCore.getPlayers()) {
            player.sendTitle("§4§n§lシステムをリロードします", "", 10, 60, 0);
            player.closeInventory();
        }
        SomTask.syncDelay(() -> {
            if (SomCore.ID.equals("ServerDev")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "citizens save");
            for (PlayerData playerData : PlayerData.getPlayerList()) {
                playerData.saveSql();
            }
            long startTime = System.currentTimeMillis();
            //SomSQL.disconnect();
            Updater.UpdatePlugin();
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "plugman reload swordofmagic10");
            Log("§a[SomCore]§bSomReload - " + (System.currentTimeMillis() - startTime) + "ms");
        }, 60);
        return true;
    }
}

