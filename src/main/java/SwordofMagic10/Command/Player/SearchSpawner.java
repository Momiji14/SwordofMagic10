package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.SpawnerDataLoader;
import SwordofMagic10.Player.Dungeon.SpawnerData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;
import static SwordofMagic10.Component.Function.scale;

public class SearchSpawner implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length == 0) {
            List<String> message = new ArrayList<>();
            SomParticle particle = new SomParticle(Color.RED, playerData);
            List<SpawnerData> list = new ArrayList<>();
            for (SpawnerData spawnerData : SpawnerDataLoader.getSpawnerDataList()) {
                if (spawnerData.getLocation().distance(playerData.getLocation()) < spawnerData.getRadius() + 64) {
                    list.add(spawnerData);
                }
            }
            list.sort(Comparator.comparingDouble(spawnerData -> spawnerData.getLocation().distance(playerData.getLocation())));
            for (SpawnerData spawnerData : list) {
                message.add("§7・§e" + spawnerData.getId() + " " + spawnerData.getDungeonID() + " " + scale(spawnerData.getLocation().distance(playerData.getLocation()), 1) + "m");
                particle.sphere(Collections.singletonList(playerData), spawnerData.getLocation(), spawnerData.getRadius());
            }
            if (!message.isEmpty()) {
                message.add(0, decoText("周囲のスポナー"));
                playerData.sendMessage(message, SomSound.Tick);
            } else {
                playerData.sendMessage("§a周囲に§eスポナー§aがありません", SomSound.Nope);
            }
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}
