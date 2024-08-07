package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.Component.Function.randomDouble;

public class QualityMenu extends GUIManager {

    private SomQuality quality;
    private SomItem qualityStone;
    private SomQuality.Rank rank = SomQuality.Rank.Mythical;
    public QualityMenu(PlayerData playerData) {
        super(playerData, "品質変更", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 1 -> {
                quality = null;
                qualityStone = null;
                SomSound.Tick.play(playerData);
            }
            case 3 -> {
                qualityStone = null;
                SomSound.Tick.play(playerData);
            }
            case 5 -> {
                if (event.getClick().isRightClick()) {
                    rank = rank.back();
                } else {
                    rank = rank.next();
                }
                SomSound.Tick.play(playerData);
            }
            case 7 -> {
                if (quality != null && qualityStone != null) {
                    if (quality.getQualityRank().ordinal() > rank.ordinal()) {
                        if (playerData.getItemInventory().has(qualityStone, 1)) {
                            if (playerData.getMel() >= mel()) {
                                playerData.removeMel(mel());
                                playerData.getItemInventory().remove(qualityStone, 1);
                                SomQuality.Rank oldRank = quality.getQualityRank();
                                quality.setQuality(randomDouble(randomDouble(0, 0.5), 1));
                                SomQuality.Rank rank = quality.getQualityRank();
                                playerData.sendSomText(quality.toSomText().add("§aの§e品質変更§aを行いました " + oldRank.getColorDisplay() + " §a-> " + rank.getColorDisplay()), SomSound.Level);
                            } else {
                                playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                            }
                        } else {
                            playerData.sendSomText(qualityStone.toSomText().add("§aが足りません"), SomSound.Nope);
                        }
                    } else {
                        playerData.sendMessage("§e品質§aが§b設定値以上§aです", SomSound.Nope);
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
            if (quality == null) {
                if (item instanceof SomQuality qualityItem) {
                    quality = qualityItem;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e品質変更可能アイテム§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (qualityStone == null) {
                if (item.getId().equals("品質変更石")) {
                    if (this.quality.getTier() <= item.getTier()) {
                        qualityStone = item;
                        SomSound.Tick.play(playerData);
                    } else {
                        playerData.sendMessage("§eアイテム§aの§eティア以上§aの§e品質変更石§aを§b選択§aしてください", SomSound.Nope);
                    }
                }
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        quality = null;
        qualityStone = null;
    }

    public int mel() {
        return 30;
    }

    @Override
    public void update() {
        clear();
        setItem(0, Config.FlameItem);
        setItem(2, Config.FlameItem);
        setItem(4, Config.FlameItem);
        setItem(6, Config.FlameItem);
        setItem(8, Config.FlameItem);

        setItem(1, quality == null ? Config.AirItem : quality.viewItem());
        setItem(3, qualityStone == null ? Config.AirItem : qualityStone.viewItem());
        setItem(5, new CustomItemStack(rank.getIcon()).setNonDecoDisplay(rank.getColorDisplay()));
        if (quality != null && qualityStone != null) {
            SomItem viewItem = quality.clone();
            CustomItemStack item = viewItem.viewItem();
            item.addSeparator("品質変更情報");
            item.addLore(decoLore("消費メル") + mel());
            item.addLore(decoLore("消費品質変更石") + 1);
            setItem(7, item);
        } else {
            setItem(7, Config.AirItem);
        }
    }
}
