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

public class Collect {

    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> poppy = new ArrayList<>();
        poppy.add(new GatheringTable(ItemDataLoader.getItemData("ポピー"), 1.2));
        put(POPPY, poppy);

        List<GatheringTable> blueOrchid = new ArrayList<>();
        blueOrchid.add(new GatheringTable(ItemDataLoader.getItemData("ヒスイラン"), 1.2));
        put(BLUE_ORCHID, blueOrchid);

        List<GatheringTable> dandelion = new ArrayList<>();
        dandelion.add(new GatheringTable(ItemDataLoader.getItemData("ポピー"), 0.6));
        dandelion.add(new GatheringTable(ItemDataLoader.getItemData("ヒスイラン"), 0.6));
        put(DANDELION, dandelion);

        List<GatheringTable> wheat = new ArrayList<>();
        wheat.add(new GatheringTable(ItemDataLoader.getItemData("小麦"), 0.6));
        wheat.add(new GatheringTable(ItemDataLoader.getItemData("米"), 0.3));
        wheat.add(new GatheringTable(ItemDataLoader.getItemData("大豆"), 0.3));
        put(WHEAT, wheat);

        List<GatheringTable> carrot = new ArrayList<>();
        carrot.add(new GatheringTable(ItemDataLoader.getItemData("ニンジン"), 0.9));
        carrot.add(new GatheringTable(ItemDataLoader.getItemData("大葉"), 0.3));
        put(CARROTS, carrot);

        List<GatheringTable> potato = new ArrayList<>();
        potato.add(new GatheringTable(ItemDataLoader.getItemData("ジャガイモ"), 0.9));
        potato.add(new GatheringTable(ItemDataLoader.getItemData("葉野菜"), 0.3));
        put(POTATOES, potato);

        List<GatheringTable> beetRoot = new ArrayList<>();
        beetRoot.add(new GatheringTable(ItemDataLoader.getItemData("ビートルート"), 0.9));
        beetRoot.add(new GatheringTable(ItemDataLoader.getItemData("胡麻"), 0.3));
        put(BEETROOTS, beetRoot);

        List<GatheringTable> grass = new ArrayList<>();
        grass.add(new GatheringTable(ItemDataLoader.getItemData("大葉"), 0.25));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("葉野菜"), 0.25));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("胡麻"), 0.25));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("大豆"), 0.25));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("米"), 0.25));
        put(GRASS, grass);
        put(TALL_GRASS, grass);

        values().forEach(list -> list.add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.01)));
    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();

    public Collect(PlayerData playerData) {
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

    public HashMap<Location, Integer> getCoolTime() {
        return coolTime;
    }

    public void collect(Block block) {
        if (coolTime.containsKey(block.getLocation())) {
            playerData.sendMessage("§aこの§e採集物§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e採集可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Collect);
            if (tool == null) {
                playerData.sendMessage("§e採集§aするには§e採集道具§aが必要です", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), 90);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Collect, Table.get(block.getType()), 1, tool);
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.AIR.createBlockData()), 2);
        }
    }
}
