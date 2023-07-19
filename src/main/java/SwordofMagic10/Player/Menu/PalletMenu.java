package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PalletMenu extends GUIManager {

    private final HashMap<ClassType, SomSkill[]> skillPallet = new HashMap<>() {{
        for (ClassType classType : ClassType.values()) {
            put(classType, new SomSkill[12]);
        }
    }};

    private final SomItem[] item = new SomItem[2];

    public PalletMenu(PlayerData playerData) {
        super(playerData, "パレット", 6);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (Config.UpScrollIcon.proximate(clickedItem)) {
                scroll = Math.max(0, scroll - 1);
            } else if (Config.UpScrollIcon.proximate(clickedItem)) {
                scroll = Math.min((int) Math.floor(cache.size()/8f), scroll + 1);
            }
            if (CustomItemStack.hasCustomData(clickedItem, "SomSkill")) {
                SomSkill selectSkill = playerData.getSkillManager().getSkill(CustomItemStack.getCustomData(clickedItem, "SomSkill"));
                int i = 0;
                for (SomSkill skill : getPallet()) {
                    if (skill == null) {
                        setPallet(i, selectSkill);
                        playerData.sendMessage("§eスロット[" + (i+1) + "]§aに§b" + selectSkill.getDisplay() + "§aを§eセット§aしました", SomSound.Tick);
                        update();
                        return;
                    }
                    i++;
                }
                playerData.sendMessage("§e未設定スロット§aがありません", SomSound.Nope);
            }
            if (CustomItemStack.hasCustomData(clickedItem, "UnPallet")) {
                setPallet(CustomItemStack.getCustomDataInt(clickedItem, "UnPallet"), null);
            }
            update();
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot < getPallet().length/2 && getPallet()[slot] != null) {
            setPallet(slot, null);
            SomSound.Tick.play(playerData);
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    private final HashMap<Integer, SomSkill> cache = new HashMap<>();
    private int scroll = 0;
    @Override
    public void update() {
        ItemStack[] contents = new ItemStack[54];
        cache.clear();
        contents[8] = Config.UpScrollIcon;
        contents[17] = Config.FlameItem;
        contents[26] = Config.DownScrollIcon;
        int slot = 0;
        ClassType mainClass = playerData.getClasses().getMainClass();
        if (mainClass != null) {
            for (SomSkill skill : playerData.getClasses().getSkillList()) {
                contents[slot] = skill.viewItem(playerData.getLevel()).setCustomData("SomSkill", skill.getId());
                slot++;
                if (Math.floorMod(slot, 9) == 8) slot++;
            }
        }

        slot = 45;
        for (int i = 0; i < getPallet().length; i++) {
            contents[slot] = getPallet()[i] != null ? getPallet()[i].viewItem(playerData.getLevel()).setCustomData("UnPallet", i) : Config.NonePallet(i);
            slot++;
            if (i == 5) slot = 36;
        }

        slot = 51;
        for (int i = 0; i < item.length; i++) {
            contents[slot] = item[i] != null ? item[i].viewItem() : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§7アイテムスロット[" + i + "]");
            slot -= 9;
        }
        setContents(contents);
    }

    public SomSkill[] getPallet() {
        return getPallet(playerData.getClasses().getMainClass());
    }

    public SomSkill[] getPallet(ClassType classType) {
        return skillPallet.get(classType);
    }

    public void setPallet(int slot, SomSkill skill) {
        setPallet(playerData.getClasses().getMainClass(), slot, skill);
    }

    public void setPallet(ClassType classType, int slot, SomSkill skill) {
        skillPallet.get(classType)[slot] = skill;
    }

    public SomItem[] getItemPallet() {
        return item;
    }

    public void setItemPallet(int i, SomItem item) {
        this.item[i] = item;
    }
}
