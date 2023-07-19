package SwordofMagic10.Player;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.MinMax;

public abstract class GUIManager {
    protected final PlayerData playerData;
    protected final Player player;
    private String display;
    private int size;
    private Inventory inventory;

    public GUIManager(PlayerData playerData, String display, int size) {
        this.playerData = playerData;
        player = playerData.getPlayer();
        setDisplay(display);
        setSize(size);
        playerData.addGUIManager(this);
        refresh();
    }

    public void setContents(ItemStack[] contents) {
        inventory.setContents(contents);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void refresh() {
        inventory = Bukkit.createInventory(null, size*9, display);
        inventory.setMaxStackSize(127);
    }

    public abstract void topClick(InventoryClickEvent event);
    public abstract void bottomClick(InventoryClickEvent event);

    public abstract void close(InventoryCloseEvent event);

    public void open() {
        update();
        SomTask.sync(() -> playerData.getPlayer().openInventory(inventory));
    }

    public abstract void update();

    public void clear() {
        inventory.clear();
    }

    public abstract static class Bar extends GUIManager {
        private static final CustomItemStack Amount_Add_1 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e+1");
        private static final CustomItemStack Amount_Add_64 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e+64");
        private static final CustomItemStack Amount_Add_256 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e+256");
        private static final CustomItemStack Amount_Remove_1 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e-1");
        private static final CustomItemStack Amount_Remove_64 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e-64");
        private static final CustomItemStack Amount_Remove_256 = new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setDisplay("§e-256");


        protected int amount = 1;
        public Bar(PlayerData playerData, String display, int size) {
            super(playerData, display, size);
        }

        public void updateBar() {
            CustomItemStack amountIcon = new CustomItemStack(Material.GOLD_NUGGET).setDisplay("§ax" + amount);
            amountIcon.setAmount(amount);
            int slot = getSize()*9-9;
            setItem(slot, Config.FlameItem);
            setItem(slot+1, Amount_Remove_256);
            setItem(slot+2, Amount_Remove_64);
            setItem(slot+3, Amount_Remove_1);
            setItem(slot+4, amountIcon);
            setItem(slot+5, Amount_Add_1);
            setItem(slot+6, Amount_Add_64);
            setItem(slot+7, Amount_Add_256);
            setItem(slot+8, Config.FlameItem);
        }

        public void barClick(InventoryClickEvent event) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                int amount = 0;
                if (Amount_Add_1.proximate(clickedItem)) {
                    amount += 1;
                } else if (Amount_Add_64.proximate(clickedItem)) {
                    amount += 64;
                } else if (Amount_Add_256.proximate(clickedItem)) {
                    amount += 256;
                } else if (Amount_Remove_1.proximate(clickedItem)) {
                    amount -= 1;
                } else if (Amount_Remove_64.proximate(clickedItem)) {
                    amount -= 64;
                } else if (Amount_Remove_256.proximate(clickedItem)) {
                    amount -= 256;
                }
                if (amount != 0) {
                    this.amount = MinMax(this.amount == 1 && amount != 1 ? amount : this.amount + amount, 1, 10000);
                    update();
                    SomSound.Tick.play(playerData);
                }
            }
        }
    }
}

