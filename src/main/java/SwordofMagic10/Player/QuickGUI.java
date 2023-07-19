package SwordofMagic10.Player;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Player.Classes.ClassType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuickGUI {
    public static void WeaponSupply(Player player) {
        Inventory weaponSupply = Bukkit.createInventory(null, 9, "武器支給");
        int slot = 0;
        for (ClassType classType : ClassType.values()) {
            weaponSupply.setItem(slot, ItemDataLoader.getItemData(classType.toString()).viewItem());
            slot++;
        }
        SomTask.sync(() -> player.openInventory(weaponSupply));
    }

    public static void onClickInventory(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (ClassType.values().length > slot) {
                if (title.equalsIgnoreCase("武器支給")) {
                    PlayerData playerData = PlayerData.get((Player) event.getWhoClicked());
                    if (playerData.getClasses().applyClassWeapon(ClassType.values()[slot])) {
                        playerData.sendMessage("§aすでに§eクラス武器§aを§e所持§aしています", SomSound.Nope);
                    }
                }
            }
        }
    }


}
