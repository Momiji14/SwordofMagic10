package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.SomParty;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class Party implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            if (!playerData.hasParty()) {
                if (args[0].equalsIgnoreCase("create")) {
                    String id = args.length >= 2 ? args[1] : player.getName() + "のパーティ";
                    if (!SomParty.getPartyList().containsKey(id)) {
                        SomParty.create(id, playerData);
                    } else {
                        playerData.sendMessage("§aすでに§aその§eパーティ名§aは使われています", SomSound.Nope);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("accept")) {
                    SomParty.inviteAccept(playerData);
                    return true;
                }
            } else {
                SomParty party = playerData.getParty();
                if (args[0].equalsIgnoreCase("leave")) {
                    party.leavePlayer(playerData);
                    return true;
                } else if (args[0].equalsIgnoreCase("status")) {
                    List<String> message = new ArrayList<>();
                    message.add(decoText(party.getId()));
                    message.add("§7・§e" + party.getLeader().getDisplayName());
                    for (PlayerData member : party.getMember()) {
                        if (member != party.getLeader()) {
                            message.add("§7・§r" + party.getLeader().getDisplayName());
                        }
                    }
                    playerData.sendMessage(message, SomSound.Tick);
                    return true;
                }
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        Player invitePlayer = Bukkit.getPlayer(args[1]);
                        if (invitePlayer != null && invitePlayer.isOnline()) {
                            playerData.getParty().invitePlayer(playerData, PlayerData.get(invitePlayer));
                        } else {
                            playerData.sendMessage("§cオフライン§aまたは§c存在§aしない§eプレイヤー§aです", SomSound.Nope);
                        }
                        return true;
                    }
                }
            }
        }
        List<String> message = new ArrayList<>();
        if (!playerData.hasParty()) {
            message.add("§7・§e/party create [<name>]§7:§r §aパーティを作成します");
            if (SomParty.hasInvite(playerData)) {
                message.add("§7・§e/party accept§7:§r §a招待を受け取ります");
            }
        } else {
            message.add("§7・§e/party invite <player>§7:§r §aプレイヤーを招待します");
            message.add("§7・§e/party leave§7:§r §aパーティを脱退します");
        }
        playerData.sendMessage(message, SomSound.Tick);
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            if (!playerData.hasParty()) {
                complete.add("create");
                if (SomParty.hasInvite(playerData)) {
                    complete.add("accept");
                }
            } else {
                complete.add("invite");
                complete.add("leave");
            }
        }
        if (args.length == 2) {
            if (!playerData.hasParty() && args[0].equalsIgnoreCase("create")) {
                complete.add(player.getName() + "のパーティ");
            } else if (playerData.hasParty() && args[0].equalsIgnoreCase("invite")) {
                complete.addAll(PlayerData.getComplete());
            }
        }
        return complete;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return null;
    }
}
