package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.DataBase.MobDataLoader;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MobSpawn implements SomCommand, SomTabComplete {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 3) {
            MobData mobData = MobDataLoader.getMobData(args[0]);
            int level = Integer.parseInt(args[1]);
            DungeonDifficulty difficulty = DungeonDifficulty.valueOf(args[2]);
            EnemyData.spawn(mobData, level, difficulty, player.getLocation(), PlayerData.getPlayerList()).setGlobal(true);
        } else {
            playerData.sendMessage("§e/mobSpawn <mobData> <level> <difficulty>");
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
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            complete.addAll(MobDataLoader.getComplete());
        }
        return null;
    }
}

