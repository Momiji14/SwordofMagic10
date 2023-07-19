package SwordofMagic10.Player.Help;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.DataBase.HelpDataLoader;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class HelpMenu extends GUIManager {

    public HelpMenu(PlayerData playerData) {
        super(playerData, "ヘルプ", 3);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (selectCategory == null && category[slot] != null) {
                selectCategory = category[slot];
                update();
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        selectCategory = null;
    }

    private String selectCategory;
    private String[] category;

    @Override
    public void update() {
        clear();
        int slot = 0;
        if (selectCategory == null) {
            category = new String[getSize()*9];
            for (Map.Entry<String, List<HelpData>> entry : HelpDataLoader.getCategoryList().entrySet()) {
                String category = entry.getKey();
                List<HelpData> helpList = entry.getValue();
                CustomItemStack item = new CustomItemStack(Material.BOOK);
                item.setDisplay(category);
                for (HelpData helpData : helpList) {
                    item.addLore("§7・§e" + helpData.getDisplay());
                }
                this.category[slot] = category;
                setItem(slot, item);
                slot++;
            }
        } else {
            for (HelpData helpData : HelpDataLoader.getCategoryList(selectCategory)) {
                setItem(slot, helpData.viewItem());
                slot++;
            }
        }
    }
}
