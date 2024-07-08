package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.InventoryViewer;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.MinMax;

public class StorageMenu extends GUIManager {
    public StorageMenu(PlayerData playerData) {
        super(playerData, "ストレージ", 6);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (Config.isRightSlot(slot)) {
                int index = Config.FixedSlot(slot) + scroll * 8;
                SomItemStack stack = playerData.getItemStorage().getInventory().get(index).clone();
                playerData.getItemStorage().remove(stack);
                playerData.getItemInventory().add(stack);
            } else {
                if (Config.UpScrollIcon.proximate(clickedItem)) {
                    removeScroll(clickType.isShiftClick() ? 10 : 1);
                } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                    addScroll(clickType.isShiftClick() ? 10 : 1);
                } else if (CustomItemStack.hasCustomData(clickedItem, "Sort") && CustomItemStack.hasCustomData(clickedItem, "SortIndex")) {
                    InventoryViewer.Sort sort = InventoryViewer.Sort.valueOf(CustomItemStack.getCustomData(clickedItem, "Sort"));
                    if (event.getClick().isLeftClick()) {
                        sort = sort.next();
                    } else if (event.getClick().isRightClick()) {
                        sort = sort.back();
                    }
                    playerData.getInventoryViewer().setSort(CustomItemStack.getCustomDataInt(clickedItem, "SortIndex"), sort);
                    playerData.getInventoryViewer().sort(playerData.getItemInventory().getInventory());
                    playerData.getInventoryViewer().sort(playerData.getItemStorage().getInventory());
                }
            }
            update();
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
            SomItemStack stack = playerData.getInventoryViewer().getSomItemStack(clickedItem);
            playerData.getItemStorage().add(stack);
            playerData.getItemInventory().remove(stack);
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        ItemStack[] contents = new ItemStack[54];
        int slot = 0;
        int index = scroll * 8;
        contents[8] = Config.UpScrollIcon.clone().setAmountReturn(scroll);
        contents[53] = Config.DownScrollIcon.clone().setAmountReturn(scrollAble()-scroll);
        while (playerData.getItemStorage().size() > index && slot < 54) {
            SomItemStack stack = playerData.getItemStorage().getInventory().get(index);
            contents[slot] = stack.viewItem().addLore("§8Index:" + index).setAmountReturn(stack.getAmount());
            index++;
            slot++;
            if (Math.floorMod(slot, 9) == 8) slot++;
        }
        slot = 17;
        int sortIndex = 0;
        for (InventoryViewer.Sort sort : playerData.getInventoryViewer().getSort()) {
            contents[slot] = sort.viewItem().setCustomData("SortIndex", sortIndex);
            slot += 9;
            sortIndex++;
        }
        //contents[slot] = new CustomItemStack(Material.LEVER).setNonDecoDisplay("§a上記のソート順でソートする").setCustomData("StartSort", true);
        setContents(contents);
    }

    public int scrollAble() {
        return (int) Math.ceil(playerData.getItemStorage().size()/8.0)-6;
    }

    private int scroll = 0;

    public int getScroll() {
        return scroll;
    }

    public void addScroll(int i) {
        scroll = MinMax(scrollAble(), 0, scroll+i);
    }


    public void removeScroll(int i) {
        scroll -= i;
        if (scroll < 0) scroll = 0;
    }
}
