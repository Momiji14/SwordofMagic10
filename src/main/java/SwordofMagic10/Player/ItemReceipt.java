package SwordofMagic10.Player;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;

import java.util.UUID;

public class ItemReceipt {
    private static final String ItemReceipt = "PlayerItemReceipt";
    public static void itemReceipt(PlayerData playerData) {
        if (SomSQL.existSql(ItemReceipt, "UUID", playerData.getUUIDAsString())) {
            SomJson json = new SomJson(SomSQL.getString(ItemReceipt, "UUID", playerData.getUUIDAsString(), "Receipt"));
            playerData.sendMessage("§a以下の§eアイテム§aを受け取りました", SomSound.Level);
            for (String data : json.getList("Receipt")) {
                SomItemStack stack = SomItemStack.fromJson(data);
                playerData.getItemInventory().add(stack);
                playerData.sendSomText(SomText.create("§7・§r").add(stack.toSomText()));
            }
            SomSQL.delete(ItemReceipt, "UUID", playerData.getUUIDAsString());
        } else {
            playerData.sendMessage("§e受取アイテム§aはありません", SomSound.Nope);
        }
    }
    public static void addItem(String uuid, SomItem item, int amount) {
        addItem(uuid, new SomItemStack(item, amount));
    }
    public static void addItem(String uuid, SomItemStack stack) {
        SomJson json;
        if (SomSQL.existSql(ItemReceipt, "UUID", uuid)) {
            json = new SomJson(SomSQL.getString(ItemReceipt, "UUID", uuid, "Receipt"));
        } else {
            json = new SomJson();
        }
        json.addArray("Receipt", stack.toJson());
        SomSQL.setSql(ItemReceipt, "UUID", uuid, "Receipt", json.toString());
    }
}
