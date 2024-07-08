package SwordofMagic10.Player.Quest;

import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.entity.EntityType;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static SwordofMagic10.Component.Function.decoText;

public class QuestHunting extends QuestPhase implements Cloneable {

    private final HashMap<EntityType, Integer> questCount = new HashMap<>();
    private HashMap<EntityType, Integer> count = new HashMap<>();

    public QuestHunting(QuestData questData) {
        super(questData);
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        for (Map.Entry<EntityType, Integer> entry : questCount.entrySet()) {
            int req = entry.getValue();
            if (req > count.getOrDefault(entry.getKey(), 0)) {
                return false;
            }
        }
        return true;
    }

    public HashMap<EntityType, Integer> getQuestCount() {
        return questCount;
    }

    public HashMap<EntityType, Integer> getCount() {
        return count;
    }

    public void kill(EntityType type) {
        if (questCount.containsKey(type)) {
            addCount(type);
        }
    }

    public void setQuestCount(EntityType type, int req) {
        questCount.put(type, req);
    }

    public void addCount(EntityType type) {
        count.merge(type, 1, Integer::sum);
    }

    public void setCount(EntityType type, int count) {
        this.count.put(type, count);
    }

    @Override
    public List<String> sidebarLine(PlayerData playerData) {
        List<String> line = new ArrayList<>();
        getQuestCount().forEach((entityType, value) -> line.add("§7・§c" + entityType + " §a" + getCount().get(entityType) + "/" + value));
        return line;
    }

    @Override
    public QuestHunting clone() {
        QuestHunting clone = (QuestHunting) super.clone();
        clone.count = new HashMap<>();
        return clone;
    }
}
