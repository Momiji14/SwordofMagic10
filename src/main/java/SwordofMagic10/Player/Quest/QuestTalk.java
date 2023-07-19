package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;
import static SwordofMagic10.Component.Function.loreText;

public class QuestTalk extends QuestPhase implements Cloneable {

    private List<String> talk = new ArrayList<>();
    private String handler;
    public QuestTalk(QuestData questData) {
        super(questData);
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setTalk(List<String> talk) {
        this.talk = loreText(talk);
    }

    public List<String> getTalk() {
        return talk;
    }

    private boolean talking = false;
    public synchronized void talk(PlayerData playerData) {
        if (!talking) {
            talking = true;
            playerData.sendMessage(decoText(getQuestData().getDisplay()), SomSound.Tick);
            playerData.closeInventory();
            SomTask.run(() -> {
                int lineIndex = 0;
                int oneIndex = 0;
                int wait = 0;
                StringBuilder title = new StringBuilder();
                while (getTalk().size() > lineIndex) {
                    String[] split = getTalk().get(lineIndex).split("");
                    if (split.length > oneIndex) {
                        wait = 120 - playerData.getQuestMenu().getTalkSpeed();
                        title.append(split[oneIndex]);
                        while (split.length > oneIndex+1 && split[oneIndex+1].equals("§")) {
                            title.append(split[oneIndex + 1]).append(split[oneIndex + 2]);
                            oneIndex += 2;
                        }
                        playerData.sendTitle("", title.toString(), 0, 25, 5);
                        SomSound.Tick.play(playerData);
                        oneIndex++;
                    } else if (getTalk().size() > lineIndex) {
                        wait = 1000 - (playerData.getQuestMenu().getTalkSpeed()*5);
                        oneIndex = 0;
                        lineIndex++;
                        playerData.sendMessage(title.toString(), SomSound.Tick);
                        title.delete(0, title.length());
                    }
                    if (playerData.getPlayer().isSneaking()) wait = 10;
                    SomTask.wait(wait);
                }
                flag = true;
                talking = false;
            });
        }
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        return flag;
    }

    @Override
    public SomJson toJson() {
        SomJson json = new SomJson();
        return json;
    }

    @Override
    public QuestPhase fromJson(SomJson json) {
        return clone();
    }

    @Override
    public QuestTalk clone() {
        QuestTalk clone = (QuestTalk) super.clone();
        clone.talking = false;
        return clone;
    }
}
