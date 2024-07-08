package SwordofMagic10.Player;

import SwordofMagic10.Command.Player.Trade;
import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Menu.EnhanceMenu;
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
import java.util.*;

import static SwordofMagic10.Component.Function.MinMax;
import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.SNCChannel;

public class Order extends GUIManager.Sub {
    private static final String order = "Order";
    private static final String orderEdit = "OrderEdit";
    private static final int max = 26;
    private int page = 0;

    public Order(PlayerData playerData) {
        super(playerData, "オーダー", 6, "オーダー管理", 3);
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
                if (SomSQL.existSql(order, priKey, priValue) && SomSQL.getString(order, priKey, priValue, "Auth").equals(auth)) {
                    int itemAmount = SomSQL.getInt(order, priKey, priValue, "Amount");
                    int amount = Math.min(itemAmount, this.amount);
                    int mel = CustomItemStack.getCustomDataInt(clickedItem, "Mel") * amount;
                    SomItem reqItem = viewList[event.getSlot()];
                    if (playerData.getItemInventory().req(reqItem, amount)) {
                        itemAmount -= amount;
                        Optional<SomItemStack> stack = playerData.getItemInventory().get(reqItem);
                        if (stack.isPresent()) {
                            SomItem item = stack.get().getItem();
                            String itemJson = item.toJson().toString();
                            int max = SomSQL.getMax(orderEdit, "UUID", uuid, "Slot");
                            for (int i = 0; i < max+1; i++) {
                                if (!SomSQL.existSql(orderEdit, priKey, priValue)) {
                                    SomSQL.setSql(orderEdit, priKey, priValue, "Item", itemJson);
                                }
                                if (SomSQL.getString(orderEdit, priKey, priValue, "Item").equals(itemJson)) {
                                    if (itemAmount <= 0) {
                                        SomSQL.delete(order, priKey, priValue);
                                    } else {
                                        SomSQL.removeNumber(order, priKey, priValue, "Amount", amount);
                                    }
                                    SomSQL.addNumber(orderEdit, priKey, priValue, "Amount", amount);
                                    playerData.getItemInventory().removeReq(reqItem, amount);
                                    playerData.addMel(mel);
                                    playerData.sendSomText(reqItem.toSomText(amount).add("§aを§e" + mel + "メル§aで§b売却§aしました"), SomSound.Tick);
                                    OrderLog(playerData, CustomItemStack.getCustomData(clickedItem, "OwnerName"), new SomItemStack(reqItem, amount).toJson().set("Mel", mel).toString());
                                    break;
                                }
                            }
                        } else {
                            playerData.sendMessage("§e注文内容§aを満たした§eアイテム§aを§e所持§aしていません", SomSound.Nope);
                        }
                    } else {
                        playerData.sendMessage("§e注文内容§aを満たした§eアイテム§aを§e所持§aしていません", SomSound.Nope);
                    }
                } else {
                    playerData.sendMessage("§aこの§e注文§aは§b更新§aされました", SomSound.Nope);
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
    public void open() {
        if (playerData.sendMessageIsInCity()) {
            super.open();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    private SomItem[] viewList = new SomItem[45];
    @Override
    public void update() {
        clear();
        viewList = new SomItem[45];
        int slot = 0;
        if (SomSQL.existSql(order, "UUID")) for (RowData objects : SomSQL.getSqlList(order, "*", "`Item` ASC, `mel` DESC, `Amount` DESC", page*45, 45)) {
            String jsonData = objects.getString("Item");
            try {
                int mel = objects.getInt("Mel");
                int amount = objects.getInt("Amount");
                SomItem somItem = SomItem.fromJson(new SomJson(jsonData));
                CustomItemStack item = somItem.viewItem();
                item.setAmount(amount);
                item.addSeparator("マーケット");
                item.addLore(decoLore("単価") + mel);
                item.addLore(decoLore("注文数") + amount);
                item.addLore(decoLore("注文者") + objects.getString("OwnerName"));
                item.addLore(decoLore("注文時間") + objects.getString("Time"));
                item.setCustomData("UUID", objects.getString("UUID"));
                item.setCustomData("OwnerName", objects.getString("OwnerName"));
                item.setCustomData("Slot", objects.getInt("Slot"));
                item.setCustomData("Auth", objects.getString("Auth"));
                item.setCustomData("Json", jsonData);
                item.setCustomData("Mel", mel);
                setItem(slot, item);
                viewList[slot] = somItem;
                slot++;
            } catch (Exception e) {
                e.printStackTrace();
                Log("§cアイテムの読み込みに失敗しました -> " + jsonData);
            }
        }
        updateBar();
        if (page > 0) setItem(45, Config.DownScrollIcon);
        if (Math.floor(SomSQL.getCount(order, "*")/45f) > page) setItem(53, Config.UpScrollIcon);
    }

    private static final String[] priKey = new String[]{"UUID", "Slot"};
    public String[] priValue(int slot) {
        return new String[]{playerData.getUUIDAsString(), String.valueOf(slot)};
    }
    public void command(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("open")) open();
            if (args[0].equalsIgnoreCase("edit")) subOpen();
        } else if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("entry")) {
                try {
                    if (ItemDataLoader.getComplete().contains(args[1])) {
                        SomItem item = ItemDataLoader.getItemData(args[1]);
                        if (args.length >= 5) item.setTier(MinMax(Integer.parseInt(args[4]), 1, 6));
                        if (args.length >= 6 && item instanceof SomQuality quality) quality.setLevel(MinMax(Integer.parseInt(args[5]), 1, Classes.MaxLevel));
                        if (args.length >= 7 && item instanceof SomPlus plus) plus.setPlus(MinMax(Integer.parseInt(args[6]), 0, EnhanceMenu.MaxPlus));
                        SomItemStack stack = new SomItemStack(item, amount);
                        int mel = MinMax(Integer.parseInt(args[2]), 1, Integer.MAX_VALUE);
                        int amount = MinMax(Integer.parseInt(args[3]), 1, 10000);
                        int totalMel = Math.multiplyExact(mel, amount);
                        int reqMel =  Math.max((int) (totalMel * playerData.getRank().getCommission()), 1);
                        if (playerData.getMel() < Math.addExact(totalMel, reqMel)) {
                            playerData.sendMessage("§c注文手数料§aが足りません", SomSound.Nope);
                            return;
                        }
                        try {
                            if (SomSQL.getMax(order, "UUID", playerData.getUUIDAsString(), "Slot") < max) {
                                for (int slot = 0; slot < max; slot++) {
                                    if (!SomSQL.existSql(order, priKey, priValue(slot))) {
                                        SomSQL.setSql(order, priKey, priValue(slot), "Item", stack.getItem().toJson().toString());
                                        SomSQL.setSql(order, priKey, priValue(slot), "Amount", amount);
                                        SomSQL.setSql(order, priKey, priValue(slot), "Mel", mel);
                                        SomSQL.setSql(order, priKey, priValue(slot), "OwnerName", player.getName());
                                        SomSQL.setSql(order, priKey, priValue(slot), "Time", LocalDateTime.now().format(Config.DateFormat));
                                        SomSQL.setSql(order, priKey, priValue(slot), "Auth", UUID.randomUUID().toString());
                                        playerData.removeMel(totalMel);
                                        playerData.removeMel(reqMel);
                                        playerData.sendSomText(stack.getItem().toSomText(amount).add("§aを§e単価" + mel + "メル§aで§b注文§aしました"), SomSound.Tick);
                                        playerData.sendMessage("§c[注文手数料] §e-" + reqMel + "メル");
                                        return;
                                    }
                                }
                            }
                            playerData.sendMessage("§c注文数上限§aです", SomSound.Nope);
                        } catch (Exception e) {
                            e.printStackTrace();
                            playerData.sendMessage("§cエラーが発生しました", SomSound.Nope);
                        }
                    } else {
                        playerData.sendMessage("§a存在しない§eアイテムID§aです", SomSound.Nope);
                    }
                    return;
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                playerData.sendMessage("§c無効§aな値です", SomSound.Nope);
            }
        } else {
            List<String> message = new ArrayList<>();
            message.add("§e/order open");
            message.add("§e/order edit");
            message.add("§e/order entry <ItemID> <単価> <注文個数> [<ティア>] [<レベル>] [<強化値>]");
            playerData.sendMessage(message, SomSound.Nope);
        }
    }

    public List<String> complete(String[] args) {
        if (args.length == 1) return List.of(new String[]{"open", "entry", "edit"});
        if (args[0].equalsIgnoreCase("entry")) {
            switch (args.length) {
                case 2 -> {
                    return ItemDataLoader.getComplete();
                }
                case 3 -> {
                    return Collections.singletonList("単価");
                }
                case 4 -> {
                    return Collections.singletonList("注文個数");
                }
                case 5 -> {
                    return Collections.singletonList("ティア");
                }
                case 6 -> {
                    SomItem item = ItemDataLoader.getItemData(args[1]);
                    if (item instanceof SomQuality) {
                        return Collections.singletonList("レベル");
                    } else return null;
                }
                case 7 -> {
                    SomItem item = ItemDataLoader.getItemData(args[1]);
                    if (item instanceof SomPlus) {
                        return Collections.singletonList("強化値");
                    } else return null;
                }
            }
        }
        return null;
    }

    @Override
    public void subTopClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "UUID")) {
                String uuid = CustomItemStack.getCustomData(clickedItem, "UUID");
                String slot = String.valueOf(CustomItemStack.getCustomDataInt(clickedItem, "Slot"));
                String auth = CustomItemStack.getCustomData(clickedItem, "Auth");
                String[] priValue = new String[]{uuid, slot};
                if (SomSQL.existSql(order, priKey, priValue) && SomSQL.getString(order, priKey, priValue, "Auth").equals(auth)) {
                    int amount = SomSQL.getInt(order, priKey, priValue, "Amount");
                    int mel = SomSQL.getInt(order, priKey, priValue, "Mel") * amount;
                    SomItem item = SomItem.fromJson(new SomJson(CustomItemStack.getCustomData(clickedItem, "Json")));
                    SomSQL.delete(order, priKey, priValue);
                    playerData.addMel(mel);
                    playerData.sendMessage(item.getColorDisplay() + (amount > 1 ? "§ex" + amount : "") + "§aを§c取消§aしました §e+" + mel + "メル", SomSound.Tick);
                } else {
                    playerData.sendMessage("§aこの§e注文§aは§b更新§aされました", SomSound.Nope);
                }
                subUpdate();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Item")) {
                if (SomSQL.existSql(orderEdit, "UUID", playerData.getUUIDAsString())) {
                    for (int i = 0; i < max; i++) {
                        if (SomSQL.existSql(orderEdit, priKey, priValue(i))) {
                            SomItem item = SomItem.fromJson(new SomJson(SomSQL.getString(orderEdit, priKey, priValue(i), "Item")));
                            int amount = SomSQL.getInt(orderEdit, priKey, priValue(i), "Amount");
                            playerData.getItemInventory().add(item, amount);
                        }
                    }
                    SomSQL.delete(orderEdit, "UUID", playerData.getUUIDAsString());
                    playerData.sendMessage("§b買取済アイテム§aを受け取りました", SomSound.Tick);
                    subUpdate();
                } else {
                    playerData.sendMessage("§b買取済アイテム§aがありません", SomSound.Nope);
                }
            }
        }
    }

    @Override
    public void subBottomClick(InventoryClickEvent event) {

    }

    @Override
    public void subUpdate() {
        subClear();
        int slot = 0;
        for (RowData objects : SomSQL.getSqlList(order, "UUID", playerData.getUUIDAsString(), "*")) {
            String jsonData = objects.getString("Item");
            int mel = objects.getInt("Mel");
            CustomItemStack item = SomItem.fromJson(new SomJson(jsonData)).viewItem();
            item.setAmount(objects.getInt("Amount"));
            item.addSeparator("マーケット");
            item.addLore(decoLore("単価") + mel);
            item.addLore(decoLore("注文者") + objects.getString("OwnerName"));
            item.addLore(decoLore("注文時間") + objects.getString("Time"));
            item.setCustomData("UUID", objects.getString("UUID"));
            item.setCustomData("Slot", objects.getInt("Slot"));
            item.setCustomData("Auth", objects.getString("Auth"));
            item.setCustomData("Json", jsonData);
            subSetItem(slot, item);
            slot++;
        }
        CustomItemStack chest = new CustomItemStack(Material.CHEST).setDisplay("買取済アイテム");
        for (int i = 0; i < max; i++) {
            if (SomSQL.existSql(orderEdit, priKey, priValue(i))) {
                SomItem item = SomItem.fromJson(new SomJson(SomSQL.getString(orderEdit, priKey, priValue(i), "Item")));
                int amount = SomSQL.getInt(orderEdit, priKey, priValue(i), "Amount");
                chest.addLore("§7・§r" + item.getColorDisplay() + "§ex" + amount);
            }
        }
        subSetItem(26, chest.setCustomData("Item", true));
    }

    public void OrderLog(PlayerData sender, String owner, String log) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Som10OrderLog");
            out.writeUTF(sender.getPlayer().getName() + " -> " + owner + ": " + log);
            sender.getPlayer().sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
