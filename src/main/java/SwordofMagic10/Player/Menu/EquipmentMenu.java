package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class EquipmentMenu extends GUIManager {
    public EquipmentMenu(PlayerData playerData) {
        super(playerData, "§l装備メニュー", 3);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && !playerData.isDeath()) {
            if (CustomItemStack.hasCustomData(clickedItem, "EquipSlot")) {
                EquipSlot equipSlot = EquipSlot.valueOf(CustomItemStack.getCustomData(clickedItem, "EquipSlot"));
                playerData.unEquip(equipSlot);
            }
            if (CustomItemStack.hasCustomData(clickedItem, "ToolType")) {
                SomTool.Type type = SomTool.Type.valueOf(CustomItemStack.getCustomData(clickedItem, "ToolType"));
                playerData.getGatheringMenu().unEquip(type);
            }
            update();
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        update();
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        clear();
        int slot = 0;
        for (EquipSlot equipSlot : EquipSlot.values()) {
            SomEquipment equipment = playerData.getEquipment(equipSlot);
            CustomItemStack item = equipment != null ? equipment.viewItem().setCustomData("EquipSlot", equipSlot.toString()) : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c" + equipSlot.getDisplay());
            setItem(slot, item);
            slot++;
        }
        slot = 18;
        for (SomTool.Type toolType : SomTool.Type.values()) {
            CustomItemStack item = playerData.getGatheringMenu().getToolViewItem(toolType).setCustomData("ToolType", toolType.toString());
            setItem(slot, item);
            slot++;
        }
    }
}
