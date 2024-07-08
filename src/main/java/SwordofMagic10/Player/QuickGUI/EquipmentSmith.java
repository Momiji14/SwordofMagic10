package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.DungeonDataLoader;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.DataBase.ShopDataLoader;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.ShopData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EquipmentSmith extends QuickGUI {
    private static EquipmentSmith equipmentSmith;
    public static void open(PlayerData playerData) {
        equipmentSmith.openIns(playerData);
    }

    public EquipmentSmith() {
        super(Type.EquipmentSmith);
        int slot = 0;
        for (DungeonInstance dungeonInstance : DungeonDataLoader.getDungeonList()) {
            if (!dungeonInstance.isLegendRaid()) {
                CustomItemStack item = MapDataLoader.getMapData(dungeonInstance.getId()).viewItem().setCustomData("ShopData", dungeonInstance.getId());
                setItem(slot, item);
                slot++;
            }
        }
        equipmentSmith = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "ShopData")) {
                ShopData shopData = ShopDataLoader.getShopData(CustomItemStack.getCustomData(clickedItem, "ShopData"));
                playerData.getShopManager().open(shopData);
            }
        }
    }
}
