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

public class Lumber {
    private static final List<GatheringTable> baseTable = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.025));
        add(new GatheringTable(ItemDataLoader.getItemData("品質変更石"), 0.0025));
    }};

    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> wood = new ArrayList<>(baseTable);
        wood.add(new GatheringTable(ItemDataLoader.getItemData("オークの原木"), 0.35));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("シラカバの原木"), 0.2));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("トウヒの原木"), 0.2));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("アカシアの原木"), 0.2));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("サクラの原木"), 0.05));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("精霊の葉"), 0.03));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("リンゴ"), 0.01));
        put(OAK_LOG, wood);
        put(OAK_WOOD, wood);
        put(SPRUCE_LOG, wood);
        put(SPRUCE_WOOD, wood);
        put(JUNGLE_LOG, wood);
        put(JUNGLE_WOOD, wood);
    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();

    public Lumber(PlayerData playerData) {
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


    public void lumber(Block block) {
        if (coolTime.containsKey(block.getLocation())) {
            //playerData.sendMessage("§aこの§e原木§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e伐採可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Lumber);
            if (tool == null) {
                playerData.sendMessage("§e伐採§aするには§e伐採道具§aが必要です", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), GatheringTime);
            playerData.getStatistics().add(Statistics.Type.LumberCount, 1);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Lumber, Table.get(block.getType()), tool);
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.STRIPPED_OAK_WOOD.createBlockData()), 2);
        }
    }
}
