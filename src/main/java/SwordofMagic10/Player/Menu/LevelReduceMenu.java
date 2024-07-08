package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LevelReduceMenu extends GUIManager {
    private SomQuality item;
    private int level = 1;
    public LevelReduceMenu(PlayerData playerData) {
        super(playerData, "レベル降下", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && item != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "Item")) {
                item = null;
                SomSound.Tick.play(playerData);
            } else if (CustomItemStack.hasCustomData(clickedItem, "Complete")) {
                item.setLevel(level);
                playerData.sendMessage("§eレベル降下§aしました", SomSound.Level);
                item = null;
            } else if (CustomItemStack.hasCustomData(clickedItem, "LevelReduce")) {
                if (level < item.getLevel()) {
                    level++;
                    SomSound.Tick.play(playerData);
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
            SomItem item = stack.getItem();
            if (item instanceof SomQuality quality) {
                this.item = quality;
                level = 1;
                SomSound.Tick.play(playerData);
            } else {
                playerData.sendMessage("§eレベル降下可能アイテム§aを§b選択§aしてください", SomSound.Nope);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        item = null;
        level = 1;
    }

    @Override
    public void update() {
        clear();
        for (int i = 0; i < 9; i++) {
            setItem(i, Config.FlameItem);
        }

        if (item != null) {
            SomQuality viewItem = item.clone();
            viewItem.setLevel(level);
            setItem(1, item.viewItem().setCustomData("Item", true));
            setItem(4, new CustomItemStack(Material.EXPERIENCE_BOTTLE).setNonDecoDisplay("§e変更後レベル　§b-> §a" + level).setCustomData("LevelReduce", true));
            setItem(7, viewItem.viewItem().setCustomData("Complete", true));
        } else {
            setItem(1, Config.AirItem);
            setItem(7, Config.AirItem);
        }
    }
}
