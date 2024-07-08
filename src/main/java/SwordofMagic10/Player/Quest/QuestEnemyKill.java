package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static SwordofMagic10.Component.Function.decoText;

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

    public HashMap<String, QuestEnemyKillContainer> getQuestCount() {
        return questCount;
    }

    public HashMap<String, Integer> getCount() {
        return count;
    }

    public void setQuestCount(String id, int req, MapData mapData, DungeonDifficulty difficulty, int minLevel, int maxLevel) {
        questCount.put(id, new QuestEnemyKillContainer(id, req, mapData, difficulty, minLevel, maxLevel));
    }

    public void kill(EnemyData enemyData) {
        String id = enemyData.getMobData().getId();
        if (questCount.containsKey("Any")) {
            addCount("Any", enemyData);
        }
        if (enemyData.isBoss() && questCount.containsKey("Boss")) {
            addCount("Boss", enemyData);
        }
        if (enemyData.getMobData().getTier() == MobData.Tier.LegendRaidBoss && questCount.containsKey("LegendRaidBoss")) {
            addCount("LegendRaidBoss", enemyData);
        }
        if (enemyData.getMobData().getTier() == MobData.Tier.WorldRaidBoss && questCount.containsKey("WorldRaidBoss")) {
            addCount("WorldRaidBoss", enemyData);
        }
        if (questCount.containsKey(id)) {
            addCount(id, enemyData);
        }
    }

    public void addCount(String id, EnemyData enemyData) {
        if (questCount.get(id).mapData() == null || questCount.get(id).mapData() == enemyData.getMapData()) {
            if (questCount.get(id).difficulty == null || questCount.get(id).difficulty == enemyData.getDifficulty()) {
                if (questCount.get(id).minLevel() <= enemyData.getLevel() && enemyData.getLevel() <= questCount.get(id).maxLevel()) {
                    count.merge(id, 1, Integer::sum);
                }
            }
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

    @Override
    public List<String> sidebarLine(PlayerData playerData) {
        List<String> line = new ArrayList<>();
        getQuestCount().forEach((id, container) -> line.add("§7・§c" + id + " §a" + getCount().getOrDefault(id, 0) + "/" + container.count()));
        return line;
    }

    public record QuestEnemyKillContainer(String id, int count, MapData mapData, DungeonDifficulty difficulty, int minLevel, int maxLevel) {
        public String getDisplay() {
            return id().equals("Any") ? "指定なし" : id();
        }
    }
}
