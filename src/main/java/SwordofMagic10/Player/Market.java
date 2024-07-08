package SwordofMagic10.Player;

import SwordofMagic10.Command.Player.Trade;
import SwordofMagic10.Component.*;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.SomCore;
import com.github.jasync.sql.db.RowData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static SwordofMagic10.Component.Function.MinMax;
import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.SNCChannel;

public class Market extends GUIManager {
    private static final String market = "Market";
    private static final String marketEdit = "MarketEdit";
    private static final int max = 26;
    protected SomItem.ItemCategory category = null;
    private final MarketEdit edit;
    private final MarketView view;

    public Market(PlayerData playerData) {
        super(playerData, "マーケット", 5);
        edit = new MarketEdit(playerData);
        view = new MarketView(playerData, this);
        if (!SomSQL.existSql(marketEdit, "UUID", playerData.getUUIDAsString())) {
            SomSQL.setSql(marketEdit, "UUID", playerData.getUUIDAsString(), "Mel", 0);
        }
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "MarketView")) {
                category = SomItem.ItemCategory.valueOf(CustomItemStack.getCustomData(clickedItem, "MarketView"));
                view.open();
            }
            if (CustomItemStack.hasCustomData(clickedItem, "MarketViewAll")) {
                category = null;
                view.open();
            }
            if (CustomItemStack.hasCustomData(clickedItem, "MarketEdit")) {
                edit.open();
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
        if (playerData.sendMessageIsInCity()) {
            super.open();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        setItem(0, new CustomItemStack(Material.CRAFTING_TABLE).setDisplay("マーケット管理").addLore("§aマーケット管理を開きます").setCustomData("MarketEdit", true));
        setItem(1, new CustomItemStack(Material.SUNFLOWER).setDisplay("マーケット販売").addLore("§aコマンドを使用してアイテムを販売します").addSeparator("コマンド").addLore("§e/market sell <スロット番号> <単価> [<個数>]"));
        setItem(8, new CustomItemStack(Material.CHEST).setDisplay("すべて表示").addLore("§aカテゴリ分けなしでマーケットを開きます").setCustomData("MarketViewAll", true));
        int slot = 9;
        for (int i = 0; i < 9; i++) {
            setItem(slot, Config.FlameItem);
            slot++;
        }
        for (SomItem.ItemCategory itemCategory : SomItem.ItemCategory.values()) {
            setItem(slot, itemCategory.viewItem().addLore("§e" + itemCategory.getDisplay() + "§aでマーケットを開きます").setCustomData("MarketView", itemCategory.toString()));
            slot++;
        }
    }

    protected static final String[] priKey = new String[]{"UUID", "Slot"};
    protected String[] priValue(int slot) {
        return new String[]{playerData.getUUIDAsString(), String.valueOf(slot)};
    }
    public void command(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("open")) open();
            if (args[0].equalsIgnoreCase("edit")) edit.open();
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("sell")) {
                try {
                    int index = Integer.parseInt(args[1].split(":")[0]);
                    if (playerData.getItemInventory().size() > index) {
                        SomItemStack stack = playerData.getItemInventory().getInventory().get(index);
                        if (stack.getItem().isFavorite()) {
                            playerData.sendIsFavorite();
                            return;
                        }
                        int mel = MinMax(Integer.parseInt(args[2]), 1, Integer.MAX_VALUE);
                        int amount = args.length >= 4 ? MinMax(Integer.parseInt(args[3]), 1, stack.getAmount()) : stack.getAmount();
                        int totalMel = Math.multiplyExact(mel, amount);
                        int reqMel = Math.max((int) (totalMel * playerData.getRank().getCommission()), 1);
                        if (playerData.getMel() < reqMel) {
                            playerData.sendMessage("§c出品手数料§aが足りません", SomSound.Nope);
                            return;
                        }
                        try {
                            if (SomSQL.getMax(market, "UUID", playerData.getUUIDAsString(), "Slot") < max) {
                                for (int slot = 0; slot < max; slot++) {
                                    if (!SomSQL.existSql(market, priKey, priValue(slot))) {
                                        playerData.getItemInventory().remove(stack.getItem(), amount);
                                        SomSQL.setSql(market, priKey, priValue(slot), "Item", stack.getItem().toJson().toString());
                                        SomSQL.setSql(market, priKey, priValue(slot), "Amount", amount);
                                        SomSQL.setSql(market, priKey, priValue(slot), "Mel", mel);
                                        SomSQL.setSql(market, priKey, priValue(slot), "OwnerName", player.getName());
                                        SomSQL.setSql(market, priKey, priValue(slot), "Time", LocalDateTime.now().format(Config.DateFormat));
                                        SomSQL.setSql(market, priKey, priValue(slot), "Auth", UUID.randomUUID().toString());
                                        playerData.removeMel(reqMel);
                                        playerData.sendSomText(stack.getItem().toSomText(amount).add("§aを§e単価" + mel + "メル§aで§b出品§aしました"), SomSound.Tick);
                                        playerData.sendMessage("§c[出品手数料] §e-" + reqMel + "メル");
                                        return;
                                    }
                                }
                            }
                            playerData.sendMessage("§c出品数上限§aです", SomSound.Nope);
                        } catch (Exception e) {
                            e.printStackTrace();
                            playerData.sendMessage("§cエラーが発生しました", SomSound.Nope);
                        }
                        return;
                    }
                } catch (Exception ignored) {}
                playerData.sendMessage("§c無効§aな値です", SomSound.Nope);
            }
        } else {
            List<String> message = new ArrayList<>();
            message.add("§e/market open");
            message.add("§e/market edit");
            message.add("§e/market sell <スロット番号> <単価> [<個数>]");
            playerData.sendMessage(message, SomSound.Nope);
        }
    }

    public List<String> complete(String[] args) {
        if (args.length == 1) return List.of(new String[]{"open", "sell", "edit"});
        if (args[0].equalsIgnoreCase("sell")) {
            switch (args.length) {
                case 2 -> {
                    return Trade.invComplete(playerData);
                }
                case 3 -> {
                    return Collections.singletonList("単価");
                }
                case 4 -> {
                    return Collections.singletonList("個数");
                }
            }
        }
        return null;
    }

    public static class MarketEdit extends GUIManager {

        public MarketEdit(PlayerData playerData) {
            super(playerData, "マーケット管理", 3);
        }

        @Override
        public void topClick(InventoryClickEvent event) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                if (CustomItemStack.hasCustomData(clickedItem, "UUID")) {
                    String uuid = CustomItemStack.getCustomData(clickedItem, "UUID");
                    String slot = String.valueOf(CustomItemStack.getCustomDataInt(clickedItem, "Slot"));
                    String auth = CustomItemStack.getCustomData(clickedItem, "Auth");
                    String[] priValue = new String[]{uuid, slot};
                    if (SomSQL.existSql(market, priKey, priValue) && SomSQL.getString(market, priKey, priValue, "Auth").equals(auth)) {
                        int amount = SomSQL.getInt(market, priKey, priValue, "Amount");
                        SomItem item = SomItem.fromJson(new SomJson(CustomItemStack.getCustomData(clickedItem, "Json")));
                        SomSQL.delete(market, priKey, priValue);
                        playerData.getItemInventory().add(item, amount);
                        playerData.sendMessage(item.getColorDisplay() + (amount > 1 ? "§ex" + amount : "") + "§aを§c取消§aしました", SomSound.Tick);
                    } else {
                        playerData.sendMessage("§aこの§e出品§aは§b更新§aされました", SomSound.Nope);
                    }
                    update();
                } else if (CustomItemStack.hasCustomData(clickedItem, "Gold")) {
                    int mel = SomSQL.getInt(marketEdit, "UUID", playerData.getUUIDAsString(), "Mel");
                    if (mel > 0) {
                        SomSQL.setSql(marketEdit, "UUID", playerData.getUUIDAsString(), "Mel", 0);
                        playerData.addMel(mel);
                        playerData.sendMessage("§b売上§aを受け取りました §e+" + mel + "メル", SomSound.Level);
                        update();
                    } else {
                        playerData.sendMessage("§b売上§aがありません", SomSound.Nope);
                    }
                }
            }
        }

        @Override
        public void bottomClick(InventoryClickEvent event) {

        }

        @Override
        public void close(InventoryCloseEvent event) {

        }

        @Override
        public void update() {
            clear();
            int slot = 0;
            for (RowData objects : SomSQL.getSqlList(market, "UUID", playerData.getUUIDAsString(), "*")) {
                String jsonData = objects.getString("Item");
                int mel = objects.getInt("Mel");
                CustomItemStack item = SomItem.fromJson(new SomJson(jsonData)).viewItem();
                item.setAmount(objects.getInt("Amount"));
                item.addSeparator("マーケット");
                item.addLore(decoLore("単価") + mel);
                item.addLore(decoLore("出品者") + objects.getString("OwnerName"));
                item.addLore(decoLore("出品時間") + objects.getString("Time"));
                item.setCustomData("UUID", objects.getString("UUID"));
                item.setCustomData("Slot", objects.getInt("Slot"));
                item.setCustomData("Auth", objects.getString("Auth"));
                item.setCustomData("Json", jsonData);
                setItem(slot, item);
                slot++;
            }
            int mel = SomSQL.getInt(marketEdit, "UUID", playerData.getUUIDAsString(), "Mel");
            CustomItemStack gold = new CustomItemStack(Material.GOLD_NUGGET).setDisplay("売上");
            gold.addLore(decoLore("売上メル") + mel);
            setItem(26, gold.setCustomData("Gold", true));
        }
    }

    public static class MarketView extends GUIManager.Bar {
        private final Market market;
        private int page = 0;
        public MarketView(PlayerData playerData, Market market) {
            super(playerData, "マーケット", 6);
            this.market = market;
        }

        public void addPage() {
            page++;
        }

        public void removePage() {
            page = Math.max(0, page - 1);
        }

        @Override
        public void topClick(InventoryClickEvent event) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                if (Config.UpScrollIcon.proximate(clickedItem)) {
                    addPage();
                    update();
                } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                    removePage();
                    update();
                } else if (CustomItemStack.hasCustomData(clickedItem, "UUID")) {
                    String uuid = CustomItemStack.getCustomData(clickedItem, "UUID");
                    String slot = String.valueOf(CustomItemStack.getCustomDataInt(clickedItem, "Slot"));
                    String auth = CustomItemStack.getCustomData(clickedItem, "Auth");
                    String[] priValue = new String[]{uuid, slot};
                    if (SomSQL.existSql(Market.market, priKey, priValue) && SomSQL.getString(Market.market, priKey, priValue, "Auth").equals(auth)) {
                        int mel = CustomItemStack.getCustomDataInt(clickedItem, "Mel");
                        int itemAmount = SomSQL.getInt(Market.market, priKey, priValue, "Amount");
                        int amount = Math.min(itemAmount, this.amount);
                        if (playerData.getMel() >= mel * amount) {
                            itemAmount -= amount;
                            SomItem item = SomItem.fromJson(new SomJson(CustomItemStack.getCustomData(clickedItem, "Json")));
                            SomSQL.addNumber(marketEdit, "UUID", uuid, "Mel", mel * amount);
                            if (itemAmount <= 0) {
                                SomSQL.delete(Market.market, priKey, priValue);
                            } else {
                                SomSQL.removeNumber(Market.market, priKey, priValue, "Amount", amount);
                            }
                            playerData.removeMel(mel * amount);
                            playerData.getItemInventory().add(item, amount);
                            playerData.sendMessage(item.getColorDisplay() + (amount > 1 ? "§ex" + amount : "") + "§aを§b購入§aしました", SomSound.Tick);
                            MarketLog(playerData, CustomItemStack.getCustomData(clickedItem, "OwnerName"), new SomItemStack(item, amount).toJson().set("Mel", mel * amount).toString());
                        } else playerData.sendMessageNonMel();
                    } else {
                        playerData.sendMessage("§aこの§e出品§aは§b更新§aされました", SomSound.Nope);
                    }
                    update();
                }
                barClick(event);
            }
        }

        @Override
        public void bottomClick(InventoryClickEvent event) {

        }

        @Override
        public void close(InventoryCloseEvent event) {

        }

        @Override
        public void update() {
            clear();
            int index = 0;
            int slot = 0;
            if (SomSQL.existSql(Market.market, "UUID")) for (RowData objects : SomSQL.getSqlList(Market.market, "*", "`Item` ASC, `mel` ASC, `Amount` DESC")) {
                String jsonData = objects.getString("Item");
                SomItem itemData = SomItem.fromJson(new SomJson(jsonData));
                CustomItemStack item = itemData.viewItem();
                if (itemData.getItemCategory() == market.category || market.category == null) {
                    index++;
                    if (index > page*45) {
                        int mel = objects.getInt("Mel");
                        int amount = objects.getInt("Amount");
                        item.setAmount(amount);
                        item.addSeparator("マーケット");
                        item.addLore(decoLore("単価") + mel);
                        item.addLore(decoLore("出品数") + amount);
                        item.addLore(decoLore("出品者") + objects.getString("OwnerName"));
                        item.addLore(decoLore("出品時間") + objects.getString("Time"));
                        item.setCustomData("UUID", objects.getString("UUID"));
                        item.setCustomData("OwnerName", objects.getString("OwnerName"));
                        item.setCustomData("Slot", objects.getInt("Slot"));
                        item.setCustomData("Auth", objects.getString("Auth"));
                        item.setCustomData("Json", jsonData);
                        item.setCustomData("Mel", mel);
                        setItem(slot, item);
                        slot++;
                        if (slot > 45) break;
                    }
                }
            }
            updateBar();
            if (page > 0) setItem(45, Config.DownScrollIcon);
            if (slot > 45) setItem(53, Config.UpScrollIcon);
        }
    }

    public static void MarketLog(PlayerData buyer, String owner, String log) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Som10MarketLog");
            out.writeUTF(owner + " -> " + buyer.getPlayer().getName() + ": " + log);
            buyer.getPlayer().sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
