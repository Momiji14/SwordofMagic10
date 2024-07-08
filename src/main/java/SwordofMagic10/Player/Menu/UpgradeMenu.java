package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.decoLore;

public class UpgradeMenu extends GUIManager {
    public static final PlayerRank AnyWhereRank = PlayerRank.Emerald;
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
                SomSound.Tick.play(playerData);
            }
            case 4 -> {
                upgradeStone = null;
                stoneAmount = 0;
                SomSound.Tick.play(playerData);
            }
            case 7 -> {
                if (item != null && upgradeStone != null) {
                    if (playerData.getItemInventory().has(upgradeStone, stoneAmount)) {
                        if (playerData.getMel() >= mel()) {
                            playerData.removeMel(mel());
                            playerData.getItemInventory().remove(upgradeStone, stoneAmount);
                            item.addExp(exp());
                            playerData.sendSomText(item.toSomText().add("§aを§e精錬§aしました §7(" + item.getExp() + ")"), SomSound.Level);
                        } else {
                            playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                        }
                    } else {
                        playerData.sendSomText(upgradeStone.toSomText(stoneAmount).add("§aが足りません"), SomSound.Nope);
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
                if (this.item instanceof SomAlchemyStone stone) {
                    if (stone.getExpMap().containsKey(item.getId())) {
                        upgradeStone = item;
                        SomSound.Tick.play(playerData);
                    }
                } else if (item.getId().equals("精錬石")) {
                    upgradeStone = item;
                    SomSound.Tick.play(playerData);
                }
                if (upgradeStone != null) {
                    stoneAmount = 1;
                    SomSound.Tick.play(playerData);
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
        return 5 * stoneAmount;
    }

    public int exp() {
        if (this.item instanceof SomAlchemyStone stone) {
            return stone.getExpMap(upgradeStone.getId()) * stoneAmount;
        } else return (int) (10 * Math.pow(2.5, upgradeStone.getTier()-1) * stoneAmount);
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
