package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomQuality;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.decoLore;

public class UpgradeMenu extends GUIManager {

    private SomQuality item;
    private SomItem upgradeStone;
    private int stoneAmount = 0;
    public UpgradeMenu(PlayerData playerData) {
        super(playerData, "アイテム精錬", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 1 -> {
                item = null;
                upgradeStone = null;
                stoneAmount = 0;
            }
            case 4 -> {
                upgradeStone = null;
                stoneAmount = 0;
            }
            case 7 -> {
                if (item != null && upgradeStone != null) {
                    if (playerData.getItemInventory().has(upgradeStone, stoneAmount)) {
                        if (playerData.getMel() >= mel()) {
                            playerData.removeMel(mel());
                            playerData.getItemInventory().remove(upgradeStone, stoneAmount);
                            item.addExp(exp());
                            playerData.sendSomText(item.toSomText().addText("§aを§e精錬§aしました §7(" + item.getExp() + ")"), SomSound.Level);
                        } else {
                            playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                        }
                    } else {
                        playerData.sendSomText(upgradeStone.toSomText(stoneAmount).addText("§aが足りません"), SomSound.Nope);
                    }
                }
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
            if (this.item == null) {
                if (item instanceof SomQuality somItem) {
                    this.item = somItem;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e精錬可能アイテム§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (upgradeStone == null) {
                if (item.getId().equals("精錬石")) {
                    if (this.item.getTier() <= item.getTier()) {
                        upgradeStone = item;
                        stoneAmount = 1;
                        SomSound.Tick.play(playerData);
                    } else {
                        playerData.sendMessage("§eアイテム§aの§eティア以上§aの§e精錬石§aを§b選択§aしてください", SomSound.Nope);
                    }
                }
            } else if (upgradeStone == item && stack.getAmount() > stoneAmount) {
                stoneAmount++;
                SomSound.Tick.play(playerData);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        item = null;
        upgradeStone = null;
        stoneAmount = 0;
    }

    public int mel() {
        return 10 + (stoneAmount-1) * 7;
    }

    public int exp() {
        return (int) (10 * Math.pow(upgradeStone.getTier(), 2) * stoneAmount);
    }

    @Override
    public void update() {
        clear();
        setItem(0, Config.FlameItem);
        setItem(2, Config.FlameItem);
        setItem(3, Config.FlameItem);
        setItem(5, Config.FlameItem);
        setItem(6, Config.FlameItem);
        setItem(8, Config.FlameItem);

        setItem(1, item == null ? Config.AirItem : item.viewItem());
        setItem(4, upgradeStone == null ? Config.AirItem : upgradeStone.viewItem().setAmountReturn(stoneAmount));
        if (item != null && upgradeStone != null) {
            SomQuality viewItem = item.clone();
            viewItem.addExp(exp());
            CustomItemStack item = viewItem.viewItem();
            item.addSeparator("アイテム精錬情報");
            item.addLore(decoLore("消費メル") + mel());
            item.addLore(decoLore("消費精錬石") + stoneAmount);
            item.addLore(decoLore("追加精錬値") + exp());
            setItem(7, item);
        } else {
            setItem(7, Config.AirItem);
        }
    }
}
