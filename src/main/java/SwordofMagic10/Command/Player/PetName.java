package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetName implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 2) {
            try {
                int index = Integer.parseInt(args[0].split(":")[0]);
                if (playerData.getItemInventory().getInventory().size() > index) {
                    SomItemStack stack = playerData.getItemInventory().getInventory().get(index);
                    if (stack.getItem() instanceof SomPet pet && args[1].length() <= 10) {
                        pet.setPetName(args[1]);
                        playerData.sendMessage(pet.getPetName() + "§aと名付けました", SomSound.Level);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerData.sendMessage("§e/petName <スロット番号> <名前 1~10文字>", SomSound.Nope);
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        List<String> complete = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                complete.addAll(Trade.invComplete(playerData));
                complete.removeIf(text -> !text.contains("種別") || !text.contains("レベル"));
            }
            case 2 -> complete.add("名前");
        }
        return complete;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return null;
    }
}
