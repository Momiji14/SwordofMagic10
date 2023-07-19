package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.*;

public class EnhanceMenu extends GUIManager {

    public static int MaxPlus = 15;

    private SomQuality item;
    public EnhanceMenu(PlayerData playerData) {
        super(playerData, "アイテム強化", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 1 -> item = null;
            case 7 -> {
                if (playerData.getMel() >= mel()) {
                    if (item.getExp() >= reqExp()) {
                        double percent = percent();
                        String percentText = " §e(" + scale(percent*100) + "%)";
                        playerData.removeMel(mel());
                        item.addExp(-reqExp());
                        if (randomDouble(0.0, 1.0) < percent) {
                            playerData.sendSomText(item.toSomText().addText("§aの§e強化§aに§b成功§aしました" + percentText), SomSound.Level);
                            if (item instanceof SomEquipment equipment) {
                                equipment.setPlus(equipment.getPlus() + 1);
                                if (equipment.getPlus() >= MaxPlus) {
                                    item = null;
                                }
                            } else {
                                item.setLevel(item.getLevel() + 1);
                                if (item.getLevel() >= Classes.MaxLevel) {
                                    item = null;
                                }
                            }
                        } else {
                            playerData.sendSomText(item.toSomText().addText("§aの§e強化§aに§c失敗§aしました" + percentText), SomSound.Tick);
                            if (item instanceof SomEquipment equipment) {
                                if (equipment.getPlus() >= 10) {
                                    equipment.setPlus(0);
                                    playerData.sendSomText(item.toSomText().addText("§aの§e強化値§aが§c+0§aになりました" + percentText));
                                    item = null;
                                }
                            }
                        }
                    } else {
                        playerData.sendMessage("§e精錬値§aが足りません", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
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
                    if (somItem instanceof SomEquipment equipment && equipment.getPlus() >= MaxPlus) {
                        playerData.sendMessage("§aこの§e装備§aの§e強化値§aは§c最大§aです", SomSound.Nope);
                        return;
                    } else if (somItem.getLevel() >= Classes.MaxLevel) {
                        playerData.sendMessage("§aこの§eアイテム§aは§c最大レベル§aです", SomSound.Nope);
                        return;
                    }
                    this.item = somItem;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e強化可能アイテム§aを§b選択§aしてください", SomSound.Nope);
                }
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        item = null;
    }

    public int mel() {
        if (item instanceof SomEquipment equipment) {
            return (int) (30 * (1 + equipment.getLevel()*0.1) * (1 + equipment.getPlus()*0.1));
        } else if (item instanceof SomRune) {
            return (int) (50 * (1 + item.getLevel()*0.1));
        }
        return 0;
    }

    public int reqExp() {
        return reqExp(item);
    }

    public static int reqExp(SomQuality item) {
        if (item instanceof SomEquipment equipment) {
            return (int) (100 * (1 + equipment.getLevel()*0.1) * (1 + equipment.getPlus()*0.1));
        } else if (item instanceof SomRune) {
            return (int) (100 * (1 + item.getLevel()*0.1));
        }
        return 0;
    }

    public double percent() {
        if (item instanceof SomEquipment equipment) {
            if (equipment.getPlus() < 10) {
                return 1 * Math.pow(0.99, equipment.getPlus());
            } else {
                return 0.1;
            }
        } else if (item instanceof SomRune) {
            return 1;
        }
        return 0;
    }

    @Override
    public void update() {
        clear();
        setItem(0, Config.FlameItem);
        setItem(2, Config.FlameItem);
        setItem(3, Config.FlameItem);
        setItem(4, Config.FlameItem);
        setItem(5, Config.FlameItem);
        setItem(6, Config.FlameItem);
        setItem(8, Config.FlameItem);

        setItem(1, item == null ? Config.AirItem : item.viewItem());
        if (item != null) {
            SomQuality viewItem = item.clone();
            List<String> addLore = new ArrayList<>();
            addLore.add(decoSeparator("アイテム強化情報"));
            addLore.add(decoLore("消費メル") + mel());
            addLore.add(decoLore("消費精錬値") + reqExp());
            addLore.add(decoLore("強化確率") + scale(percent()*100, 2) + "%");
            if (viewItem instanceof SomEquipment equipment) {
                if (equipment.getPlus() >= 10) {
                    setItem(4, new CustomItemStack(Material.TNT).setDisplay("§c強化値+10以上で失敗すると+0になります"));
                    addLore.add("§c※強化値+10以上で失敗すると+0になります");
                }
                equipment.setPlus(equipment.getPlus() + 1);
            } else {
                viewItem.setLevel(viewItem.getLevel() + 1);
            }
            CustomItemStack item = viewItem.viewItem();
            item.addLore(addLore);
            setItem(7, item);
        } else {
            setItem(7, Config.AirItem);
        }
    }
}
