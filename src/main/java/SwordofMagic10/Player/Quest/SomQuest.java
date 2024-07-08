package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

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
        return phase.size() > phaseIndex+1;
    }

    private Runnable runnable;
    private BukkitTask task;
    public void nextPhase(PlayerData playerData) {
        getNowPhase().giveReward(playerData);
        if (nextCheck()) {
            phaseIndex++;
            playerData.sendTitle("§eQuest Progress !", getDisplay(), 5, 20, 5);
            playerData.sendMessage(Prefix + getDisplay() + "§aが§b進行§aしました", SomSound.Level);
            QuestPhase questPhase = getNowPhase();
            if (task != null) {
                runnable.run();
                task.cancel();
                task = null;
            }
            runnable = () -> {
                playerData.sendTitle(questPhase.getDisplay(), questPhase.getLore().get(0), 5, 20, 5);
                playerData.sendMessage(Prefix + getDisplay() + " - " + questPhase.getDisplay() + "§aを§b開始§aしました", SomSound.Level);
            };
            task = SomTask.delay(runnable, 30);
        } else clearQuest(playerData);
    }

    public void clearQuest(PlayerData playerData) {
        playerData.sendTitle("§eQuest Clear !", getDisplay(), 10, 40, 10);
        playerData.sendMessage(Prefix + getDisplay() + "§aを§bクリア§aしました", SomSound.Level);
        playerData.getStatistics().add(Statistics.Type.QuestClear, 1);
        if (getQuestData().getCycle() == QuestData.Cycle.Daily) {
            playerData.getStatistics().add(Statistics.Type.DayQuestClear, 1);
        }
        playerData.getQuestMenu().addClearQuest(getId());
        setClearFlag(true);
        playerData.closeInventory();
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

    public List<QuestPhase> getPhase() {
        return phase;
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

        int mel = 0;
        double classExp = 0;
        int equipmentExp = 0;
        List<SomItemStack> rewardItem = new ArrayList<>();
        for (QuestPhase questPhase : questData.getPhase()) {
            mel += questPhase.getMel();
            classExp += questPhase.getClassExp();
            equipmentExp += questPhase.getEquipmentExp();
            rewardItem.addAll(questPhase.getItemList());
        }
        item.addSeparator("全フェーズの合計報酬");
        if (classExp > 0) item.addLore(decoLore("クラス経験値") + scale(classExp));
        if (equipmentExp > 0) item.addLore(decoLore("装備精錬値") + equipmentExp);
        if (mel > 0) item.addLore(decoLore("メル") + mel);
        for (SomItemStack stack : rewardItem) {
            item.addLore("§7・§f" + stack.getItem().getColorDisplay() + "T" + stack.getItem().getTier() + "§ex" + stack.getAmount());
        }
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
                StringBuilder display = new StringBuilder();
                if (container.mapData() != null) {
                    display.append("§e[").append(container.mapData().getDisplay()).append("] ");
                }
                if (container.difficulty() != null) {
                    display.append("§e[").append(container.difficulty()).append("] ");
                }
                display.append("§c").append(container.getDisplay());
                if (container.minLevel() != 0) {
                    display.append(" §eLv").append(container.maxLevel() == Integer.MAX_VALUE ? container.minLevel() + "↑" : container.minLevel() + "~" + container.maxLevel());
                }
                list.add("§7・" + display + " " + questEnemyKill.getCount().getOrDefault(container.id(), 0) + "/" + container.count());
            }
        } else if (questPhase instanceof QuestHunting questHunting) {
            questHunting.getQuestCount().forEach((entityType, count) -> list.add("§7・§c" + entityType.toString() + " " + questHunting.getCount().getOrDefault(entityType, 0) + "/" + count));
        } else if (questPhase instanceof QuestLocation questLocation) {
            list.add(decoLore("座標") + questLocation.getLocationDisplay());
        } else if (questPhase instanceof QuestPassItem questPassItem) {
            list.add(decoLore("対象") + questPassItem.getHandler());
            for (SomItemStack stack : questPassItem.getReqItem()) {
                list.add("§7・" + stack.getItem().getColorTierDisplay() + "§ex" + stack.getAmount());
            }
        } else if (questPhase instanceof QuestShowItem questShowItem) {
            list.add(decoLore("対象") + questShowItem.getHandler());
            for (SomItemStack stack : questShowItem.getReqItem()) {
                list.add("§7・" + stack.getItem().getColorTierDisplay() + "§ex" + stack.getAmount());
            }
        } else if (questPhase instanceof QuestDungeonClear questDungeonClear) {
            list.add(decoLore("ダンジョン") + questDungeonClear.getDungeonDisplay());
            list.add(decoLore("難易度") + questDungeonClear.getDifficultyDisplay());
            list.add(decoLore("クリア回数") + questDungeonClear.getCount() + "/" + questDungeonClear.getReqCount());
        }
        list.add(decoSeparator("受注条件"));

        if (questData.getMaxLevel() == Classes.MaxLevel) {
            list.add("§7・§cクラスレベル" + questData.getReqLevel());
        } else {
            list.add("§7・§cクラスレベル" + questData.getReqLevel() + "~" + questData.getMaxLevel());
        }
        if (!questData.getReqClass().isEmpty()) {
            for (ClassType reqClass : questData.getReqClass()) {
                list.add("§7・" + reqClass.getColorDisplay());
            }
        }
        for (String quest : getQuestData().getReqQuest()) {
            list.add("§7・§c" + quest);
        }
        list.add("§7・§c" + questData.getCycle().getDisplay());
        List<String> reward = new ArrayList<>();
        list.add(decoSeparator("現在フェーズの報酬"));
        if (questPhase.getClassExp() > 0) reward.add(decoLore("クラス経験値") + scale(questPhase.getClassExp()));
        if (questPhase.getEquipmentExp() > 0) reward.add(decoLore("装備精錬値") + questPhase.getEquipmentExp());
        if (questPhase.getMel() > 0) reward.add(decoLore("メル") + questPhase.getMel());
        if (!questPhase.getItemList().isEmpty()) {
            for (SomItemStack stack : questPhase.getItemList()) {
                reward.add("§7・§f" + stack.getItem().getColorDisplay() + "T" + stack.getItem().getTier() + "§ex" + stack.getAmount());
            }
        }
        if (!reward.isEmpty()) {
            list.addAll(reward);
        } else {
            list.add("§7・§c報酬なし");
        }
        return list;
    }
}
