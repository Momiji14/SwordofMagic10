package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MobDataLoader;
import SwordofMagic10.Item.*;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GetItem implements SomCommand, SomTabComplete {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            int amount = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
            SomItem item = ItemDataLoader.getItemData(args[0]);
            if (args.length >= 3) {
                item.setTier(Integer.parseInt(args[2]));
            }
            if (args.length >= 4) {
                if (item instanceof SomQuality quality) {
                    quality.setLevel(Integer.parseInt(args[3]));
                }
            }
            if (args.length >= 5) {
                if (item instanceof SomQuality quality) {
                    quality.setQuality(Double.parseDouble(args[4]));
                }
            }
            if (args.length >= 6) {
                if (item instanceof SomPlus plus) {
                    plus.setPlus(Integer.parseInt(args[5]));
                } else if (item instanceof SomRune rune) {
                    rune.setPower(Double.parseDouble(args[5]));
                }
            }
            if (item instanceof SomAmulet amulet) {
                amulet.randomStatus();
            }
            if (item instanceof SomPet pet) {
                pet.initialize();
                pet.setOwner(playerData);
                pet.setMobData(MobDataLoader.getMobData(args[4]));
                pet.addLevel(Integer.parseInt(args[3]));
                if (args.length >= 6) pet.setPerson1(SomPet.Person1.valueOf(args[5]));
                if (args.length >= 7) pet.setPerson2(SomPet.Person2.valueOf(args[6]));
                if (args.length >= 8) pet.setPerson3(SomPet.Person3.valueOf(args[7]));
                if (args.length >= 9) pet.setPerson4(SomPet.Person4.valueOf(args[8]));
                pet.load();
            }
            playerData.getItemInventory().add(item, amount);
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
        switch (args.length) {
            case 1 -> {
                return ItemDataLoader.getComplete();
            }
            case 2 -> complete.add("Amount");
            case 3 -> complete.add("Tier");
            case 4 -> complete.add("Level");
            case 5 -> {
                if (args[0].equals("SomPet")) {
                    return MobDataLoader.getComplete();
                } else {
                    complete.add("Quality");
                }
            }
            case 6 -> {
                if (args[0].equals("SomPet")) {
                    return SomPet.Person1.getComplete();
                } else complete.add("Plus/RunePower");
            }
            case 7 -> {
                if (args[0].equals("SomPet")) return SomPet.Person2.getComplete();
            }
            case 8 -> {
                if (args[0].equals("SomPet")) return SomPet.Person3.getComplete();
            }
            case 9 -> {
                if (args[0].equals("SomPet")) return SomPet.Person4.getComplete();
            }
        }
        return complete;
    }
}

