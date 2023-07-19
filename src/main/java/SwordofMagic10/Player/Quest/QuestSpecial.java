package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;

public class QuestSpecial extends QuestPhase {

    private SpecialID specialID;
    public QuestSpecial(QuestData questData) {
        super(questData);
    }

    public SpecialID getSpecialID() {
        return specialID;
    }

    public void setSpecialID(SpecialID specialID) {
        this.specialID = specialID;
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        switch (specialID) {
            case Tutorial_ArmorBuy -> {
                for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                    if (stack.getItem() instanceof SomEquipment equipment) {
                        switch (equipment.getEquipmentCategory()) {
                            case Helmet, Chest, Legs, Boots -> {
                                return true;
                            }
                        }
                    }
                }
            }
            case Tutorial_ArmorEquip -> {
                for (EquipSlot equipSlot : new EquipSlot[]{EquipSlot.Helmet, EquipSlot.Chest, EquipSlot.Legs, EquipSlot.Boots}) {
                    if (playerData.getEquipment(equipSlot) != null) {
                        return true;
                    }
                }
            }
            case Tutorial_Upgrade -> {
                for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                    if (stack.getItem() instanceof SomEquipment equipment && equipment.getPlus() >= 1) {
                        return true;
                    }
                }
            }
            case Tutorial_SkillGroup -> {
                return playerData.getClasses().getSkillGroup().size() > 1;
            }
        }
        return false;
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

    public enum SpecialID {
        Tutorial_ArmorBuy,
        Tutorial_ArmorEquip,
        Tutorial_Upgrade,
        Tutorial_SkillGroup,
    }
}
