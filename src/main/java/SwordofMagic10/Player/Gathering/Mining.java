package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

import static SwordofMagic10.Player.Gathering.GatheringMenu.GatheringTime;
import static org.bukkit.Material.*;

public class Mining {
    private static final List<GatheringTable> baseTable = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.025));
        add(new GatheringTable(ItemDataLoader.getItemData("品質変更石"), 0.0025));
    }};
    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> stone = new ArrayList<>(baseTable);
        stone.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 1.0));
        put(STONE, stone);
        put(COBBLESTONE, stone);
        put(ANDESITE, stone);

        List<GatheringTable> coal = new ArrayList<>(baseTable);
        coal.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.3));
        coal.add(new GatheringTable(ItemDataLoader.getItemData("石炭"), 0.7));
        coal.add(new GatheringTable(ItemDataLoader.getItemData("欠けたダイヤモンド"), 0.01));
        put(COAL_ORE, coal);

        List<GatheringTable> copper = new ArrayList<>(baseTable);
        copper.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.3));
        copper.add(new GatheringTable(ItemDataLoader.getItemData("銅鉱石"), 0.7));
        put(COPPER_ORE, copper);
        
        List<GatheringTable> iron = new ArrayList<>(baseTable);
        iron.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.3));
        iron.add(new GatheringTable(ItemDataLoader.getItemData("鉄鉱石"), 0.7));
        put(IRON_ORE, iron);

        List<GatheringTable> gold = new ArrayList<>(baseTable);
        gold.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 0.3));
        gold.add(new GatheringTable(ItemDataLoader.getItemData("金鉱石"), 0.7));
        gold.add(new GatheringTable(ItemDataLoader.getItemData("欠けたエメラルド"), 0.002));
        put(GOLD_ORE, gold);

        List<GatheringTable> nether = new ArrayList<>(baseTable);
        nether.add(new GatheringTable(ItemDataLoader.getItemData("石材"), 1.0));
        nether.add(new GatheringTable(ItemDataLoader.getItemData("残骸"), 0.02));
        put(NETHERRACK, nether);

    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();

    public Mining(PlayerData playerData) {
        this.playerData = playerData;
        SomTask.timerPlayer(playerData, () -> {
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

    public HashMap<Location, Integer> getCoolTime() {
        return coolTime;
    }

    public void mining(Block block) {
        if (coolTime.containsKey(block.getLocation())) {
            //playerData.sendMessage("§aこの§e鉱石§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e採掘可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Mining);
            if (tool == null) {
                playerData.sendMessage("§e採掘§aするには§e採掘道具§aが必要です", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), GatheringTime);
            playerData.getStatistics().add(Statistics.Type.MiningCount, 1);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Mining, Table.get(block.getType()), tool);
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.COBBLESTONE.createBlockData()), 2);
        }
    }
}
