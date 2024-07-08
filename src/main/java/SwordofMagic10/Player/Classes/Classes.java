package SwordofMagic10.Player.Classes;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.SkillGroupLoader;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomPotion;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.Menu.PalletMenu;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillData;
import SwordofMagic10.Player.Skill.SkillGroup;
import SwordofMagic10.Player.Skill.SomSkill;
import com.github.jasync.sql.db.RowData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.scale;
import static SwordofMagic10.SomCore.Log;

public class Classes extends GUIManager {

    public final static int MaxLevel = 110;
    public final static int[] UnlockSkillGroupSlot = {1, 5, 15, 30};
    private final static double[] ReqExp = new double[MaxLevel];
    private final static double[] Exp = new double[MaxLevel];

    static {
        ReqExp[0] = 1000;
        for (int i = 1; i < ReqExp.length; i++) {
            double multiply = 1.15;
            if (i >= 50) multiply = 1.05;
            if (i == 98) multiply = 10;
            if (i > 98) multiply = 2;
            ReqExp[i] = ReqExp[i-1] * multiply;
        }

        Exp[0] = 1000;
        for (int i = 1; i < Exp.length; i++) {
            double multiply = 1.15;
            if (i >= 50) multiply = 1.05;
            Exp[i] = Exp[i-1] * multiply;
        }
    }

    public static double getReqExp(int level) {
        if (level < 1) level = 1;
        if (MaxLevel <= level) return 0;
        return ReqExp[level - 1];
    }

    public static double getExp(int level) {
        if (level < 1) level = 1;
        if (MaxLevel <= level) return 0;
        return Exp[level - 1] / (5+Math.pow(level, 1.1));
    }

    private final PlayerData playerData;
    private ClassType mainClass;
    private final HashMap<ClassType, Integer> level = new HashMap<>();
    private final HashMap<ClassType, Double> exp = new HashMap<>();
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

    public boolean isValidClass() {
        return mainClass != null;
    }

    public boolean isNullClass() {
        return mainClass == null;
    }

    public double getExp(ClassType classType) {
        return exp.getOrDefault(classType, 0.0);
    }

    public void setExp(ClassType classType, double exp) {
        this.exp.put(classType, exp);
    }

    public void addExp(double addExp) {
        addExp(mainClass, addExp);
    }

    public void addExp(ClassType classType, double addExp) {
        if (getLevel(classType) >= MaxLevel) {
            setExp(classType, 0);
            return;
        }
        double reqExp = getReqExp(getLevel(classType));
        int addLevel = 0;
        double currentExp = getExp(classType);
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            int nextLevel = getLevel(classType) + addLevel;
            if (MaxLevel > nextLevel) {
                reqExp = getReqExp(nextLevel);
            } else {
                reqExp = Double.MAX_VALUE;
            }
        }
        if (addLevel > 0) {
            addLevel(classType, addLevel);
        }
        setExp(classType, currentExp);

