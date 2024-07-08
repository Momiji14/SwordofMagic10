package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomQuality;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.MinMax;
import static SwordofMagic10.Component.Function.unDecoText;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.SNCChannel;


public class Trade implements SomCommand, SomTabComplete {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1 && playerData.isTutorialClear()) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target != player) {
                PlayerData targetData = PlayerData.get(target);
                if (args.length >= 2) {
                    switch (args[1]) {
                        case "sendItem" -> {
                            if (args.length >= 3) {
                                try {
                                    int index = MinMax(Integer.parseInt(args[2].split(":")[0]), 0, 1000000);
                                    int amount = args.length >= 4 ? MinMax(Integer.parseInt(args[3]), 1, 1000000) : 1;
                                    SomItemStack stack = playerData.getItemInventory().getInventory().get(index).clone();
                                    stack.setAmount(Math.min(amount, stack.getAmount()));
                                    playerData.getItemInventory().remove(stack);
                                    targetData.getItemInventory().add(stack);
                                    playerData.sendSomText(SomText.create(targetData.getDisplayName() + "§aに").add(stack.toSomText()).add("§aを送りました"));
                                    targetData.sendSomText(SomText.create(playerData.getDisplayName() + "§aから").add(stack.toSomText()).add("§aが送られてきました"));
                                    TradeLog(playerData, targetData, stack.toJson().toString());
                                } catch (Exception e) {
                                    playerData.sendMessage("§c無効§aな値です", SomSound.Nope);
                                }
                            }
                        }
                        case "sendMel" -> {
                            try {
                                int mel = MinMax(Integer.parseInt(args[2]), 1, 100000000);
                                if (playerData.getMel() >= mel) {
                                    playerData.removeMel(mel);
                                    targetData.addMel(mel);
                                    playerData.sendMessage(targetData.getDisplayName() + "§aに§e" + mel + "メル§aを送りました");
                                    targetData.sendMessage(playerData.getDisplayName() + "§aから§e" + mel + "メル§aが送られてきました");
                                    TradeLog(playerData, targetData, mel + "メル");
                                }
                            } catch (Exception e) {
                                playerData.sendMessage("§c無効§aな値です", SomSound.Nope);
                            }
                        }
                    }
                }
            } else {
                playerData.sendMessage("§a存在しない§eプレイヤー§aです", SomSound.Nope);
            }
            return true;
        }
        List<String> message = new ArrayList<>();
        message.add("§7・§e/trade <player> sendItem <index> [<amount>]");
        message.add("§7・§e/trade <player> sendMel <mel>");
        playerData.sendMessage(message, SomSound.Nope);
        return true;
    }

    public void TradeLog(PlayerData sender, PlayerData receiver, String log) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Som10TradeLog");
            out.writeUTF(sender.getPlayer().getName() + " -> " + receiver.getPlayer().getName() + ": " + log);
            sender.getPlayer().sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            complete.addAll(PlayerData.getComplete());
        }
        if (args.length == 2) {
            complete.add("sendItem");
            complete.add("sendMel");
        }
        if (args.length == 3) {
            if (args[1].equals("sendItem")) {
                return invComplete(playerData);
            }
        }
        return complete;
    }

    public static List<String> invComplete(PlayerData playerData) {
        List<String> complete = new ArrayList<>();
        int index = 0;
        for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
            String prefix = "";
            if (index < 10) {
                prefix = "00";
            } else if (index < 100) {
                prefix = "0";
            }
            SomItem item = stack.getItem();
            StringBuilder builder = new StringBuilder();
            if (item instanceof SomQuality quality) {
                builder.append(",レベル:").append(quality.getLevel()).append(",品質:").append(quality.getQualityRank().getDisplay());
            }
            if (item instanceof SomPet pet) {
                builder.append("種別:").append(pet.getMobData().getId()).append(",レベル:").append(pet.getLevel());
            }
            complete.add(prefix + index + ":" + stack.getItem().getDisplay() + "T" + stack.getItem().getTier() + builder);
            index++;
        }
        return complete;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return null;
    }
}

