package SwordofMagic10.Player.Quest;

import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class QuestDungeonClear extends QuestPhase implements Cloneable {

    private String dungeonID;
    private DungeonDifficulty difficulty;
    private int reqCount = 1;
    private int count = 0;

    public QuestDungeonClear(QuestData questData) {
        super(questData);
    }

    @Override
    public List<String> sidebarLine(PlayerData playerData) {
        List<String> list = new ArrayList<>();
        list.add("§7・§e" + getDungeonDisplay() + ":" + getDifficultyDisplay() + " §a" + count + "/" + reqCount);
        return list;
    }

    public String getDungeonDisplay() {
        return dungeonID.equals("Any") ? "指定なし" : DungeonInstance.get(dungeonID).getDisplay();
    }

    public String getDungeonID() {
        return dungeonID;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public String getDifficultyDisplay() {
        return difficulty == null ? "Any" : difficulty.toString();
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
        if (getDungeonID().equals(dungeon.getId()) || getDungeonID().equals("Any")){
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
    public QuestDungeonClear clone() {
        QuestDungeonClear clone = (QuestDungeonClear) super.clone();
        clone.count = getCount();
        return clone;
    }
}
