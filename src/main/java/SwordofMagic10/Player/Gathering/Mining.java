package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.randomDouble;
import static org.bukkit.Material.*;

public class Mining {

    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> stone = new ArrayList<>();
        stone.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 1.2));
        put(STONE, stone);
        put(COBBLESTONE, stone);

        List<GatheringTable> coal = new ArrayList<>();
        coal.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.5));
        coal.add(new GatheringTable(ItemDataLoader.getItemData("石炭"), 0.7));
        coal.add(new GatheringTable(ItemDataLoader.getItemData("ダイヤモンド"), 0.01));
        put(COAL_ORE, coal);

        List<GatheringTable> copper = new ArrayList<>();
        copper.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.5));
        copper.add(new GatheringTable(ItemDataLoader.getItemData("銅鉱石"), 0.5));
        put(COPPER_ORE, copper);
        
        List<GatheringTable> iron = new ArrayList<>();
        iron.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.5));
        iron.add(new GatheringTable(ItemDataLoader.getItemData("鉄鉱石"), 0.5));
        put(IRON_ORE, iron);

        List<GatheringTable> gold = new ArrayList<>();
        gold.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.5));
        gold.add(new GatheringTable(ItemDataLoader.getItemData("金鉱石"), 0.5));
        put(GOLD_ORE, gold);

        values().forEach(list -> list.add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.05)));
    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();

    public Mining(PlayerData playerData) {
        this.playerData = playerData;
        SomTask.timer(() -> {
            coolTime.forEach((key, value) -> coolTime.put(key, value - 1));
            coolTime.entrySet().removeIf(entry -> {
                if (entry.getValue() <= 0) {
                    Location location = entry.getKey();
                    SomTask.delay(() -> playerData.getPlayer().sendBlockChange(location, location.getBlock().getBlockData()), 2);
                    return true;
                } else return false;
            });
        }, 20, 20);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }


    public void mining(Block block) {
        if (coolTime.containsKey(block.getLocation())) {
            playerData.sendMessage("§aこの§e鉱石§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e採掘可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Mining);
            if (tool == null) {
                playerData.sendMessage("§e採掘§aするには§e採掘道具§aが必要です", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), 90);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Mining, Table.get(block.getType()), 1, tool);
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.COBBLESTONE.createBlockData()), 2);
        }
    }
}
