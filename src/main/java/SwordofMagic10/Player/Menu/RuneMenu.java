package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomRune;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class RuneMenu extends GUIManager {

    private SomEquipment equipment;
    public RuneMenu(PlayerData playerData) {
        super(playerData, "ルーン装着", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 1) equipment = null;
        if (equipment != null) {
            int index = slot-3;
            SomRune rune = equipment.getRune(index);
            if (rune != null) {
                playerData.getItemInventory().add(rune, 1);
                equipment.removeRune(index);
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
            if (equipment == null) {
                if (item instanceof SomEquipment equipment) {
                    this.equipment = equipment;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e装備§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (item instanceof SomRune rune) {
                if (equipment.getRuneSlot() > equipment.getRune().size()) {
                    for (SomRune somRune : equipment.getRune()) {
                        if (rune.getId().equals(somRune.getId())) {
                            playerData.sendMessage("§aすでに§e同名ルーン§aが§b装着§aされています", SomSound.Nope);
                            return;
                        }
                    }
                    equipment.addRune(rune);
                    playerData.getItemInventory().remove(rune, 1);
                } else {
                    playerData.sendMessage("§eルーン枠§aが足りません", SomSound.Nope);
                }
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        equipment = null;
    }

    private static final CustomItemStack NonSlot = new CustomItemStack(Material.BARRIER).setDisplay("§c未開放スロット");
    @Override
    public void update() {
        clear();
        setItem(0, Config.FlameItem);
        setItem(2, Config.FlameItem);
        setItem(8, Config.FlameItem);

        if (equipment != null) {
            setItem(1, equipment.viewItem());
            int slot = 3;
            for (SomRune rune : equipment.getRune()) {
                setItem(slot, rune.viewItem());
                slot++;
            }
            for (int i = equipment.getRuneSlot(); i < 5; i++) {
                setItem(i+3, NonSlot);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                setItem(i+3, NonSlot);
            }
            setItem(1, Config.AirItem);
        }
    }
}
