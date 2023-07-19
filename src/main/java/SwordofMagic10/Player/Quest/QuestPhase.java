package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.Component.Function.decoText;

public abstract class QuestPhase implements Cloneable {

    private final QuestData questData;
    private String display;
    private List<String> lore;
    private Type type;
    protected boolean flag;

    private int classExp = 0;
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

    public int getClassExp() {
        return classExp;
    }

    public void setClassExp(int classExp) {
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

    public void giveReward(PlayerData playerData) {
        List<String> message = new ArrayList<>();
        message.add(decoText(getDisplay()));
        if (getClassExp() > 0) {
            message.add(decoLore("クラス経験値") + getClassExp());
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
        if (getItemList().size() > 0) {
            for (SomItemStack stack : getItemList()) {
                message.add("§7・§r" + stack.getItem().getColorDisplay() + "§ax" + stack.getAmount());
                playerData.getItemInventory().add(stack);
            }
        }
        playerData.sendMessage(message, SomSound.Tick);
    }

    public abstract boolean isProcess(PlayerData playerData);

    public abstract SomJson toJson();
    public abstract QuestPhase fromJson(SomJson json);

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
        Location,
        DungeonClear,
        EnemyKill,
        Talk,
        Special,
    }
}
