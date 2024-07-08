package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static SwordofMagic10.Player.Gathering.GatheringMenu.GatheringTime;
import static org.bukkit.Material.*;

public class Collect {
    public static final int Exp = 4;
    private static final List<GatheringTable> baseTable = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.025));
        add(new GatheringTable(ItemDataLoader.getItemData("品質変更石"), 0.0025));
        add(new GatheringTable(ItemDataLoader.getItemData("生命の実"), 0.03));
    }};

    public static final HashMap<Material, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> poppy = new ArrayList<>(baseTable);
        poppy.add(new GatheringTable(ItemDataLoader.getItemData("ポピー"), 1.0));
        put(POPPY, poppy);

        List<GatheringTable> blueOrchid = new ArrayList<>(baseTable);
        blueOrchid.add(new GatheringTable(ItemDataLoader.getItemData("ヒスイラン"), 1.0));
        put(BLUE_ORCHID, blueOrchid);

        List<GatheringTable> redAndBlue = new ArrayList<>(baseTable);
        redAndBlue.add(new GatheringTable(ItemDataLoader.getItemData("ポピー"), 0.5));
        redAndBlue.add(new GatheringTable(ItemDataLoader.getItemData("ヒスイラン"), 0.5));
        put(DANDELION, redAndBlue);
        put(AZURE_BLUET, redAndBlue);
        put(LILAC, redAndBlue);
        put(PEONY, redAndBlue);
        put(ORANGE_TULIP, redAndBlue);
        put(LILY_OF_THE_VALLEY, redAndBlue);

        List<GatheringTable> wheat = new ArrayList<>(baseTable);
        wheat.add(new GatheringTable(ItemDataLoader.getItemData("小麦"), 0.7));
        wheat.add(new GatheringTable(ItemDataLoader.getItemData("米"), 0.3));
        put(WHEAT, wheat);

        List<GatheringTable> carrot = new ArrayList<>(baseTable);
        carrot.add(new GatheringTable(ItemDataLoader.getItemData("ニンジン"), 0.7));
        carrot.add(new GatheringTable(ItemDataLoader.getItemData("葉野菜"), 0.3));
        put(CARROTS, carrot);

        List<GatheringTable> potato = new ArrayList<>(baseTable);
        potato.add(new GatheringTable(ItemDataLoader.getItemData("ジャガイモ"), 0.7));
        potato.add(new GatheringTable(ItemDataLoader.getItemData("大豆"), 0.3));
        put(POTATOES, potato);

        List<GatheringTable> beetRoot = new ArrayList<>(baseTable);
        beetRoot.add(new GatheringTable(ItemDataLoader.getItemData("ビートルート"), 0.7));
        beetRoot.add(new GatheringTable(ItemDataLoader.getItemData("胡麻"), 0.3));
        put(BEETROOTS, beetRoot);

        List<GatheringTable> redMush = new ArrayList<>(baseTable);
        redMush.add(new GatheringTable(ItemDataLoader.getItemData("赤キノコ"), 1.0));
        put(RED_MUSHROOM, redMush);

        List<GatheringTable> brownMush = new ArrayList<>(baseTable);
        brownMush.add(new GatheringTable(ItemDataLoader.getItemData("茶キノコ"), 1.0));
        put(BROWN_MUSHROOM, brownMush);

        List<GatheringTable> grass = new ArrayList<>(baseTable);
        grass.add(new GatheringTable(ItemDataLoader.getItemData("薬草"), 0.6));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("大豆"), 0.1));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("米"), 0.1));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("葉野菜"), 0.1));
        grass.add(new GatheringTable(ItemDataLoader.getItemData("胡麻"), 0.1));
        put(GRASS, grass);
        put(TALL_GRASS, grass);
        put(OXEYE_DAISY, grass);
        put(CORNFLOWER, grass);
        put(SMALL_DRIPLEAF, grass);
        put(JUNGLE_SAPLING, grass);
        put(SUNFLOWER, grass);
    }};

    private final PlayerData playerData;
    private final HashMap<Location, Integer> coolTime = new HashMap<>();
    public Collect(PlayerData playerData) {
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

    private boolean isBreakable = true;
    public void collect(Block block) {
        if (!isBreakable) return;
        if (coolTime.containsKey(block.getLocation())) {
            //playerData.sendMessage("§aこの§e採集物§aは§e" + coolTime.get(block.getLocation()) + "秒後§aに§e採集可能§aになります", SomSound.Nope);
            return;
        }
        if (Table.containsKey(block.getType())) {
            SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Collect);
            if (tool == null) {
                playerData.sendMessage("§e採集§aするには§e採集道具§aが必要です", SomSound.Nope);
                return;
            }
            if (playerData.getPlayer().getInventory().getItemInMainHand().getType() != tool.getIcon()) {
                playerData.sendMessage("§e採集§aするには§e採集道具§aを使ってください", SomSound.Nope);
                return;
            }
            coolTime.put(block.getLocation(), GatheringTime);
            isBreakable = false;
            playerData.getStatistics().add(Statistics.Type.CollectCount, 1);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Collect, Table.get(block.getType()), tool);
            SomTask.sync(() -> playerData.getPlayer().setGameMode(GameMode.ADVENTURE));
            SomTask.syncDelay(() -> {
                isBreakable = true;
                playerData.getPlayer().setGameMode(GameMode.SURVIVAL);
            }, 8-Math.max(0, tool.getPlus()-10));
        }
        if (coolTime.containsKey(block.getLocation())) {
            SomTask.delay(() -> playerData.getPlayer().sendBlockChange(block.getLocation(), Material.AIR.createBlockData()), 2);
        }
    }
}
