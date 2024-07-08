package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomRune;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static SwordofMagic10.SomCore.Log;

public class RuneCrusher extends QuickGUI {
    public static final PlayerRank AutoCrash = PlayerRank.Diamond;
    public static final PlayerRank AllCrashRank = PlayerRank.Gold;
    private static final RuneCrusher runeCrusher = new RuneCrusher();
    public static void open(PlayerData playerData) {
        runeCrusher.openIns(playerData);
    }
    public RuneCrusher() {
        super(Type.RuneCrusher);
        CustomItemStack flame = Config.FlameItem.clone().setNonDecoDisplay("§c粉砕§aしたい§eルーン§aを選択してください");
        for (int i = 0; i < 9; i++) {
            setItem(i, flame);
        }
        setItem(4, new CustomItemStack(Material.PISTON).setDisplay("一括粉砕").addLore("§e100%未満§aの§eルーン§aを§c粉砕§aします").addLore("§c※" + AllCrashRank.getDisplay() + "§a以上が§c必要§aです").setCustomData("AllCrash", true));
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "AllCrash")) {
                if (playerData.hasRank(AllCrashRank)) {
                    HashMap<String, Integer> count = new HashMap<>();
                    for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                        if (stack.getItem() instanceof SomRune rune && !rune.isFavorite()) {
                            if (rune.getPower() <= 1) {
                                count.merge(rune.getId().replace("RuneOf", ""), amount(rune), Integer::sum);
                                playerData.getItemInventory().remove(rune, 1);
                            }
                        }
                    }
                    if (!count.isEmpty()) {
                        count.forEach((powder, amount) -> playerData.getItemInventory().add(ItemDataLoader.getItemData("RunePowderOf" + powder), amount, true));
                        playerData.sendMessage("§c一回粉砕§aを行いました", SomSound.Tick);
                    } else {
                        playerData.sendMessage("§c粉砕可能§aな§eルーン§aがありません", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessageReqRank(AllCrashRank);
                }
            }
        }
    }

    @Override
    public void bottomClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
                SomItem item = playerData.getInventoryViewer().getSomItemStack(clickedItem).getItem();
                if (item instanceof SomRune rune) {
                    if (rune.isFavorite()) {
                        playerData.sendIsFavorite();
                        return;
                    }
                    SomItem powder = getPowder(rune);
                    int amount = amount(rune);
                    playerData.getItemInventory().remove(rune, 1);
                    playerData.getItemInventory().add(powder, amount);
                    playerData.sendSomText(rune.toSomText().add("§aが").add(powder.toSomText(amount)).add("§aになりました"), SomSound.Tick);
                }
            }
        }
    }

    public static SomItem getPowder(SomRune rune) {
        return ItemDataLoader.getItemData(rune.getId().replace("Rune", "RunePowder"));
    }

    public static int amount(SomRune rune) {
        double value;
        if (rune.getPower() >= 1) {
            value = 8.8 * 4.5585 * Math.pow(10, -6) * Math.expm1(0.150614 * (rune.getPower()*100));
        } else {
            value = 3.46767 * Math.pow(10, -6) * Math.pow((rune.getPower()*100), 3.802281368821292);
        }
        return (int) value;
    }
}
