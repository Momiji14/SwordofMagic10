package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.Quest.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.util.*;

import static SwordofMagic10.Component.Function.loreText;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.World;

public class QuestDataLoader {
    private static final HashMap<String, QuestData> questDataList = new HashMap<>();
    private static final HashMap<String, List<QuestData>> handler = new HashMap<>();
    private static final List<QuestData> list = new ArrayList<>();
    public static QuestData getQuestData(String id) {
        if (!questDataList.containsKey(id)) Log("§c存在しないQuestが参照されました -> " + id, true);
        return questDataList.get(id);
    }

    public static HashMap<String, List<QuestData>> getHandler() {
        return handler;
    }

    public static List<QuestData> getQuestDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (QuestData questData : getQuestDataList()) {
            complete.add(questData.getId());
        }
        return complete;
    }

    public static void load() {
        questDataList.clear();
        list.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "QuestData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                QuestData questData = new QuestData();
                questData.setId(id);
                questData.setDisplay(data.getString("Display"));
                questData.setLore(loreText(data.getStringList("Lore")));
                questData.setCycle(QuestData.Cycle.valueOf(data.getString("Cycle")));
                questData.setHandler(data.getString("Handler"));
                questData.setReqLevel(data.getInt("ReqLevel", questData.getReqLevel()));
                questData.setMaxLevel(data.getInt("MaxLevel", questData.getMaxLevel()));
                if (data.isSet("ReqQuest")) {
                    List<String> reqQuest = data.getStringList("ReqQuest");
                    questData.setReqQuest(reqQuest);
                }
                if (data.isSet("ReqClass")) {
                    List<ClassType> reqClass = new ArrayList<>();
                    for (String str : data.getStringList("ReqClass")) {
                        reqClass.add(ClassType.valueOf(str));
                    }
                    questData.setReqClass(reqClass);
                }
                questData.setSortIndex(data.getInt("SortIndex", questData.getSortIndex()));
                questData.setOverrideSlot(data.getInt("OverrideSlot", questData.getOverrideSlot()));
                int phaseIndex = 0;
                String key = "Phase-" + phaseIndex + ".";
                if (!data.isSet(key + "Display")) {
                    Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-0が設定されていません");
                    throw new RuntimeException(questData.getId() + "のPhase-0が設定されていません");
                }
                while (data.isSet(key + "Display")) {
                    QuestPhase phase;
                    QuestPhase.Type type = QuestPhase.Type.valueOf(data.getString(key + "Type"));
                    switch (type) {
                        case PassItem -> {
                            QuestPassItem questPassItem = new QuestPassItem(questData);
                            if (data.isSet(key + "Handler")) {
                                questPassItem.setHandler(data.getString(key + "Handler"));
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にHandlerが設定されていません");
                            }
                            if (data.isSet(key + "ReqItem")) {
                                for (String strData : data.getStringList(key + "ReqItem")) {
                                    SomItemStack stack = ItemDataLoader.fromText(strData);
                                    questPassItem.addReqItem(stack.getItem(), stack.getAmount());
                                }
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にReqItemが設定されていません");
                            }
                            phase = questPassItem;
                        }
                        case ShowItem -> {
                            QuestShowItem questShowItem = new QuestShowItem(questData);
                            if (data.isSet(key + "Handler")) {
                                questShowItem.setHandler(data.getString(key + "Handler"));
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にHandlerが設定されていません");
                            }
                            if (data.isSet(key + "ReqItem")) {
                                for (String strData : data.getStringList(key + "ReqItem")) {
                                    SomItemStack stack = ItemDataLoader.fromText(strData);
                                    questShowItem.addReqItem(stack.getItem(), stack.getAmount());
                                }
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にReqItemが設定されていません");
                            }
                            phase = questShowItem;
                        }
                        case Talk -> {
                            QuestTalk questTalk = new QuestTalk(questData);
                            if (data.isSet(key + "Handler")) {
                                questTalk.setHandler(data.getString(key + "Handler"));
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にHandlerが設定されていません");
                            }
                            if (data.isSet(key + "Talk")) {
                                questTalk.setTalk(loreText(data.getStringList(key + "Talk")));
                            } else {
                                Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "にTalkが設定されていません");
                            }
                            phase = questTalk;
                        }
                        case Location -> {
                            QuestLocation questLocation = new QuestLocation(questData);
                            questLocation.setRadius(data.getDouble(key + "Radius"));
                            String locationKey = key + "Location.";
                            questLocation.setLocation(new Location(World, data.getDouble(locationKey + "x", 0), data.getDouble(locationKey + "y", 0), data.getDouble(locationKey + "z", 0)));
                            phase = questLocation;
                        }
                        case EnemyKill -> {
                            QuestEnemyKill questEnemyKill = new QuestEnemyKill(questData);
                            for (String strData : data.getStringList(key + "Req")) {
                                String[] split = strData.split(",");
                                String enemyId = split[0];
                                int count = 0;
                                int minLevel = 0;
                                int maxLevel = Integer.MAX_VALUE;
                                MapData mapData = null;
                                DungeonDifficulty difficulty = null;
                                for (String str : split) {
                                    String[] split2 = str.split(":");
                                    switch (split2[0]) {
                                        case "Count" -> count = Integer.parseInt(split2[1]);
                                        case "MapData" -> mapData = MapDataLoader.getMapData(split2[1]);
                                        case "Difficulty" -> difficulty = DungeonDifficulty.valueOf(split2[1]);
                                        case "MinLevel" -> minLevel = Integer.parseInt(split2[1]);
                                        case "MaxLevel" -> maxLevel = Integer.parseInt(split2[1]);
                                    }
                                }
                                if (!enemyId.equals("Any") && !MobDataLoader.getComplete().contains(enemyId)) Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "の"  + enemyId + "は存在しないEnemyIDです");
                                if (count == 0) Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "の"  + enemyId + "にCountが設定されていません");
                                questEnemyKill.setQuestCount(enemyId, count, mapData, difficulty, minLevel, maxLevel);
                            }
                            phase = questEnemyKill;
                        }
                        case Hunting -> {
                            QuestHunting questHunting = new QuestHunting(questData);
                            for (String strData : data.getStringList(key + "Req")) {
                                String[] split = strData.split(",");
                                EntityType entityType = EntityType.valueOf(split[0]);
                                int count = 0;
                                for (String str : split) {
                                    String[] split2 = str.split(":");
                                    switch (split2[0]) {
                                        case "Count" -> count = Integer.parseInt(split2[1]);
                                    }
                                }
                                questHunting.setQuestCount(entityType, count);
                            }
                            phase = questHunting;
                        }
                        case DungeonClear -> {
                            QuestDungeonClear questDungeonClear = new QuestDungeonClear(questData);
                            questDungeonClear.setDungeonID(data.getString(key + "DungeonID"));
                            String difficulty = data.getString(key + "Difficulty", "Any");
                            questDungeonClear.setDifficulty(difficulty.equals("Any") ? null : DungeonDifficulty.valueOf(difficulty));
                            questDungeonClear.setReqCount(data.getInt(key + "ReqCount", 1));
                            phase = questDungeonClear;
                        }
                        case Special -> {
                            QuestSpecial questSpecial = new QuestSpecial(questData);
                            questSpecial.setSpecialID(QuestSpecial.SpecialID.valueOf(data.getString(key + "SpecialID")));
                            phase = questSpecial;
                        }
                        default -> {
                            Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase-" + phaseIndex + "が無効なTypeです");
                            throw new RuntimeException(questData.getId() + "のPhase-" + phaseIndex + "が無効なTypeです");
                        }
                    }
                    phase.setDisplay(data.getString(key + "Display"));
                    phase.setLore(loreText(data.getStringList(key + "Lore")));
                    phase.setType(type);
                    String classExpText = data.getString(key + "Reward.ClassExp", "0");
                    if (classExpText.contains("Dungeon:")) {
                        String[] split = classExpText.split(",");
                        DungeonInstance dungeon = null;
                        DungeonDifficulty difficulty = null;
                        double multiply = 1;
                        for (String str : split) {
                            String[] split2 = str.split(":");
                            switch (split2[0]) {
                                case "Dungeon" -> dungeon = DungeonDataLoader.getDungeon(split2[1]);
                                case "Difficulty" -> difficulty = DungeonDifficulty.valueOf(split2[1]);
                                case "Multiply" -> multiply = Double.parseDouble(split2[1]);
                            }
                        }
                        if (dungeon != null && difficulty != null) {
                            phase.setClassExp(dungeon.getReward(difficulty).getExp() * multiply);
                        }
                    } else {
                        phase.setClassExp(Double.parseDouble(classExpText));
                    }

                    phase.setEquipmentExp(data.getInt(key + "Reward.EquipmentExp", 0));
                    phase.setMel(data.getInt(key + "Reward.Mel", 0));
                    if (data.isSet(key + "Reward.Item")) {
                        for (String strData : data.getStringList(key + "Reward.Item")) {
                            phase.addItem(ItemDataLoader.fromText(strData));
                        }
                    }
                    questData.addPhase(phase);
                    phaseIndex++;
                    key = "Phase-" + phaseIndex + ".";
                }
                if (questData.getPhase().isEmpty()) {
                    Log("§a[QuestDataLoader]§c" + questData.getId() + "のPhase数が0です");
                    throw new RuntimeException(questData.getId() + "のPhase数が0です");
                }
                if (!handler.containsKey(questData.getHandler())) handler.put(questData.getHandler(), new ArrayList<>());
                handler.get(questData.getHandler()).add(questData);
                questDataList.put(id, questData);
                list.add(questData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
            for (List<QuestData> list : handler.values()) {
                list.sort(Comparator.comparing(QuestData::getSortIndex));
            }
        }
        Log("§a[QuestDataLoader]§b" + questDataList.size() + "個をロードしました");
    }
}
