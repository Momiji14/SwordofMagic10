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
        this.display = display;
        this.size = size;
        playerData.addGUIManager(this);
        refresh();
    }

    public void setContents(ItemStack[] contents) {
        if (playerData.isBE()) {
            for (ItemStack item : contents) {
                if (item != null && item.getType() == Material.POTION) {
                    item.setType(Material.DRAGON_BREATH);
                }
            }
        }
        inventory.setContents(contents);
    }

    public void setItem(int slot, ItemStack item) {
        if (playerData.isBE()) {
            if (item != null && item.getType() == Material.POTION) {
                item.setType(Material.DRAGON_BREATH);
            }
        }
        inventory.setItem(slot, item);
    }

    public void setItem(int x, int y, ItemStack item) {
        setItem(getSlot(x, y), item);
    }

    public int[] getSlot(int slot) {
        return new int[]{slot % 9, slot/9};
    }

    public int getSlot(int x, int y) {
        return x+y*9;
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

    public abstract static class Sub extends Bar {
        private final String subDisplay;
        private final Inventory subInventory;

        public Sub(PlayerData playerData, String display, int size, String subDisplay, int subSize) {
            super(playerData, display, size);
            this.subDisplay = subDisplay;
            subInventory = Bukkit.createInventory(null, subSize*9, subDisplay);
            subInventory.setMaxStackSize(127);
        }

        public String subDisplay() {
            return subDisplay;
        }

        public void subSetItem(int slot, ItemStack item) {
            if (subInventory != null) {
                subInventory.setItem(slot, item);
            }
        }

        public void subClear() {
            if (subInventory != null) {
                subInventory.clear();
            }
        }

        public void subOpen() {
            subUpdate();
            SomTask.sync(() -> playerData.getPlayer().openInventory(subInventory));
        }

        public abstract void subTopClick(InventoryClickEvent event);
        public abstract void subBottomClick(InventoryClickEvent event);

        public void subUpdate() {

        }
    }

    public abstract static class Bar extends GUIManager {
        private static final CustomItemStack Amount_Add_1 = new CustomItemStack(Material.OAK_SIGN).setNonDecoDisplay("§e+1");
        private static final CustomItemStack Amount_Add_10 = new CustomItemStack(Material.SPRUCE_SIGN).setNonDecoDisplay("§e+10");
        private static final CustomItemStack Amount_Add_100 = new CustomItemStack(Material.DARK_OAK_SIGN).setNonDecoDisplay("§e+100");
        private static final CustomItemStack Amount_Remove_1 = new CustomItemStack(Material.OAK_HANGING_SIGN).setNonDecoDisplay("§e-1");
        private static final CustomItemStack Amount_Remove_10 = new CustomItemStack(Material.SPRUCE_HANGING_SIGN).setNonDecoDisplay("§e-10");
        private static final CustomItemStack Amount_Remove_100 = new CustomItemStack(Material.DARK_OAK_HANGING_SIGN).setNonDecoDisplay("§e-100");


        protected int amount = 1;
        public Bar(PlayerData playerData, String display, int size) {
            super(playerData, display, size);
        }

        public void updateBar() {
            CustomItemStack amountIcon = new CustomItemStack(Material.GOLD_BLOCK).setNonDecoDisplay("§ex" + amount);
            amountIcon.setAmount(amount);
            int slot = getSize()*9-9;
            setItem(slot, Config.FlameItem);
            setItem(slot+1, Amount_Remove_100);
            setItem(slot+2, Amount_Remove_10);
            setItem(slot+3, Amount_Remove_1);
            setItem(slot+4, amountIcon);
            setItem(slot+5, Amount_Add_1);
            setItem(slot+6, Amount_Add_10);
            setItem(slot+7, Amount_Add_100);
            setItem(slot+8, Config.FlameItem);
        }

        public void barClick(InventoryClickEvent event) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                int amount = 0;
                if (Amount_Add_1.proximate(clickedItem)) {
                    amount += 1;
                } else if (Amount_Add_10.proximate(clickedItem)) {
                    amount += 10;
                } else if (Amount_Add_100.proximate(clickedItem)) {
                    amount += 100;
                } else if (Amount_Remove_1.proximate(clickedItem)) {
                    amount -= 1;
                } else if (Amount_Remove_10.proximate(clickedItem)) {
                    amount -= 10;
                } else if (Amount_Remove_100.proximate(clickedItem)) {
                    amount -= 100;
                }
                if (event.getClick().isShiftClick()) amount = amount > 0 ? 1000 : -1000;
                if (amount != 0) {
                    this.amount = MinMax(this.amount == 1 && amount != 1 ? amount : this.amount + amount, 1, 10000);
                    update();
                    SomSound.Tick.play(playerData);
                }
            }
        }

        @Override
        public void open() {
            amount = 1;
            super.open();
        }
    }
}

