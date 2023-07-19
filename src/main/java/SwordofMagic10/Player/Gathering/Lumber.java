package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Enemy.DropData;
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
import static SwordofMagic10.Player.Classes.Classes.MaxLevel;
import static org.bukkit.Material.*;
import static org.bukkit.Material.IRON_ORE;

public class Lumber {

    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> wood = new ArrayList<>();
        wood.add(new GatheringTable(ItemDataLoader.getItemData("オークの原木"), 0.5));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("シラカバの原木"), 0.25));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("トウヒの原木"), 0.25));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("アカシアの原木"), 0.25));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("サクラの原木"), 0.08));
        wood.add(new GatheringTable(ItemDataLoader.getItemData("リンゴ"), 0.01));
        put(OAK_LOG, wood);
        put(OAK_WOOD, wood);
        put(JUNGLE_LOG, wood);
        put(JUNGLE_WOOD, wood);
        values().forEach(list -> list.add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.02)));
    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();

    public Lumber(PlayerData playerData) {
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

    public void lumber(Block block) {
        if (coolTime.containsKey(block.getLocation())) {
            playerData.sendMessage("§aこの§e原木§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e伐採可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Lumber);
            if (tool == null) {
                playerData.sendMessage("§e伐採§aするには§e伐採道具§aが必要です", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), 90);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Lumber, Table.get(block.getType()), 1, tool);
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.STRIPPED_OAK_WOOD.createBlockData()), 2);
        }
    }
}
