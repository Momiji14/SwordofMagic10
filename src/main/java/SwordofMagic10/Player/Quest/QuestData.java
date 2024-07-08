package SwordofMagic10.Player.Quest;

import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;

import java.util.ArrayList;
import java.util.List;

public class QuestData {

    private String id;
    private String display;
    private List<String> lore;
    private Cycle cycle;
    private String handler;
    private int reqLevel = 1;
    private int maxLevel = Classes.MaxLevel;
    private List<String> reqQuest = new ArrayList<>();
    private List<ClassType> reqClass = new ArrayList<>();
    private int sortIndex = 0;
    private int overrideSlot = -1;
    private final List<QuestPhase> phase = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public List<String> getReqQuest() {
        return reqQuest;
    }

    public void setReqQuest(List<String> reqQuest) {
        this.reqQuest = reqQuest;
    }

    public List<ClassType> getReqClass() {
        return reqClass;
    }

    public void setReqClass(List<ClassType> reqClass) {
        this.reqClass = reqClass;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public int getOverrideSlot() {
        return overrideSlot;
    }

    public void setOverrideSlot(int overrideSlot) {
        this.overrideSlot = overrideSlot;
    }

    public List<QuestPhase> getPhase() {
        return phase;
    }

    public void addPhase(QuestPhase questPhase) {
        phase.add(questPhase);
    }

    public boolean classCheck(ClassType classType) {
        return getReqClass().isEmpty() || getReqClass().contains(classType);
    }

    public enum Cycle {
        One("一度のみ"),
        Daily("デイリー"),
        Weak("ウィークリー"),
        Many("制限なし"),
        ;

        private final String display;

        Cycle(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
