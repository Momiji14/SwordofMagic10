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

import static SwordofMagic10.Component.Function.*;

public class TierMenu extends GUIManager {

    public static final int[] TierReqLevel = {1, 25, 40, 50};

    private SomItem item;
    private SomItem tierStone;
    private int stoneAmount = 0;
    public TierMenu(PlayerData playerData) {
        super(playerData, "アイテム昇級", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 1 -> {
                item = null;
                tierStone = null;
                stoneAmount = 0;
            }
            case 4 -> {
                tierStone = null;
                stoneAmount = 0;
            }
            case 7 -> {
                if (item != null && tierStone != null) {
                    if (playerData.getItemInventory().has(tierStone, stoneAmount)) {
                        if (playerData.getMel() >= mel()) {
                            double percent = percent();
                            playerData.removeMel(mel());
                            playerData.getItemInventory().remove(tierStone, stoneAmount);
                            String percentText = " §e(" + scale(percent*100) + "%)";
                            if (randomDouble(0.0, 1.0) < percent) {
                                playerData.sendSomText(item.toSomText().addText("§aの§e昇級§aに§b成功§aしました" + percentText), SomSound.Level);
                                item.setTier(item.getTier() + 1);
                                item = null;
                                tierStone = null;
                                stoneAmount = 0;
                            } else {
                                playerData.sendSomText(item.toSomText().addText("§aの§e昇級§aに§c失敗§aしました" + percentText), SomSound.Tick);
                            }
                        } else {
                            playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                        }
                    } else {
                        playerData.sendSomText(tierStone.toSomText(stoneAmount).addText("§aが足りません"), SomSound.Nope);
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
                if (item instanceof SomQuality somQuality) {
                    if (somQuality.getLevel() >= TierReqLevel[item.getTier()]) {
                        this.item = item;
                    } else {
                        playerData.sendMessage("§aこの§eアイテム§aは§eLv" + TierReqLevel[item.getTier()] + "§aで§e昇級可能§aです", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessage("§e昇級可能アイテム§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (tierStone == null) {
                if (item.getId().equals("昇級石")) {
                    if (this.item.getTier() <= item.getTier()) {
                        tierStone = item;
                        stoneAmount = 1;
                        SomSound.Tick.play(playerData);
                    } else {
                        playerData.sendMessage("§eアイテム§aの§eティア以上§aの§e昇級石§aを§b選択§aしてください", SomSound.Nope);
                    }
                }
            } else if (tierStone == item && stack.getAmount() > stoneAmount) {
                stoneAmount++;
                SomSound.Tick.play(playerData);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        item = null;
        tierStone = null;
        stoneAmount = 0;
    }

    public int mel() {
        return 100 + (stoneAmount-1) * 70;
    }

    public double percent() {
        double percent = 1;
        for (int i = 0; i < stoneAmount; i++) {
            percent *= 0.7;
        }
        return 1-percent;
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
        setItem(4, tierStone == null ? Config.AirItem : tierStone.viewItem().setAmountReturn(stoneAmount));
        if (item != null && tierStone != null) {
            SomItem viewItem = item.clone();
            viewItem.setTier(viewItem.getTier() + 1);
            CustomItemStack item = viewItem.viewItem();
            item.addSeparator("装備昇級情報");
            item.addLore(decoLore("消費メル") + mel());
            item.addLore(decoLore("消費昇級石") + stoneAmount);
            item.addLore(decoLore("昇級確率") + scale(percent()*100, 2) + "%");
            setItem(7, item);
        } else {
            setItem(7, Config.AirItem);
        }
    }
}
