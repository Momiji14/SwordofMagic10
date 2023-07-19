package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Quest.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
                if (data.isSet("ReqQuest")) {
                    List<String> reqQuest = data.getStringList("ReqQuest");
                    questData.setReqQuest(reqQuest);
                }
                questData.setSortIndex(data.getInt("SortIndex"));
                int phaseIndex = 0;
                String key = "Phase-" + phaseIndex + ".";
                while (data.isSet(key + "Display")) {
                    QuestPhase phase;
                    QuestPhase.Type type = QuestPhase.Type.valueOf(data.getString(key + "Type"));
                    switch (type) {
                        case PassItem -> {
                            QuestPassItem questPassItem = new QuestPassItem(questData);
                            questPassItem.setHandler(data.getString(key + "Handler"));
                            for (String strData : data.getStringList(key + "ReqItem")) {
                                SomItemStack stack = ItemDataLoader.fromText(strData);
                                questPassItem.addReqItem(stack.getItem(), stack.getAmount());
                            }
                            phase = questPassItem;
                        }
                        case Talk -> {
                            QuestTalk questTalk = new QuestTalk(questData);
                            questTalk.setHandler(data.getString(key + "Handler"));
                            questTalk.setTalk(loreText(data.getStringList(key + "Talk")));
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
                                for (String str : split) {
                                    String[] split2 = str.split(":");
                                    switch (split2[0]) {
                                        case "Count" -> count = Integer.parseInt(split2[1]);
                                        case "MinLevel" -> minLevel = Integer.parseInt(split2[1]);
                                        case "MaxLevel" -> maxLevel = Integer.parseInt(split2[1]);
                                    }
                                }
                                questEnemyKill.setQuestCount(enemyId, count, minLevel, maxLevel);
                            }
                            phase = questEnemyKill;
                        }
                        case DungeonClear -> {
                            QuestDungeonClear questDungeonClear = new QuestDungeonClear(questData);
                            questDungeonClear.setDungeonID(data.getString(key + "DungeonID"));
                            String difficulty = data.getString(key + "Difficulty", "All");
                            questDungeonClear.setDifficulty(difficulty.equals("All") ? null : DungeonDifficulty.valueOf(difficulty));
                            questDungeonClear.setReqCount(data.getInt(key + "ReqCount", 1));
                            phase = questDungeonClear;
                        }
                        case Special -> {
                            QuestSpecial questSpecial = new QuestSpecial(questData);
                            questSpecial.setSpecialID(QuestSpecial.SpecialID.valueOf(data.getString(key + "SpecialID")));
                            phase = questSpecial;
                        }
                        default -> phase = new QuestSpecial(questData);
                    }
                    phase.setDisplay(data.getString(key + "Display"));
                    phase.setLore(loreText(data.getStringList(key + "Lore")));
                    phase.setType(type);
                    phase.setClassExp(data.getInt(key + "Reward.ClassExp", 0));
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
