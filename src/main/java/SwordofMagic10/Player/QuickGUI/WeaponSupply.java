package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponSupply extends QuickGUI {
    private static WeaponSupply weaponSupply;
    public static void open(PlayerData playerData) {
        weaponSupply.openIns(playerData);
    }

    public WeaponSupply() {
        super(Type.WeaponSupply);
        int slot = 0;
        for (ClassType classType : ClassType.values()) {
            setItem(slot, ItemDataLoader.getItemData(classType.toString()).viewItem());
            slot++;
        }
        weaponSupply = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (ClassType.values().length > slot) {
                if (playerData.getClasses().applyClassWeapon(ClassType.values()[slot])) {
                    playerData.sendMessage("§aすでに§eクラス武器§aを§e所持§aしています", SomSound.Nope);
                }
            }
        }
    }
}
