package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.*;

public abstract class QuestPhase implements Cloneable {

    private final QuestData questData;
    private String display;
    private List<String> lore;
    private Type type;
    protected boolean flag;

    private double classExp = 0;
    private int equipmentExp = 0;
    private int mel = 0;
    private final List<SomItemStack> itemList = new ArrayList<>();

    public QuestPhase(QuestData questData) {
        this.questData = questData;
    }

    public String getId() {
        return questData.getId();
    }

    public QuestData getQuestData() {
        return questData;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getClassExp() {
        return classExp;
    }

    public void setClassExp(double classExp) {
        this.classExp = classExp;
    }

    public int getEquipmentExp() {
        return equipmentExp;
    }

    public void setEquipmentExp(int equipmentExp) {
        this.equipmentExp = equipmentExp;
    }

    public int getMel() {
        return mel;
    }

    public void setMel(int mel) {
        this.mel = mel;
    }

    public List<SomItemStack> getItemList() {
        return itemList;
    }

    public void addItem(SomItemStack stack) {
        itemList.add(stack);
    }

    public abstract List<String> sidebarLine(PlayerData playerData);

    public boolean hasReward() {
        return classExp > 0 || equipmentExp > 0 || mel > 0 || !getItemList().isEmpty();
    }

    public void giveReward(PlayerData playerData) {
        List<String> message = new ArrayList<>();
        message.add(decoText(getDisplay()));
        if (getClassExp() > 0) {
            message.add(decoLore("クラス経験値") + scale(getClassExp(), -1));
            playerData.getClasses().addExp(getClassExp());
        }
        if (getEquipmentExp() > 0) {
            message.add(decoLore("装備経験値") + getEquipmentExp());
            playerData.addEquipmentExp(getEquipmentExp(), Integer.MAX_VALUE);
        }
        if (getMel() > 0) {
            message.add(decoLore("メル") + getMel());
            playerData.addMel(getMel());
        }
        if (!getItemList().isEmpty()) {
            for (SomItemStack stack : getItemList()) {
                message.add("§7・§r" + stack.getItem().getColorDisplay() + "§ex" + stack.getAmount());
                playerData.getItemInventory().add(stack.getItem().clone(), stack.getAmount());
            }
        }
        playerData.sendMessage(message, SomSound.Tick);
    }

    public abstract boolean isProcess(PlayerData playerData);

    public SomJson toJson() {
        SomJson json = new SomJson();
        if (this instanceof QuestDungeonClear clear) {
            json.set("Count", clear.getCount());
        } else if (this instanceof QuestEnemyKill enemyKill) {
            enemyKill.getCount().forEach(json::set);
        } else if (this instanceof QuestHunting hunting) {
            hunting.getCount().forEach((entityType, count) -> json.set(entityType.toString(), count));
        }
        return json;
    }
    public QuestPhase fromJson(SomJson json) {
        switch (type) {
            case DungeonClear -> {
                QuestDungeonClear questDungeonClear = (QuestDungeonClear) clone();
                questDungeonClear.setCount(json.getInt("Count", 0));
                return questDungeonClear;
            }
            case EnemyKill -> {
                QuestEnemyKill questEnemyKill = (QuestEnemyKill) clone();
                for (String id : questEnemyKill.getQuestCount().keySet()) {
                    questEnemyKill.setCount(id, json.getInt(id, 0));
                }
                return questEnemyKill;
            }
            case Hunting -> {
                QuestHunting questHunting = (QuestHunting) clone();
                for (EntityType entityType : questHunting.getQuestCount().keySet()) {
                    questHunting.setCount(entityType, json.getInt(entityType.toString(), 0));
                }
                return questHunting;
            }
        }
        return clone();
    }

    @Override
    public QuestPhase clone() {
        try {
            return (QuestPhase) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum Type {
        PassItem,
        ShowItem,
        Location,
        DungeonClear,
        EnemyKill,
        Hunting,
        Talk,
        Special,
    }
}
