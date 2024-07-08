package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GetEffect implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        boolean buff = args.length < 3 || Boolean.parseBoolean(args[2]);
        SomEffect.Rank rank = args.length >= 4 ? SomEffect.Rank.valueOf(args[3]) : SomEffect.Rank.Normal;
        SomEffect somEffect = new SomEffect(args[0], args[0], buff, Double.parseDouble(args[1]));
        somEffect.setRank(rank);
        playerData.addEffect(somEffect, playerData);
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
        switch (args.length) {
            case 1 -> complete.add("ID");
            case 2 -> complete.add("Time");
            case 3 -> complete.add("true");
            case 4 -> {
                for (SomEffect.Rank rank : SomEffect.Rank.values()) {
                    complete.add(rank.toString());
                }
            }
        }
        return complete;
    }
}

