package SwordofMagic10.Player.Shop;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomInventory;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.MinMax;

public class SellManager extends GUIManager.Bar {
    private final SomInventory sellInventory;
    public static final PlayerRank AnyWhereRank = PlayerRank.Gold;

    public SellManager(PlayerData playerData) {
        super(playerData, "アイテム売却", 6);
        sellInventory = new SomInventory(playerData);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        if (sellInventory.getInventory().size() > slot && slot < 45) {
            SomItemStack stack = sellInventory.getInventory().get(slot);
            SomItem item = stack.getItem();
            int amount = Math.min(stack.getAmount(), super.amount);
            if (clickType.isShiftClick()) amount = stack.getAmount();
            int mel = item.getSell() * amount;
            if (playerData.getMel() >= mel) {
                sellInventory.remove(item, amount);
                playerData.getItemInventory().add(item, amount);
                playerData.removeMel(mel);
                playerData.sendSomText(item.toSomText(amount).add("§aを§b買戻§aしました"), SomSound.Tick);
                update();
            } else {
                playerData.sendMessageNonMel();
            }
        }
        barClick(event);
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
            SomItemStack stack = playerData.getInventoryViewer().getSomItemStack(clickedItem);
            SomItem item = stack.getItem();
            if (!item.isFavorite()) {
                if (item.getSell() != -1) {
                    int amount = MinMax(super.amount, 0, stack.getAmount());
                    if (event.getClick().isShiftClick()) amount = stack.getAmount();
                    playerData.getItemInventory().remove(item, amount);
                    sellInventory.add(item, amount);
                    int mel = item.getSell() * amount;
                    playerData.addMel(mel);
                    playerData.sendMessage(item.getColorDisplay() + "§ex" + amount + "§aを§e" + mel + "メル§aで§c売却§aしました", SomSound.Level);
                    if (sellInventory.getInventory().size() > 45) {
                        sellInventory.getInventory().remove(0);
                    }
                } else {
                    playerData.sendMessage(item.getColorDisplay() + "§aは§c売却§aできません", SomSound.Nope);
                }
            } else {
                playerData.sendMessage(item.getColorDisplay() + "§aは§cロック§aされています", SomSound.Nope);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        clear();
        int slot = 0;
        for (SomItemStack stack : sellInventory.getInventory()) {
            setItem(slot, stack.viewItem().setAmountReturn(stack.getAmount()));
            slot++;
            if (slot > 45) break;
        }
        updateBar();
    }
}