        if (playerData.getSetting().isExpLog()) {
            playerData.sendMessage("§a[ExpLog]" + mainClass.getColorDisplay() + " §e+" + scale(addExp, 2));
        }
    }

    public float getExpPercent(ClassType classType) {
        if (getLevel(classType) >= MaxLevel) return 0f;
        return (float) (getExp(classType) / Classes.getReqExp(getLevel(classType)));
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
        playerData.sendMessage(classType.getColorDisplay() + "§aが§eLv" + getLevel(classType) + "§aに上がりました", SomSound.Level);
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
                    playerData.updateStatus();
                } else {
                    playerData.sendMessage("§a現在の§eクラス§aです", SomSound.Nope);
                }
            }
            if (mainClass != null) {
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
                            playerData.sendMessage("§b" + group.getDisplay() + "§aを外しました §e[" + (groupSize - 1) + "/" + activeSlot + "]", SomSound.Tick);
                        } else {
                            playerData.sendMessage("§e標準スキルグループ§aは外せません", SomSound.Nope);
                        }
                    } else {
                        if (activeSlot > groupSize) {
                            getSkillGroup(editSkillGroup).add(group);
                            playerData.sendMessage("§b" + group.getDisplay() + "§aを§bセット§aしました §e[" + (groupSize + 1) + "/" + activeSlot + "]", SomSound.Tick);
                        } else {
                            playerData.sendMessage("§eスキルグループスロット§aに§c空§aがありません §c[" + groupSize + "/" + activeSlot + "]", SomSound.Nope);
                        }
                    }
                    update();
                }
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        playerData.clearEffectNonPotion();
        editSkillGroup = null;
    }

    private ClassType editSkillGroup = null;
    @Override
    public void update() {
        clear();
        int slot = 0;
        if (editSkillGroup != null) {
            for (SkillGroup group : editSkillGroup.getSkillGroups()) {
                if (editSkillGroup.getDefaultSkillGroup() != group && !group.isHide()) {
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
                if (isValidClass()) {
                    setItem(slot + 1, Config.FlameItem);
                    setItem(slot + 8, new CustomItemStack(Material.CRAFTING_TABLE).setNonDecoDisplay("§eスキルグループ変更").setCustomData("EditSkillGroup", classType.toString()));
                    int use = 0;
                    for (SkillGroup group : getSkillGroup(classType)) {
                        setItem(slot + use + 2, group.viewItem());
                        use++;
                    }
                    for (int i = use; i < UnlockSkillGroupSlot.length; i++) {
                        CustomItemStack slotView;
                        if (UnlockSkillGroupSlot[i] > getLevel(classType)) {
                            slotView = new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c未開放スロット [Lv" + UnlockSkillGroupSlot[i] + "]");
                        } else {
                            slotView = new CustomItemStack(Material.NETHER_STAR).setNonDecoDisplay("§7未使用スロット [Lv" + UnlockSkillGroupSlot[i] + "]");
                        }
                        setItem(slot + i + 2, slotView.setCustomData("EditSkillGroup", classType.toString()));
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

    public static final String Table = "PlayerClasses";
    private static final String[] priKey = new String[]{"UUID", "Class"};
    public void save() {
        for (ClassType classType : ClassType.values()) {
            String[] priValue = new String[]{playerData.getUUIDAsString(), classType.toString()};
            SomSQL.setSql(Classes.Table, priKey, priValue, "Username", playerData.getUsername());
            SomSQL.setSql(Classes.Table, priKey, priValue, "Level", getLevel(classType));
            SomSQL.setSql(Classes.Table, priKey, priValue, "Exp", getExp(classType));

            SomJson skillGroup = new SomJson();
            for (int i = 1; i < getSkillGroup(classType).size(); i++) {
                skillGroup.set("SkillGroup.Slot-" + i, getSkillGroup(classType).get(i).getId());
            }
            SomSQL.setSql(Classes.Table, priKey, priValue, "SkillGroup", skillGroup.toString());

            SomJson pallet = new SomJson();
            PalletMenu palletMenu = playerData.getPalletMenu();
            for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
                SomSkill skill = palletMenu.getPallet(classType)[i];
                if (skill != null) pallet.set("Skill-" + i, skill.getId());
            }
            for (int i = 0; i < palletMenu.getItemPallet().length; i++) {
                SomItem item = palletMenu.getItemPallet()[i];
                if (item != null) pallet.set("Item-" + i, item.toJson());
            }
            SomSQL.setSql(Classes.Table, priKey, priValue, "Pallet", pallet.toString());
        }
    }

    public void load() {
        for (ClassType classType : ClassType.values()) {
            String[] priValue = new String[]{playerData.getUUIDAsString(), classType.toString()};
            setLevel(classType, SomSQL.getInt(Classes.Table, priKey, priValue, "Level"));
            setExp(classType, SomSQL.getDouble(Classes.Table, priKey, priValue, "Exp"));

            SomJson skillGroup = new SomJson(SomSQL.getString(Classes.Table, priKey, priValue, "SkillGroup"));
            SkillGroup first = getSkillGroup(classType).get(0);
            getSkillGroup(classType).clear();
            getSkillGroup(classType).add(first);
            for (int i = 1; i < Classes.UnlockSkillGroupSlot.length; i++) {
                try {
                    String key = "SkillGroup.Slot-" + i;
                    if (skillGroup.has(key)) {
                        getSkillGroup(classType).add(SkillGroupLoader.getSkillGroup(skillGroup.getString(key)));
                    }
                } catch (Exception ignore) {}
            }

            try {
                SomJson pallet = new SomJson(SomSQL.getString(Classes.Table, priKey, priValue, "Pallet"));
                PalletMenu palletMenu = playerData.getPalletMenu();
                for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
                    try {
                        String key = "Skill-" + i;
                        if (pallet.has(key)) {
                            palletMenu.setPallet(classType, i, playerData.getSkillManager().getSkill(pallet.getString(key)));
                        }
                    } catch (Exception ignore) {}
                }
                for (int i = 0; i < palletMenu.getItemPallet().length; i++) {
                    try {
                        String key = "Item-" + i;
                        if (pallet.has(key)) {
                            palletMenu.setItemPallet(i, (SomPotion) SomItem.fromJson(pallet.getSomJson(key)));
                        }
                    } catch (Exception ignore) {}
                }
            } catch (Exception ignore) {}
        }
    }
}
