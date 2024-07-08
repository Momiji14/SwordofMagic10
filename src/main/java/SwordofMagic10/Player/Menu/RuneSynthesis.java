package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import SwordofMagic10.Player.QuickGUI.RuneCrusher;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class RuneSynthesis extends GUIManager {
    public static final PlayerRank AnyWhereRank = PlayerRank.Diamond;
    private SomRune mainRune;
    private SomItem subRune;
    private SomRune resultRune;

    public RuneSynthesis(PlayerData playerData) {
        super(playerData, "ルーン合成", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            switch (slot) {
                case 1 -> {
                    mainRune = null;
                    subRune = null;
                    SomSound.Tick.play(playerData);
                }
                case 4 -> {
                    subRune = null;
                    SomSound.Tick.play(playerData);
                }
                case 7 -> {
                    if (CustomItemStack.hasCustomData(clickedItem, "AutoSubRune")) {
                        boolean setRune = false;
                        for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                            if (stack.getItem() instanceof SomRune rune && mainRune != rune) {
                                if (rune.getId().equals(mainRune.getId())) {
                                    if (resultPower(mainRune, rune) > mainRune.getPower()) {
                                        subRune = rune;
                                        SomSound.Tick.play(playerData);
                                        setRune = true;
                                    }
                                }
                            }
                        }
                        if (!setRune) playerData.sendMessage("§e素材ルーン§aの条件に当てはまる§eルーン§aがありません", SomSound.Nope);
                    } else if (resultRune != null) {
                        if (playerData.getMel() >= mel()) {
                            if (!(subRune instanceof SomRune)) {
                                SomItem powder = RuneCrusher.getPowder(mainRune);
                                int reqAmount = RuneCrusher.amount(mainRune);
                                if (playerData.getItemInventory().has(powder, reqAmount)) {
                                    playerData.getItemInventory().remove(powder, reqAmount);
                                } else {
                                    playerData.sendSomText(powder.toSomText(reqAmount).add("§aが§c必要§aです"), SomSound.Nope);
                                    return;
                                }
                            }
                            playerData.removeMel(mel());
                            playerData.getItemInventory().remove(subRune, 1);
                            playerData.getItemInventory().remove(mainRune, 1);
                            mainRune = (SomRune) playerData.getItemInventory().add(resultRune, 1);
                            playerData.sendSomText(resultRune.toSomText().add("§aに§eルーン力§aを注入しました"), SomSound.Level);
                            if (subRune instanceof SomRune) {
                                subRune = null;
                            }
                            resultRune = null;
                        } else {
                            playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                        }
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
            if (mainRune == null) {
                if (item instanceof SomRune rune) {
                    mainRune = rune;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§eメインルーン§aを§b選択§aしてください", SomSound.Nope);
                }
            } else if (subRune == null) {
                if (item.getItemCategory() == SomItem.ItemCategory.RunePowder) {
                    if (RuneCrusher.getPowder(mainRune).getId().equals(item.getId())) {
                        subRune = item;
                        SomSound.Tick.play(playerData);
                    } else {
                        playerData.sendMessage("§e同種§aの§eルーンの粉§aを§b選択§aしてください", SomSound.Nope);
                    }
                } else if (item instanceof SomRune rune && mainRune != rune) {
                    if (rune.isFavorite()) {
                        playerData.sendIsFavorite();
                        return;
                    }
                    if (rune.getId().equals(mainRune.getId())) {
                        if (resultPower(mainRune, rune) > mainRune.getPower()) {
                            subRune = rune;
                            SomSound.Tick.play(playerData);
                        } else {
                            playerData.sendMessage("§eルーン力§aの向上を得るには§e" + scale((mainRune.getPower()/multiply(mainRune)-mainRune.getPower())*100, 3) + "%以上§aの§eルーン力§aが必要です", SomSound.Nope);
                        }
                    } else {
                        playerData.sendMessage("§e同名ルーン§aを§b選択§aしてください", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessage("§e素材ルーン§aを§b選択§aしてください", SomSound.Nope);
                }
            }
            update();
        }
    }

    public int mel() {
        return (int) (mainRune.getPower()*100);
    }

    public int amount() {
        double value;
        if (mainRune.getPower() >= 1) {
            value = 0.874 * (6.6395 * Math.log(mainRune.getPower() + 81.656));
        } else {
            value = Math.pow(5 * mainRune.getPower(),0.263);
        }
        Log(mainRune.getId() + ":" + value);
        return (int) value;
    }

    @Override
    public void close(InventoryCloseEvent event) {
        mainRune = null;
        subRune = null;
        resultRune = null;
    }

    public static double multiply(SomRune mainRune) {
        return mainRune.getPower() >= 1 ? 0.52 : 0.6;
    }

    public static double resultPower(SomRune mainRune, SomRune subRune) {
        return (mainRune.getPower() + subRune.getPower()) * multiply(mainRune);
    }

    @Override
    public void update() {
        clear();
        for (int i = 0; i < 9; i++) {
            setItem(i, Config.FlameItem);
        }

        setItem(1, mainRune == null ? Config.AirItem : mainRune.viewItem());
        setItem(4, subRune == null ? Config.AirItem : subRune.viewItem());
        if (mainRune != null) {
            if (subRune != null){
                resultRune = mainRune.clone();
                double resultPower = resultPower(mainRune, subRune instanceof SomRune subRune ? subRune : mainRune);
                if (mainRune.getPower() < 1 && 1 <= resultPower) resultPower = 1;
                resultRune.setPower(resultPower);
                CustomItemStack item = resultRune.viewItem();
                item.addSeparator("ルーン力注入");
                item.addLore(decoLore("消費メル") + mel());
                if (!(subRune instanceof SomRune)) {
                    item.addLore(decoLore("消費粉") + RuneCrusher.amount(mainRune));
                }
                setItem(7, item);
            }
        } else {
            resultRune = null;
            setItem(7, Config.AirItem);
        }
    }
}
