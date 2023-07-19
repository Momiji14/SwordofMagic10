package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Player.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class QuestEnemyKill extends QuestPhase implements Cloneable {

    private final HashMap<String, QuestEnemyKillContainer> questCount = new HashMap<>();
    private HashMap<String, Integer> count = new HashMap<>();

    public QuestEnemyKill(QuestData questData) {
        super(questData);
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        for (Map.Entry<String, QuestEnemyKillContainer> entry : questCount.entrySet()) {
            String id = entry.getKey();
            int req = entry.getValue().count();
            if (req > count.getOrDefault(id, 0)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SomJson toJson() {
        SomJson json = new SomJson();
        getCount().forEach(json::set);
        return json;
    }

    @Override
    public QuestPhase fromJson(SomJson json) {
        QuestEnemyKill questEnemyKill = clone();
        for (String id : questEnemyKill.getQuestCount().keySet()) {
            questEnemyKill.setCount(id, json.getInt(id, 0));
        }
        return questEnemyKill;
    }

    public HashMap<String, QuestEnemyKillContainer> getQuestCount() {
        return questCount;
    }

    public HashMap<String, Integer> getCount() {
        return count;
    }

    public void setQuestCount(String id, int req, int minLevel, int maxLevel) {
        questCount.put(id, new QuestEnemyKillContainer(id, req, minLevel, maxLevel));
    }

    public void kill(EnemyData enemyData) {
        String id = enemyData.getMobData().getId();
        if (questCount.containsKey("All")) {
            addCount("All", enemyData);
        }
        if (questCount.containsKey(id)) {
            addCount(id, enemyData);
        }
    }

    public void addCount(String id, EnemyData enemyData) {
        if (questCount.get(id).minLevel() <= enemyData.getLevel() && enemyData.getLevel() <= questCount.get(id).maxLevel()) {
            count.merge(id, 1, Integer::sum);
        }
    }

    public void setCount(String id, int count) {
        this.count.put(id, count);
    }

    @Override
    public QuestEnemyKill clone() {
        QuestEnemyKill clone = (QuestEnemyKill) super.clone();
        clone.count = new HashMap<>();
        return clone;
    }

    public record QuestEnemyKillContainer(String id, int count, int minLevel, int maxLevel) {}
}
