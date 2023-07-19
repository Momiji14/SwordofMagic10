package SwordofMagic10.Player.Classes;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.Function;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.SkillGroupLoader;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillData;
import SwordofMagic10.Player.Skill.SkillGroup;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Classes extends GUIManager {

    public final static int MaxLevel = 50;
    public final static int[] UnlockSkillGroupSlot = {1, 5, 15, 30};
    private final static int[] ReqExp = new int[MaxLevel];

    static {
        ReqExp[0] = 1000;
        for (int i = 1; i < ReqExp.length; i++) {
            ReqExp[i] = (int) Math.ceil(ReqExp[i-1] * 1.2);
        }
    }

    public static int getReqExp(int level) {
        if (MaxLevel <= level) {
            return Integer.MAX_VALUE;
        }
        return ReqExp[level - 1];
    }

    private final PlayerData playerData;
    private ClassType mainClass;
    private final HashMap<ClassType, Integer> level = new HashMap<>();
    private final HashMap<ClassType, Integer> exp = new HashMap<>();
    private final HashMap<ClassType, List<SkillGroup>> skillGroup = new HashMap<>();

    public Classes(PlayerData playerData) {
        super(playerData, "クラス設定", 4);
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public ClassType getMainClass() {
        return mainClass;
    }

    public void setMainClass(ClassType mainClass) {
        this.mainClass = mainClass;
    }

    public int getExp(ClassType classType) {
        return exp.getOrDefault(classType, 0);
    }

    public void setExp(ClassType classType, int exp) {
        this.exp.put(classType, exp);
    }

    public void addExp(int addExp) {
        addExp(mainClass, addExp);
    }

    public void addExp(ClassType classType, int addExp) {
        int reqExp = getReqExp(getLevel(classType));
        int addLevel = 0;
        int currentExp = getExp(classType);
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            reqExp = getReqExp(getLevel(classType) + addLevel);
        }
        if (addLevel > 0) {
            addLevel(classType, addLevel);
        }
        setExp(classType, currentExp);

        if (playerData.getSetting().isExpLog()) {
            playerData.sendMessage("§a[EXP+]" + mainClass.getColorDisplay() + " §e+" + addExp);
        }
    }

    public float getExpPercent(ClassType classType) {
        return (float) getExp(classType) / Classes.getReqExp(getLevel(classType));
    }

    public int getLevel(ClassType classType) {
        return level.getOrDefault(classType, 1);
    }

    public void setLevel(ClassType classType, int level) {
        this.level.put(classType, level);
    }

    public void addLevel(ClassType classType, int addLevel) {
        int currentLevel = getLevel(classType);
        setLevel(classType, Function.MinMax(currentLevel + addLevel, 1, MaxLevel));
        playerData.updateStatus();
    }

    public List<SkillGroup> getSkillGroup() {
        return getSkillGroup(getMainClass());
    }

    public List<SkillGroup> getSkillGroup(ClassType classType) {
        if (!skillGroup.containsKey(classType)) {
            skillGroup.put(classType, new ArrayList<>());
            skillGroup.get(classType).add(classType.getDefaultSkillGroup());
        }
        return skillGroup.get(classType);
    }

    public List<SomSkill> getSkillList() {
        return getSkillList(getMainClass());
    }

    public List<SomSkill> getSkillList(ClassType classType) {
        List<SomSkill> list = new ArrayList<>();
        for (SkillGroup group : getSkillGroup(classType)) {
            for (SkillData skillData : group.getList()) {
                SomSkill skill = playerData.getSkillManager().getSkill(skillData.getId());
                list.add(skill);
            }
        }
        return list;
    }

    public boolean hasSkill(SomSkill skill) {
        return hasSkill(getMainClass(), skill);
    }

    public boolean hasSkill(ClassType classType, SomSkill skill) {
        return getSkillList(classType).contains(skill);
    }

    public int activeSkillGroupSlot(ClassType classType) {
        int i = 0;
        for (int level : UnlockSkillGroupSlot) {
            if (getLevel(classType) >= level) {
                i++;
            } else break;
        }
        return i;
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "ClassSelect")) {
                ClassType classType = ClassType.valueOf(CustomItemStack.getCustomData(clickedItem, "ClassSelect"));
                if (classType != mainClass) {
                    playerData.unEquip(EquipSlot.MainHand);
                    playerData.sendTitle("§e§nClass Changed !", (mainClass != null ? mainClass.getColor() + mainClass.getDisplay() + " §a-> ": "") + classType.getColor() + classType.getDisplay(), 20, 50, 20);
                    setMainClass(classType);
                    applyClassWeapon(classType);
                    SomSound.Level.play(playerData);
                    playerData.closeInventory();
                } else {
                    playerData.sendMessage("§a現在の§eクラス§aです", SomSound.Nope);
                }
            }
            if (CustomItemStack.hasCustomData(clickedItem, "EditSkillGroup")) {
                editSkillGroup = ClassType.valueOf(CustomItemStack.getCustomData(clickedItem, "EditSkillGroup"));
                update();
            }
            if (CustomItemStack.hasCustomData(clickedItem, "SkillGroup")) {
                SkillGroup group = SkillGroupLoader.getSkillGroup(CustomItemStack.getCustomData(clickedItem, "SkillGroup"));
                int activeSlot = activeSkillGroupSlot(editSkillGroup);
                int groupSize = getSkillGroup(editSkillGroup).size();
                if (getSkillGroup(editSkillGroup).contains(group)) {
                    if (group != editSkillGroup.getDefaultSkillGroup()) {
                        getSkillGroup(editSkillGroup).remove(group);
                        playerData.sendMessage("§b" + group.getDisplay() + "§aを外しました §e[" + (groupSize-1) + "/"+ activeSlot + "]", SomSound.Tick);
                    } else {
                        playerData.sendMessage("§e標準スキルグループ§aは外せません", SomSound.Nope);
                    }
                } else {
                    if (activeSlot > groupSize) {
                        getSkillGroup(editSkillGroup).add(group);
                        playerData.sendMessage("§b" + group.getDisplay() + "§aを§bセット§aしました §e[" + (groupSize+1) + "/"+ activeSlot + "]", SomSound.Tick);
                    } else {
                        playerData.sendMessage("§eスキルグループスロット§aに§c空§aがありません §c[" + groupSize + "/"+ activeSlot + "]", SomSound.Nope);
                    }
                }
                update();
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        editSkillGroup = null;
    }

    private ClassType editSkillGroup = null;
    @Override
    public void update() {
        clear();
        int slot = 0;
        if (editSkillGroup != null) {
            for (SkillGroup group : editSkillGroup.getSkillGroups()) {
                if (editSkillGroup.getDefaultSkillGroup() != group) {
                    CustomItemStack item = group.viewItem().setCustomData("SkillGroup", group.getId());
                    if (getSkillGroup(editSkillGroup).contains(group)) item.setGlowing();
                    setItem(slot, item);
                    slot++;
                }
            }
        } else {
            for (ClassType classType : ClassType.values()) {
                CustomItemStack item = new CustomItemStack(classType.getIcon());
                item.setDisplay(classType.getDisplay());
                item.addLore(classType.getLore());
                if (classType == getMainClass()) item.setGlowing();
                setItem(slot, item.setCustomData("ClassSelect", classType.toString()));
                if (getMainClass() != null) {
                    setItem(slot + 1, Config.FlameItem);
                    setItem(slot + 8, new CustomItemStack(Material.CRAFTING_TABLE).setNonDecoDisplay("§eスキルグループ変更").setCustomData("EditSkillGroup", classType.toString()));
                    int use = 0;
                    for (SkillGroup group : getSkillGroup(classType)) {
                        setItem(slot + use + 2, group.viewItem());
                        use++;
                    }
                    for (int i = use; i < UnlockSkillGroupSlot.length; i++) {
                        setItem(slot + i + 2, new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c未使用スロット [Lv" + UnlockSkillGroupSlot[i] + "]"));
                    }
                }
                slot += 9;
            }
        }
    }

    public boolean applyClassWeapon(ClassType classType) {
        SomEquipment weapon = (SomEquipment) ItemDataLoader.getItemData(classType.toString());
        List<SomItemStack> list = new ArrayList<>(playerData.getItemInventory().getInventory());
        list.addAll(playerData.getItemStorage().getInventory());
        for (SomItemStack stack : list) {
            if (stack.getItem() instanceof SomEquipment equipment) {
                if (equipment.getEquipmentCategory() == weapon.getEquipmentCategory()) {
                    return true;
                }
            }
        }
        playerData.getItemInventory().add(weapon, 1);
        playerData.sendMessage("§eクラス武器§aが§b支給§aされました", SomSound.Level);
        return false;
    }
}
