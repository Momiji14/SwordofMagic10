package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Player.Quest.QuestMenu.Prefix;

public class SomQuest {
    private final QuestData questData;
    private int phaseIndex = 0;
    private final List<QuestPhase> phase = new ArrayList<>();
    private boolean clearFlag = false;

    public SomQuest(QuestData questData) {
        this.questData = questData;
        for (QuestPhase phase : questData.getPhase()) {
            this.phase.add(phase.clone());
        }
    }

    public String getId() {
        return questData.getId();
    }

    public String getDisplay() {
        return questData.getDisplay();
    }

    public QuestData getQuestData() {
        return questData;
    }

    public boolean nextCheck() {
        return phase.size() > phaseIndex;
    }

    public void nextPhase(PlayerData playerData) {
        getNowPhase().giveReward(playerData);
        phaseIndex++;
        if (nextCheck()) {
            playerData.sendTitle("§eQuest Progress !", getDisplay(), 10, 40, 10);
            playerData.sendMessage(Prefix + getDisplay() + "§aが§b進行§aしました", SomSound.Level);
            QuestPhase questPhase = getNowPhase();
            SomTask.delay(() -> {
                playerData.sendTitle(questPhase.getDisplay(), questPhase.getLore().get(0), 10, 40, 10);
                playerData.sendMessage(Prefix + getDisplay() + " - " + questPhase.getDisplay() + "§aを§b開始§aしました", SomSound.Level);
            }, 60);
        } else clearQuest(playerData);
    }

    public void clearQuest(PlayerData playerData) {
        playerData.sendTitle("§eQuest Clear !", getDisplay(), 10, 40, 10);
        playerData.sendMessage(Prefix + getDisplay() + "§aを§bクリア§aしました", SomSound.Level);
        playerData.getQuestMenu().addClearQuest(getId());
        setClearFlag(true);
    }

    public void setPhase(int index, QuestPhase phase) {
        this.phase.set(index, phase);
    }

    public int getPhaseIndex() {
        return phaseIndex;
    }

    public QuestPhase getNowPhase() {
        return phase.get(phaseIndex);
    }

    public void setPhaseIndex(int phaseIndex) {
        this.phaseIndex = phaseIndex;
    }

    public boolean isClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(boolean clearFlag) {
        this.clearFlag = clearFlag;
    }

    public CustomItemStack viewItem(PlayerData playerData) {
        Material icon = Material.PAPER;
        if (!playerData.getQuestMenu().getClearQuest().containsAll(questData.getReqQuest()) || playerData.getLevel() < getQuestData().getReqLevel()) {
            icon = Material.BARRIER;
        }
        if (playerData.getQuestMenu().getClearQuest().contains(getId())) {
            icon = Material.EMERALD;
        }
        CustomItemStack item = new CustomItemStack(icon);
        if (playerData.getQuestMenu().getQuests().containsKey(getId())) {
            item.setIcon(Material.MAP);
        }
        item.setDisplay(getDisplay());
        item.addLore(questData.getLore());
        item.addLore(phaseLore());
        return item;
    }

    public List<String> phaseLore() {
        QuestPhase questPhase = getNowPhase();
        List<String> list = new ArrayList<>();
        list.add(decoSeparator(questPhase.getDisplay()));
        list.addAll(questPhase.getLore());
        list.add(decoSeparator("目標"));
        if (questPhase instanceof QuestTalk questTalk) {
            list.add(decoLore("会話NPC") + questTalk.getHandler());
        } else if (questPhase instanceof QuestEnemyKill questEnemyKill) {
            for (QuestEnemyKill.QuestEnemyKillContainer container : questEnemyKill.getQuestCount().values()) {
                String display = container.id().equals("All") ? "すべてのエネミー" : container.id();
                String levelText = "Lv" + (container.maxLevel() == Integer.MAX_VALUE ? container.minLevel() + "以上" : container.minLevel() + "~" + container.maxLevel());
                list.add(decoLore("§c" + display + "§e" + levelText) + questEnemyKill.getCount().getOrDefault(container.id(), 0) + "/" + container.count());
            }
        } else if (questPhase instanceof QuestLocation questLocation) {
            Location loc = questLocation.getLocation();
            list.add(decoLore("座標") + scale(loc.getX()) + "," + scale(loc.getY()) + "," + scale(loc.getZ()));
        } else if (questPhase instanceof QuestPassItem questPassItem) {
            list.add(decoLore("対象") + questPassItem.getHandler());
            for (SomItemStack stack : questPassItem.getReqItem()) {
                list.add("§7・§e" + stack.getItem().getDisplay() + "§ax" + stack.getAmount());
            }
        } else if (questPhase instanceof QuestDungeonClear questDungeonClear) {
            DungeonDifficulty difficulty = questDungeonClear.getDifficulty();
            list.add(decoLore("ダンジョン") + questDungeonClear.getDungeonID());
            list.add(decoLore("難易度") + (difficulty == null ? "All" : difficulty.toString()));
            list.add(decoLore("クリア回数") + questDungeonClear.getCount() + "/" + questDungeonClear.getReqCount());
        }
        list.add(decoSeparator("受注条件"));
        list.add("§7・§cクラスレベル" + questData.getReqLevel());
        for (String quest : getQuestData().getReqQuest()) {
            list.add("§7・§c" + quest);
        }
        List<String> reward = new ArrayList<>();
        list.add(decoSeparator("現在の報酬"));
        if (questPhase.getClassExp() > 0) reward.add(decoLore("クラス経験値") + questPhase.getClassExp());
        if (questPhase.getEquipmentExp() > 0) reward.add(decoLore("装備経験値") + questPhase.getEquipmentExp());
        if (questPhase.getMel() > 0) reward.add(decoLore("メル") + questPhase.getMel());
        if (questPhase.getItemList().size() > 0) {
            for (SomItemStack stack : questPhase.getItemList()) {
                reward.add("§7・§f" + stack.getItem().getColorDisplay() + "§ax" + stack.getAmount());
            }
        }
        if (reward.size() > 0) {
            list.addAll(reward);
        } else {
            list.add("§7・§c報酬なし");
        }
        return list;
    }
}
