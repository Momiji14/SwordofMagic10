package SwordofMagic10.Player.Shop;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class ShopManager extends GUIManager.Bar {

    private ShopData shopData;
    private int page = 0;
    public int offset() {
        return page * 45;
    }

    public void addPage() {
        page = Math.min(shopData.getMaxPage(), page + 1);
    }

    public void removePage() {
        page = Math.max(0, page - 1);
    }

    public ShopManager(PlayerData playerData) {
        super(playerData, "ショップ", 6);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (Config.UpScrollIcon.proximate(clickedItem)) {
                addPage();
                update();
            } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                removePage();
                update();
            } else if (slot < 45) {
                ShopData.ShopSlot shopSlot = shopData.getShopSlot(slot + offset());
                if (shopSlot != null) {
                    SomItem item = shopSlot.getItem();
                    int buyAmount = amount;
                    if (item instanceof SomEquipment) {
                        buyAmount = Math.min(10, buyAmount);
                    }
                    int mel = shopSlot.getMel() * buyAmount;
                    int amount = shopSlot.getAmount() * buyAmount;
                    boolean fall = false;
                    List<SomText> message = new ArrayList<>();
                    message.add(SomText.create(decoText("必要リスト")));
                    RecipeData recipeData = null;
                    if (playerData.getMel() >= mel) {
                        message.add(SomText.create("§7・§e" + mel + "メル§a✔"));
                        if (shopSlot.hasRecipe()) {
                            recipeData = shopSlot.getRecipe();
                            for (SomItemStack stack : recipeData.getRecipeSlot()) {
                                SomText itemText = SomText.create("§7・").add(stack.getItem().toSomText(stack.getAmount() * buyAmount));
                                if (playerData.getItemInventory().has(stack, buyAmount)) {
                                    message.add(itemText.add("§a✔"));
                                } else {
                                    message.add(itemText.add("§c✖"));
                                    fall = true;
                                }
                            }
                        }
                    } else {
                        message.add(SomText.create("§7・§e" + mel + "メル§c✖"));
                        fall = true;
                    }
                    if (fall) {
                        if (recipeData != null) {
                            String command = "/recipeInfo " + recipeData.getId();
                            message.add(SomText.create().addRunCommand("§e[" + command + "]", "§e" + command, command));
                        }
                        playerData.sendSomText(message, SomSound.Nope);
                    } else {
                        playerData.removeMel(mel);
                        if (shopSlot.hasRecipe())
                            for (SomItemStack stack : shopSlot.getRecipe().getRecipeSlot()) {
                                playerData.getItemInventory().remove(stack, buyAmount);
                            }
                        if (item instanceof SomEquipment equipment) {
                            for (int i = 0; i < amount; i++) {
                                equipment.randomQuality(0.4, 0.6);
                                playerData.getItemInventory().add(equipment.clone(), 1);
                            }
                            update();
                        } else {
                            playerData.getItemInventory().add(item, amount);
                        }
                        playerData.sendMessage(item.getColorDisplay() + (amount > 1 ? "§ex" + amount : "") + "§aを§b購入§aしました", SomSound.Tick);
                    }
                }
            } else {
                barClick(event);
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    public void open(ShopData shopData) {
        this.shopData = shopData;
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        if (shopData != null) {
            ItemStack[] contents = new ItemStack[54];
            int offset = offset();
            int slot = 0;
            for (int i = offset; i < offset + 45; i++) {
                ShopData.ShopSlot shopSlot = shopData.getShopSlot(i);
                if (shopSlot != null) {
                    contents[slot] = shopSlot.viewItem();
                }
                slot++;
            }
            setContents(contents);
            if (page > 0) setItem(45, Config.DownScrollIcon);
            if (shopData.getMaxPage() > page) setItem(53, Config.UpScrollIcon);
        }
        updateBar();
    }
}
