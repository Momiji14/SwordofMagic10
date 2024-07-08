package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.randomInt;

public class AmuletTrade extends QuickGUI {
    private static AmuletTrade amuletTrade;
    private static SomAmulet GriphiaAmulet;
    private static SomItem GriphiaFlame;
    private static SomAmulet Amulet2;
    public static void open(PlayerData playerData) {
        amuletTrade.openIns(playerData);
    }
    public AmuletTrade() {
        super(Type.AmuletTrade);
        GriphiaAmulet = (SomAmulet) ItemDataLoader.getItemData("願封のアミュレット");
        GriphiaFlame = ItemDataLoader.getItemData("グリフィアの炎");
        Amulet2 = (SomAmulet) ItemDataLoader.getItemData("弱封のアミュレット");
        setItem(0, GriphiaAmulet.viewItem().setCustomData("GriphiaAmulet", true));
        setItem(1, Amulet2.viewItem().setCustomData("Amulet2", true));
        amuletTrade = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "GriphiaAmulet")) {
                if (playerData.getItemInventory().has(GriphiaFlame, 1)) {
                    SomAmulet amulet = GriphiaAmulet.clone();
                    amulet.randomStatus();
                    amulet.randomQuality();
                    amulet.setPlus(randomInt(1, 11));
                    playerData.getItemInventory().remove(GriphiaFlame, 1);
                    playerData.getItemInventory().add(amulet, 1);
                    playerData.sendSomText(amulet.toSomText().add("を§e交換§aしました"), SomSound.Tick);
                } else {
                    playerData.sendSomText(GriphiaFlame.toSomText().add("が必要です"), SomSound.Nope);
                }
            }
        }
    }
}
