package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ViewParticle implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 2) {
            try {
                SomParticle particle = new SomParticle(Particle.END_ROD, playerData);
                switch (args[0]) {
                    case "Circle" -> {
                        particle.circle(playerData.getViewers(), playerData.getLocation(), Double.parseDouble(args[1]));
                    }
                    case "FanShaped" -> {
                        particle.fanShaped(playerData.getViewers(), playerData.getLocation(), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                    }
                    case "Rectangle" -> {
                        particle.rectangle(playerData.getViewers(), playerData.getLocation(), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                    }
                }
            } catch (Exception e) {
                playerData.sendMessage("引数が適切ではありません", SomSound.Nope);
            }
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
        if (args.length == 1) return List.of(new String[]{"Circle", "FanShaped", "Rectangle"});
        List<String> list = new ArrayList<>();
        return list;
    }
}

