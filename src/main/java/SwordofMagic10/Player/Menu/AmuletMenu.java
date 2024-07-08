package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class AmuletMenu extends GUIManager {

    private SomAmulet amulet;
    public AmuletMenu(PlayerData playerData) {
        super(playerData, "願封願瓶", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 0) amulet = null;
        if (amulet != null) {
            int index = slot-2;
            if (index >= 0 && amulet.getBottles().size() > index) {
                SomAmulet.Bottle bottle = amulet.getBottle(index);
                playerData.getItemInventory().add(bottle, 1);
                amulet.removeBottle(index);
                SomSound.Tick.play(playerData);
            }
        }
        update();
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
            SomItemStack stack = playerData.getInventoryViewer().getSomItemStack(clickedItem);
            SomItem item = stack.getItem();
            if (amulet == null) {
                if (item instanceof SomAmulet amulet) {
                    this.amulet = amulet;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e願封§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (item instanceof SomAmulet.Bottle bottle) {
                if (amulet.getBottleSlot() > amulet.getBottles().size()) {
                    for (SomAmulet.Bottle somBottle : amulet.getBottles()) {
                        if (bottle.getId().equals(somBottle.getId())) {
                            playerData.sendMessage("§aすでに§e同名願瓶§aが§b願付§aされています", SomSound.Nope);
                            return;
                        }
                        if (bottle.getBottleCategory().equals(somBottle.getBottleCategory())){
                            playerData.sendMessage("§aすでに§e同じスキルに作用する願瓶§aが§b願付§aされています", SomSound.Nope);
                            return;
                        }
                    }
                    amulet.addBottle(bottle);
                    playerData.getItemInventory().remove(bottle, 1);
                } else {
                    playerData.sendMessage("§e願付枠§aが足りません", SomSound.Nope);
                }
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        amulet = null;
    }

    @Override
    public void update() {
        clear();
        setItem(1, Config.FlameItem);

        if (amulet != null) {
            setItem(0, amulet.viewItem());
            int slot = 2;
            for (SomAmulet.Bottle bottle : amulet.getBottles()) {
                setItem(slot, bottle.viewItem());
                slot++;
            }
        }
    }
}
