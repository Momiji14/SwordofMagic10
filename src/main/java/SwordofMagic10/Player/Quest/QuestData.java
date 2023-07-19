package SwordofMagic10.Player.Quest;

import java.util.ArrayList;
import java.util.List;

public class QuestData {

    private String id;
    private String display;
    private List<String> lore;
    private Cycle cycle;
    private String handler;
    private int reqLevel = 1;
    private List<String> reqQuest = new ArrayList<>();
    private int sortIndex = 0;
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

    public List<String> getReqQuest() {
        return reqQuest;
    }

    public void setReqQuest(List<String> reqQuest) {
        this.reqQuest = reqQuest;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public List<QuestPhase> getPhase() {
        return phase;
    }

    public void addPhase(QuestPhase questPhase) {
        phase.add(questPhase);
    }

    public enum Cycle {
        One("一度のみ"),
        Day("デイリー"),
        Weak("ウィークリー"),
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
