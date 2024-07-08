package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.QuestDataLoader;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class QuestMenu extends GUIManager {
    public static final String Prefix = "§6[Quest]§r";
    private final HashMap<String, SomQuest> quest = new HashMap<>();
    private final HashMap<String, LocalDateTime> clearQuest = new HashMap<>();
    private int talkSpeed = 10;
    private final QuestReceive questReceive;
    private SomQuest trackingQuest;
    public QuestMenu(PlayerData playerData) {
        super(playerData, "クエスト", 1);
        questReceive = new QuestReceive(playerData, this);
        SomTask.timerPlayer(playerData, () -> {
            if (trackingQuest != null && !quest.containsValue(trackingQuest)) setTrackingQuest(null);
            for (SomQuest quest : quest.values()) {
                if (checkQuest(quest.getQuestData()).isEmpty()) {
                    if (quest.getQuestData().classCheck(playerData.getClasses().getMainClass())) {
                        if (quest.getPhaseIndex() < quest.getPhase().size()) {
                            if (quest.getNowPhase().isProcess(playerData)) {
                                quest.nextPhase(playerData);
                            }
                        } else {
                            playerData.sendMessage(quest.getDisplay() + "§aの§e進行状況§aが§c不正§aなため§e進捗§aを§eリセット§aします");
                            quest.setPhaseIndex(0);
                        }
                    }
                }
            }
            quest.values().removeIf(SomQuest::isClearFlag);
        }, 20, 20);
    }

    public boolean hasTrackingQuest() {
        return trackingQuest != null;
    }

    public SomQuest getTrackingQuest() {
        return trackingQuest;
    }

    public void setTrackingQuest(SomQuest trackingQuest) {
        if (trackingQuest != null) {
            playerData.sendMessage(Prefix + "§e" + trackingQuest.getDisplay() + "§aを§e表示§aします", SomSound.Tick);
        } else {
            playerData.sendMessage(Prefix + "§eクエスト情報§aの§e表示§aを消します", SomSound.Tick);
        }
        this.trackingQuest = trackingQuest;
    }

    public QuestReceive getQuestReceive() {
        return questReceive;
    }

    public Set<String> getClearQuest() {
        return clearQuest.keySet();
    }

    public HashMap<String, LocalDateTime> getClearQuestTime() {
        return clearQuest;
    }

    public LocalDateTime getClearQuestTime(String id) {
        return clearQuest.get(id);
    }

    public void addClearQuest(String questID) {
        addClearQuest(questID, LocalDateTime.now());
    }

    public void addClearQuest(String questID, LocalDateTime time) {
        clearQuest.put(questID, time);
    }

    private List<QuestData> guiQuestList;
    public void openGUIQuest(List<QuestData> questList) {
        guiQuestList = questList;
        open();
    }

    public HashMap<String, SomQuest> getQuests() {
        return quest;
    }

    public SomQuest getQuest(String id) {
        return quest.get(id);
    }

    public void addQuest(SomQuest quest) {
        this.quest.put(quest.getQuestData().getId(), quest);
    }

    public int getTalkSpeed() {
        return talkSpeed;
    }

    public void setTalkSpeed(int talkSpeed) {
        this.talkSpeed = talkSpeed;
        playerData.sendMessage("§eトークスピード§aを§e" + talkSpeed + "§aに§b設定§aしました");
    }

    public void setTalkSpeedNoMessage(int talkSpeed) {
        this.talkSpeed = talkSpeed;
    }

    public List<String> checkQuest(QuestData questData) {
        List<String> error = new ArrayList<>();
        if (!getClearQuest().containsAll(questData.getReqQuest())) {
            error.add("§c前提クエスト§aを§bクリア§aしていません");
        }
        if (!questData.classCheck(playerData.getClasses().getMainClass())) {
            error.add("§c受注可能クラス§aではありません");
        }
        if (playerData.getRawLevel() < questData.getReqLevel()) {
            error.add("§eレベル§aが§a足りません");
        }
        if (playerData.getRawLevel() > questData.getMaxLevel()) {
            error.add("§eレベル§aが§a超過しています");
        }
        return error;
    }
    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "QuestID")) {
                String questId = CustomItemStack.getCustomData(clickedItem, "QuestID");
                SomQuest quest;
                if (getQuests().containsKey(questId)) {
                    quest = getQuest(questId);
                } else {
                    quest = new SomQuest(QuestDataLoader.getQuestData(questId));
                }
                if (getQuests().size() < playerData.getRank().getQuestSize()) {
                    List<String> error = new ArrayList<>();
                    if (getQuests().containsKey(quest.getId())) {
                        error.add("§aすでに§b受注§aしています");
                    }
                    error.addAll(checkQuest(quest.getQuestData()));
                    switch (quest.getQuestData().getCycle()) {
                        case One -> {
                            if (getClearQuest().contains(quest.getId())) {
                                error.add("§aこの§eクエスト§aは§e1回§aしか§bクリア§a出来ません");
                            }
                        }
                        case Daily -> {
                            if (getClearQuest().contains(quest.getId()) && getClearQuestTime(quest.getId()).isAfter(LocalDateTime.now().with(LocalTime.of(0, 0)))) {
                                error.add("§aこの§eクエスト§aは§e1日1回§aしか§bクリア§a出来ません");
                            }
                        }
                    }
                    if (!error.isEmpty()) {
                        List<String> message = new ArrayList<>();
                        error.forEach(value -> message.add(Prefix + value));
                        playerData.sendMessage(message, SomSound.Nope);
                    } else {
                        addQuest(quest);
                        if (!hasTrackingQuest()) setTrackingQuest(quest);
                        playerData.sendMessage(Prefix + quest.getDisplay() + "§aを§b受注§aしました", SomSound.Level);
                        playerData.closeInventory();
                        if (quest.getNowPhase() instanceof QuestTalk questTalk) {
                            if (questTalk.getHandler().equals(quest.getQuestData().getHandler())) {
                                questTalk.talk(playerData);
                            }
                        }
                    }
                } else {
                    playerData.sendMessage(Prefix + "§c受注上限§aです", SomSound.Nope);
                }
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        guiQuestList = null;
    }

    @Override
    public void update() {
        clear();
        int slot = 0;
        int maxSlot = 0;
        if (guiQuestList != null) {
            HashMap<Integer, CustomItemStack> itemMap = new HashMap<>();
            for (QuestData questData : guiQuestList) {
                if (questData.getOverrideSlot() >= 0) slot = questData.getOverrideSlot();
                SomQuest quest;
                if (getQuests().containsKey(questData.getId())) {
                    quest = getQuest(questData.getId());
                } else {
                    quest = new SomQuest(questData);
                }
                itemMap.put(slot, quest.viewItem(playerData).setCustomData("QuestID", questData.getId()));
                if (slot > maxSlot) maxSlot = slot;
                slot++;
            }
            int nextSize = (int) Math.ceil((maxSlot+1)/9.0);
            if (getSize() != nextSize) {
                setSize(nextSize);
                refresh();
            }
            itemMap.forEach(this::setItem);
        }
    }

    public static class QuestReceive extends GUIManager {

        private final QuestMenu questMenu;
        public QuestReceive(PlayerData playerData, QuestMenu questMenu) {
            super(playerData, "受注クエスト", 1);
            this.questMenu = questMenu;
        }

        private int breakCount = 0;
        private int breakSlot = -1;
        @Override
        public void topClick(InventoryClickEvent event) {
            int slot = event.getSlot();
            if (myQuest[slot] != null) {
                SomQuest quest = myQuest[slot];
                if (questMenu.getQuests().containsKey(quest.getId())) {
                    questMenu.setTrackingQuest(quest);
                    if (breakSlot == slot) {
                        if (breakCount < 10) {
                            breakCount++;
                            SomSound.Tick.play(playerData);
                        } else {
                            QuestPhase firstPhase = quest.getPhase().get(0);
                            if (quest.getPhaseIndex() == 0 || (quest.getPhaseIndex() == 1 && !firstPhase.hasReward())) {
                                questMenu.getQuests().remove(quest.getId());
                                playerData.sendMessage(Prefix + quest.getDisplay() + "§aを§c破棄§aしました", SomSound.Break);
                                if (questMenu.getTrackingQuest() == quest) {
                                    questMenu.setTrackingQuest(null);
                                }
                                update();
                            } else {
                                playerData.sendMessage(Prefix + "§e進行中§aの§eクエスト§aは§c破棄§a出来ません", SomSound.Cannon);
                            }
                            breakCount = 0;
                            breakSlot = -1;
                        }
                    } else breakSlot = slot;
                }
            }
        }

        @Override
        public void bottomClick(InventoryClickEvent event) {

        }

        @Override
        public void close(InventoryCloseEvent event) {
            breakCount = 0;
            breakSlot = -1;
        }

        public int row() {
            return (int) Math.ceil(playerData.getRank().getQuestSize()/9.0);
        }

        private SomQuest[] myQuest;
        @Override
        public void update() {
            if (getSize() != row()) {
                setSize(row());
                refresh();
            }
            clear();
            int slot = 0;
            myQuest = new SomQuest[playerData.getRank().getQuestSize()];
            for (SomQuest quest : questMenu.getQuests().values()) {
                myQuest[slot] = quest;
                CustomItemStack item = myQuest[slot].viewItem(playerData);
                if (quest.getPhaseIndex() == 0) {
                    item.addLore("§c※連打で破棄");
                }
                setItem(slot, item);
                slot++;
            }
        }
    }
}
