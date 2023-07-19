package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.*;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DisplayParticle implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 2) {
            SomDisplayParticle particle;
            switch (args[0]) {
                case "Item" -> particle = new SomItemParticle(new ItemStack(Material.valueOf(args[1])));
                case "Block" -> particle = new SomBlockParticle(Material.valueOf(args[1]));
                case "Text" -> particle = new SomTextParticle(args[1]);
                default -> {
                    return false;
                }
            }
            if (args.length >= 3) particle.setBillboard(Display.Billboard.valueOf(args[2]));
            if (args.length >= 4) particle.setTime(Integer.parseInt(args[3]));
            if (args.length >= 5) {
                String[] split = args[4].split(",");
                particle.setScale(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
            }
            if (args.length >= 6) {
                if (args[5].contains("Look")) {
                    Location loc = player.getEyeLocation();
                    particle.setLeftRotation(loc.getYaw(), loc.getPitch(), 0);
                } else{
                    String[] split = args[5].split(",");
                    particle.setLeftRotation(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                }
            }
            if (args.length >= 7) {
                String[] split = args[6].split(",");
                particle.setRightRotation(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
            }
            Display display = particle.create(playerData.getViewers(), playerData.getEyeLocation());
            if (args.length >= 9) {
                SomTask.delay(() -> {
                    display.setInterpolationDuration(Integer.parseInt(args[7]));
                    display.setInterpolationDelay(-1);
                    String[] split;
                    if (args[8].contains("Look")) {
                        particle.setOffset(playerData.getDirection().multiply(Double.parseDouble(args[8].replace("Look", ""))));
                    } else{
                        split = args[8].split(",");
                        particle.setOffset(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                    }
                    if (args.length >= 10) {
                        split = args[9].split(",");
                        particle.setScale(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                    }
                    if (args.length >= 11) {
                        split = args[10].split(",");
                        particle.setLeftRotation(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                    }
                    if (args.length >= 12) {
                        split = args[11].split(",");
                        particle.setRightRotation(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                    }
                    display.setTransformation(particle);
                }, 2);
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
        if (args.length == 1) return List.of(new String[]{"Item", "Block", "Text"});
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            switch (args[0]) {
                case "Item" -> {
                    for (Material material : Material.values()) {
                        if (material.isItem()) list.add(material.toString());
                    }
                }
                case "Block" -> {
                    for (Material material : Material.values()) {
                        if (material.isBlock()) list.add(material.toString());
                    }
                }
                case "Text" -> list.add("Text");
            }
        }
        if (args.length == 3) {
            for (Display.Billboard billboard : Display.Billboard.values()) {
                list.add(billboard.toString());
            }
        }
        if (args.length == 4) list.add("Time(Tick)");
        if (args.length == 5) list.add("Scale");
        if (args.length == 6) list.add("LeftRotation");
        if (args.length == 7) list.add("RightRotation");
        if (args.length == 8) list.add("AnimTick");
        if (args.length == 9) list.add("AnimeTransform");
        if (args.length == 10) list.add("AnimeScale");
        if (args.length == 11) list.add("AnimeLeftRotation");
        if (args.length == 12) list.add("AnimeRightRotation");
        return list;
    }
}

