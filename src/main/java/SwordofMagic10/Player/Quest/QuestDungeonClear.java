package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.PlayerData;

public class QuestDungeonClear extends QuestPhase implements Cloneable {

    private String dungeonID;
    private DungeonDifficulty difficulty;
    private int reqCount = 1;
    private int count = 0;

    public QuestDungeonClear(QuestData questData) {
        super(questData);
    }

    public String getDungeonID() {
        return dungeonID;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DungeonDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getReqCount() {
        return reqCount;
    }

    public void setReqCount(int reqCount) {
        this.reqCount = reqCount;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int count) {
        this.count += count;
    }

    public int getCount() {
        return count;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void clear(DungeonInstance dungeon) {
        if (getDungeonID().equals(dungeon.getId())) {
            if (getDifficulty() == null || getDifficulty() == dungeon.getDifficulty()) {
                count++;
            }
        }
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        return reqCount <= count;
    }

    @Override
    public SomJson toJson() {
        SomJson json = new SomJson();
        json.set("Count", getCount());
        return json;
    }

    @Override
    public QuestPhase fromJson(SomJson json) {
        QuestDungeonClear questDungeonClear = clone();
        questDungeonClear.setCount(json.getInt("Count", 0));
        return questDungeonClear;
    }

    @Override
    public QuestDungeonClear clone() {
        QuestDungeonClear clone = (QuestDungeonClear) super.clone();
        clone.count = getCount();
        return clone;
    }
}
