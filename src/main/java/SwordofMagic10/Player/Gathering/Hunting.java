package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestHunting;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hunting {
    private static final List<GatheringTable> baseTable = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.025));
        add(new GatheringTable(ItemDataLoader.getItemData("品質変更石"), 0.0025));
        add(new GatheringTable(ItemDataLoader.getItemData("屑肉"), 0.05));
        add(new GatheringTable(ItemDataLoader.getItemData("動物の血"), 0.25));
    }};

    public static final HashMap<EntityType, List<GatheringTable>> Table = new HashMap<>() {{
        List<GatheringTable> chicken = new ArrayList<>(baseTable);
        chicken.add(new GatheringTable(ItemDataLoader.getItemData("生の鶏肉"), 0.5));
        chicken.add(new GatheringTable(ItemDataLoader.getItemData("卵"), 0.2));
        put(EntityType.CHICKEN, chicken);

        List<GatheringTable> rabbit = new ArrayList<>(baseTable);
        rabbit.add(new GatheringTable(ItemDataLoader.getItemData("生の兎肉"), 0.69));
        rabbit.add(new GatheringTable(ItemDataLoader.getItemData("兎の前足"), 0.01));
        put(EntityType.RABBIT, rabbit);

        List<GatheringTable> cow = new ArrayList<>(baseTable);
        cow.add(new GatheringTable(ItemDataLoader.getItemData("生の牛肉"), 0.5));
        cow.add(new GatheringTable(ItemDataLoader.getItemData("ミルク"), 0.2));
        put(EntityType.COW, cow);

        List<GatheringTable> mushroom = new ArrayList<>(baseTable);
        mushroom.add(new GatheringTable(ItemDataLoader.getItemData("生の牛肉"), 0.5));
        mushroom.add(new GatheringTable(ItemDataLoader.getItemData("茶キノコ"), 0.1));
        mushroom.add(new GatheringTable(ItemDataLoader.getItemData("赤キノコ"), 0.1));
        put(EntityType.MUSHROOM_COW, mushroom);

        List<GatheringTable> sheep = new ArrayList<>(baseTable);
        sheep.add(new GatheringTable(ItemDataLoader.getItemData("生の羊肉"), 0.5));
        sheep.add(new GatheringTable(ItemDataLoader.getItemData("羊毛"), 0.2));
        put(EntityType.SHEEP, sheep);

        List<GatheringTable> pig = new ArrayList<>(baseTable);
        pig.add(new GatheringTable(ItemDataLoader.getItemData("生の豚肉"), 0.7));
        put(EntityType.PIG, pig);
    }};

    private static final HashMap<EntityType, List<Entity>> entities = new HashMap<>();

    private static final int Max = 5;
    public static void spawner() {
        for (EntityType entityType : Table.keySet()) {
            entities.put(entityType, new ArrayList<>());
        }
        CustomLocation[] locations = {
            new CustomLocation(SomCore.World, -10.5, -37, 246.5),
            new CustomLocation(SomCore.World, -1.5, -58, 237.5)
        };
        SomTask.syncTimer(() -> {
            for (CustomLocation location : locations) {
                for (EntityType entityType : Table.keySet()) {
                    entities.get(entityType).removeIf(entity -> {
                        if (entity.getLocation().distance(location) > 64) {
                            entity.remove();
                        }
                        return !entity.isValid();
                    });
                    if (entities.get(entityType).size() < Max) {
                        Entity entity = SomCore.World.spawnEntity(location.clone().randomRadius(10).lower(), entityType);
                        entity.addScoreboardTag("Hunting");
                        entity.addScoreboardTag(Config.SomEntityTag);
                        entities.get(entityType).add(entity);
                    }
                }
            }
        }, 50);
    }

    private final PlayerData playerData;

    public Hunting(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void hunting(Entity entity) {
        if (Table.containsKey(entity.getType())) {
            for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                if (somQuest.getNowPhase() instanceof QuestHunting questHunting) {
                    questHunting.kill(entity.getType());
                }
            }
            playerData.getStatistics().add(Statistics.Type.HuntingCount, 1);
            GatheringTable.gathering(playerData, GatheringMenu.Type.Hunting, Table.get(entity.getType()), playerData.getGatheringMenu().getTool(SomTool.Type.Hunting));
        }
    }

    private boolean isCoolTime = false;

    public void setCoolTime(boolean coolTime) {
        isCoolTime = coolTime;
    }

    public boolean isCoolTime() {
        return isCoolTime;
    }

    public int getCoolTime() {
        return 16 - (Math.max(0, playerData.getGatheringMenu().getTool(SomTool.Type.Hunting).getPlus()-10)*2);
    }
}
