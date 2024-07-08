package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.PlayerRank;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.*;

public class EnhanceMenu extends GUIManager {
    public static final PlayerRank AnyWhereRank = PlayerRank.Emerald;
    public static int MaxPlus = 15;

    private SomQuality item;
    private int maxPlus = 0;
    private int limit = 11;
    public EnhanceMenu(PlayerData playerData) {
        super(playerData, "アイテム強化", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 1 -> {
                item = null;
                SomSound.Tick.play(playerData);
            }
            case 4 -> {
                limit++;
                if (limit > MaxPlus) limit = 11;
                update();
            }
            case 7 -> {
                if (playerData.getMel() >= mel()) {
                    if (item.getExp() >= reqExp()) {
                        double percent = percent();
                        String percentText = " §e(" + scale(percent*100) + "%)";
                        playerData.removeMel(mel());
                        item.addExp(-reqExp());
                        if (randomDouble(0.0, 1.0) < percent) {
                            playerData.sendSomText(item.toSomText().add("§aの§e強化§aに§b成功§aしました" + percentText), SomSound.Level);
                            if (item instanceof SomPlus plus) {
                                plus.setPlus(plus.getPlus() + 1);
                                if (plus.getPlus() > 10) {
                                    SomText text = SomText.create(playerData.getDisplayName()).add("§aが").add(plus.toSomText()).add("§e+" + plus.getPlus() + "§aの§e強化§aに§b成功§aしました");
                                    if (plus.getPlus() > maxPlus) {
                                        SomCore.globalMessageComponent(text);
                                    }
                                    maxPlus = Math.max(maxPlus, plus.getPlus());
                                }
                                if (plus.getPlus() >= limit) item = null;
                            } else {
                                item.setLevel(item.getLevel() + 1);
                                if (item.getLevel() >= Classes.MaxLevel) {
                                    item = null;
                                }
                            }
                        } else {
                            playerData.sendSomText(item.toSomText().add("§aの§e強化§aに§c失敗§aしました" + percentText), SomSound.Tick);
                            if (item instanceof SomPlus somPlus) {
                                int plus = somPlus.getPlus();
                                if (plus > 10) {
                                    somPlus.setPlus(10);
                                    playerData.sendSomText(item.toSomText().add("§aの§e強化値§aが§c+10§aになりました"));
                                    if (plus >= limit) item = null;
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
                if (item instanceof SomQuality quality) {
                    if (quality instanceof SomPlus plus && plus.getPlus() >= MaxPlus) {
                        playerData.sendMessage("§aこの§eアイテム§aの§e強化値§aは§c最大§aです", SomSound.Nope);
                        return;
                    } else if (quality.getLevel() >= Classes.MaxLevel) {
                        playerData.sendMessage("§aこの§eアイテム§aは§c最大レベル§aです", SomSound.Nope);
                        return;
                    }
                    this.item = quality;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§e強化可能アイテム§aを§b選択§aしてください", SomSound.Nope);
                }
            }
            update();
        }
    }

    @Override
    public void open() {
        item = null;
        maxPlus = 0;
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    public int mel() {
        if (item instanceof SomPlus plus) {
            return (int) (30 * (1 + plus.getLevel()*0.01) * (1 + plus.getTier()*0.05) * (1 + plus.getPlus()*0.1));
        } else if (item instanceof SomRune) {
            return (int) (50 * (1 + item.getLevel()*0.1) * (1 + item.getTier()*0.05));
        }
        return 0;
    }

    public int reqExp() {
        return reqExp(item);
    }

    public static int reqExp(SomQuality item) {
        if (item instanceof SomPlus plus) {
            return (int) (100 * (1 + plus.getLevel()*0.1) * (1 + plus.getPlus()*0.1) * (0.5 + plus.getTier()*0.2));
        } else if (item instanceof SomRune) {
            return (int) (100 * (1 + item.getLevel()*0.1) * (0.5 + item.getTier()*0.2));
        }
        return 0;
    }

    public double percent() {
        if (item instanceof SomPlus plus) {
            if (plus.getPlus() < 10) {
                return 1 * Math.pow(0.99, plus.getPlus());
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
            if (viewItem instanceof SomPlus plus) {
                CustomItemStack item = new CustomItemStack(Material.TNT).setNonDecoDisplay("§e強化値リミット§7: §a+" + limit);
                if (plus.getPlus() >= 10) {
                    addLore.add("§c※強化値+10以上で失敗すると+10になります");
                    item.addLore("§c※強化値+10以上で失敗すると+10になります");
                }
                setItem(4, item);
                plus.setPlus(plus.getPlus() + 1);
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
