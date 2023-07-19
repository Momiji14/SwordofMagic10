package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestMenu extends GUIManager {
    public static final String Prefix = "§6[Quest]§r";
    private final HashMap<String, SomQuest> quest = new HashMap<>();
    private final Set<String> clearQuest = new HashSet<>();
    private int talkSpeed = 10;
    private BukkitTask task;
    public QuestMenu(PlayerData playerData) {
        super(playerData, "クエスト", 1);
        task = SomTask.timer(() -> {
            if (playerData.getPlayer().isOnline()) {
                for (SomQuest quest : quest.values()) {
                    if (playerData.getLevel() >= quest.getQuestData().getReqLevel()) {
                        if (quest.nextCheck()) {
                            if (quest.getNowPhase().isProcess(playerData)) {
                                quest.nextPhase(playerData);
                            }
                        } else {
                            quest.clearQuest(playerData);
                        }
                    }
                }
                quest.values().removeIf(SomQuest::isClearFlag);
            } else {
                task.cancel();
            }
        }, 20, 50);
    }

    public Set<String> getClearQuest() {
        return clearQuest;
    }

    public void addClearQuest(String questID) {
        clearQuest.add(questID);
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

    private int breakCount = 0;
    private int breakSlot = -1;
    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (inventory[slot] != null) {
            SomQuest quest = inventory[slot];
            if (getQuests().containsKey(quest.getId())) {
                if (breakSlot == slot) {
                    if (breakCount < 10) {
                        breakCount++;
                        SomSound.Tick.play(playerData);
                    } else {
                        if (quest.getPhaseIndex() == 0) {
                            getQuests().remove(quest.getId());
                            playerData.sendMessage(Prefix + quest.getDisplay() + "§aを§c破棄§aしました", SomSound.Break);
                            update();
                        } else {
                            playerData.sendMessage(Prefix + "§e進行中§aの§eクエスト§aは§c破棄§a出来ません", SomSound.Cannon);
                        }
                        breakCount = 0;
                        breakSlot = -1;
                    }
                } else breakSlot = slot;
            } else if (getQuests().size() < 9) {
                if (getClearQuest().contains(quest.getQuestData().getId()) && quest.getQuestData().getCycle() == QuestData.Cycle.One) {
                    playerData.sendMessage(Prefix + "§aこの§eクエスト§aは§e1回§aしか§bクリア§a出来ません", SomSound.Nope);
                } else if (getClearQuest().containsAll(quest.getQuestData().getReqQuest())) {
                    if (playerData.getLevel() >= quest.getQuestData().getReqLevel()) {
                        addQuest(quest);
                        playerData.sendMessage(Prefix + quest.getDisplay() + "§aを§b受注§aしました", SomSound.Level);
                        playerData.closeInventory();
                        if (quest.getNowPhase() instanceof QuestTalk questTalk) {
                            if (questTalk.getHandler().equals(quest.getQuestData().getHandler())) {
                                questTalk.talk(playerData);
                            }
                        }
                    } else {
                        playerData.sendMessage(Prefix + "§eレベル§aが§a足りません", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessage(Prefix + "§c前提クエスト§aを§bクリア§aしていません", SomSound.Nope);
                }
            } else {
                playerData.sendMessage(Prefix + "§c受注上限§aです", SomSound.Nope);
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        guiQuestList = null;
        breakCount = 0;
        breakSlot = -1;
    }

    private SomQuest[] inventory = new SomQuest[9];
    @Override
    public void update() {
        clear();
        int slot = 0;
        inventory = new SomQuest[9];
        if (guiQuestList != null) {
            for (QuestData questData : guiQuestList) {
                if (getQuests().containsKey(questData.getId())) {
                    inventory[slot] = getQuest(questData.getId());
                } else {
                    inventory[slot] = new SomQuest(questData);
                }
                setItem(slot, inventory[slot].viewItem(playerData));
                slot++;
            }
        } else {
            for (SomQuest quest : getQuests().values()) {
                inventory[slot] = quest;
                CustomItemStack item = inventory[slot].viewItem(playerData);
                if (quest.getPhaseIndex() == 0) {
                    item.addLore("§c※連打で破棄");
                }
                setItem(slot, item);
                slot++;
            }
        }
    }
}
