package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SeriesMenu extends GUIManager {
    private SomEquipment equipment;
    private final List<SomEquipment> list = new ArrayList<>();
    public SeriesMenu(PlayerData playerData) {
        super(playerData, "シリーズ交換", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && equipment != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "Equipment")) {
                equipment = null;
                SomSound.Tick.play(playerData);
            } else if (CustomItemStack.hasCustomData(clickedItem, "Index")) {
                int mel = 100;
                if (playerData.getMel() >= mel) {
                    playerData.removeMel(mel);
                    int index = CustomItemStack.getCustomDataInt(clickedItem, "Index");
                    SomEquipment nextEquipment = list.get(index);
                    playerData.getItemInventory().remove(equipment, 1);
                    playerData.getItemInventory().add(nextEquipment, 1);
                    playerData.sendMessage("§eシリーズ交換§aしました §e-" + mel + "メル");
                    equipment = null;
                    list.clear();
                } else {
                    playerData.sendMessageNonMel();
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
            if (item instanceof SomEquipment equipment && equipment.hasSeries()) {
                this.equipment = equipment;
                SomSound.Tick.play(playerData);
            } else {
                playerData.sendMessage("§eシリーズアイテム§aを§b選択§aしてください", SomSound.Nope);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        equipment = null;
        list.clear();
    }

    @Override
    public void update() {
        for (int i = 0; i < 9; i++) {
            setItem(i, Config.AirItem);
        }
        setItem(1, Config.FlameItem);
        list.clear();
        if (equipment != null) {
            setItem(0, equipment.viewItem().setCustomData("Equipment", true));
            int i = 0;
            for (SomEquipment series : ItemDataLoader.getSeries(equipment.getSeries())) {
                SomEquipment equipment = series.clone();
                equipment.setTier(this.equipment.getTier());
                equipment.setLevel(this.equipment.getLevel());
                equipment.setExp(this.equipment.getExp());
                equipment.setQuality(this.equipment.getQuality());
                equipment.setPlus(this.equipment.getPlus());
                for (SomRune rune : this.equipment.getRune()) {
                    equipment.addRune(rune);
                }
                list.add(equipment);
                setItem(i+2, equipment.viewItem().setCustomData("Index", i));
                i++;
            }
        }
    }
}
