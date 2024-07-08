package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquip;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InfoMenu extends GUIManager {
    private PlayerData targetData = playerData;
    public InfoMenu(PlayerData playerData) {
        super(playerData, "プレイヤー情報", 3);
    }

    public void open(PlayerData targetData) {
        this.targetData = targetData;
        super.open();
    }

    @Override
    public void topClick(InventoryClickEvent event) {

    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        clear();

        setItem(0, UserMenu.statusViewItem(targetData));
        setItem(1, UserMenu.gatheringViewItem(targetData));
        setItem(2, UserMenu.otherViewItem(targetData));
        setItem(3, targetData.getStatistics().viewItem());

        int slot = 9;
        for (EquipSlot equipSlot : EquipSlot.values()) {
            SomEquip equipItem = targetData.getEquipment(equipSlot);
            CustomItemStack item = equipItem != null ? equipItem.viewItem() : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c" + equipSlot.getDisplay());
            setItem(slot, item);
            slot++;
        }

        slot = 18;
        for (SomTool.Type toolType : SomTool.Type.values()) {
            CustomItemStack item = targetData.getGatheringMenu().getToolViewItem(toolType);
            setItem(slot, item);
            slot++;
        }
    }
}
