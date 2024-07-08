package SwordofMagic10.Component;

import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {

    private static final SecureRandom random = new SecureRandom();

    public static boolean randomBool() {
        return random.nextBoolean();
    }
    public static int randomInt(int min, int max) {
        return random.nextInt(min, max);
    }
    public static double randomDouble(double min, double max) {
        return random.nextDouble(min, max);
    }

    public static double randomDoubleSign(double min, double max) {
        return (random.nextBoolean() ? -1 : 1) * random.nextDouble(min, max);
    }

    //アイテム名などに使うテキストデコレーション
    public static String decoText(String str) {
        return decoText(str, 8);
    }
    public static String decoText(String str, int flames) {
        if (str == null) return "null";
        int flame = flames - Math.round(uncolored(str).length() / 1.5f);
        StringBuilder deco = new StringBuilder("===");
        deco.append("=".repeat(Math.max(0, flame)));
        return "§6" + deco + "§r " + colored(str, "§e") + "§r §6" + deco;
    }

    //アイテムのLoreなどに使うテキストデコレーション
    public static String decoLore(String str) {
        return "§7・" + colored(str, "§e") + "§7: §a";
    }

    //アイテムのLoreの分割部などに使うテキストデコレーション
    public static String decoSeparator(String str) {
        return decoText("§3" + str);
    }
    public static String decoSeparator(String str, int flames) {
        return decoText("§3" + str, flames);
    }


    //Lore読み込みのデコレーション
    public static List<String> loreText(List<String> list) {
        List<String> lore = new ArrayList<>();
        for (String str : list) {
            for (StatusType value : StatusType.values()) {
                str = str.replace("%" + value.toString() + "%", value.getDisplay());
            }
            lore.add("§a" + str);
        }
        return lore;
    }

    //テキストデコレーションを削除
    public static String unDecoText(String str) {
        return str
                .replace("§6=", "")
                .replace("=", "")
                .replace("§r ", "");
    }

    //カラーを有効化
    public static String colored(String str, String def) {
        if (str.contains("&")) {
            return str.replace("&", "§");
        } else {
            return def.replace("&", "§") + str;
        }
    }

    //カラーを削除
    public static String uncolored(String string) {
        return string
                .replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§l", "")
                .replace("§m", "")
                .replace("§n", "")
                .replace("§k", "")
                .replace("§r", "")
                ;
    }

    //ChestInventoryの簡易作成
    public static Inventory decoInv(String name, int size) {
        Inventory inv = Bukkit.createInventory(null, size*9, name);
        inv.setMaxStackSize(127);
        return inv;
    }

    //ItemStackの簡易作成
    public static ItemStack createIcon(Material material, String display, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(decoText(display));
        meta.setLore(lore);
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createIcon(Material material, String display, String lore) {
        return createIcon(material, display, Collections.singletonList("§a" + lore));
    }

    public static ItemStack createIcon(Material material, String display) {
        return createIcon(material, display, new ArrayList<>());
    }

    public static String scale(double value, int index) {
        return scale(value, index, false);
    }

    public static String scale(double value, boolean prefix) {
        return scale(value, 0, prefix);
    }

    public static String scale(double value) {
        return scale(value, 0);
    }

    public static String scale(double value, int index, boolean prefix) {
        String prefixText = "";
        String valueText = index > -1 ? String.format("%." + index + "f", value) : String.valueOf(scaleDouble(value));
        if (prefix) {
            if (value == 0) prefixText = "±";
            else if (value > 0) prefixText = "+";
        }
        return prefixText + valueText;
    }

    public static double scaleDouble(double value) {
        return scaleDouble(value, 2);
    }

    public static double scaleDouble(double value, int index) {
        return Double.parseDouble(scale(value, index));
    }

    public static void broadcast(String message) {
        broadcast(message, null);
    }

    public static void broadcast(String message, SomSound somSound) {
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            playerData.sendMessage(message, somSound);
        }
    }

    public static void broadcast(SomText text) {
        broadcast(text, null);
    }

    public static void broadcast(SomText text, SomSound somSound) {
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            playerData.sendSomText(text, somSound);
        }
    }

    public static Vector VectorFromYawPitch(float yawData, float pitchData) {
        double pitch = (pitchData + 90) * Math.PI / 180;
        double yaw = (yawData + 90) * Math.PI / 180;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);
        return new Vector(x, z, y);
        //return new Location(Config.Alaine, 0, 0, 0, yawData, pitchData).getDirection();
    }

    public static int MinMax(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double MinMax(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float MinMax(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Vector VectorFromTo(Location to, Location from, double multiply) {
        Vector vector = VectorFromTo(to, from).normalize();
        try {
            vector.checkFinite();
        } catch (IllegalArgumentException e) {
            vector = from.getDirection();
        }
        vector.multiply(multiply);
        return vector;
    }

    public static Vector VectorFromTo(Location to, Location from, double multiply, double offsetY) {
        Vector vector = VectorFromTo(to, from).normalize();
        try {
            vector.checkFinite();
        } catch (IllegalArgumentException e) {
            vector = from.getDirection();
        }
        vector.multiply(multiply).setY(offsetY);
        return vector;
    }
    public static Vector VectorFromTo(Location to, Location from) {
        return VectorFromTo(to, from, false);
    }
    public static Vector VectorFromTo(Location to, Location from, boolean horizontal) {
        Vector vector = to.toVector().subtract(from.toVector());
        try {
            vector.checkFinite();
        } catch (IllegalArgumentException e) {
            vector = from.getDirection();
        }
        if (horizontal) vector.setY(0);
        return vector;
    }

    public static int angle(Vector vector) {
        return angle(new Vector(), vector);
    }

    public static int angle(Vector vector, Vector vector2) {
        double angle = Math.atan2(vector.getZ() - vector2.getZ(), vector.getX() - vector2.getX());
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return (int) Math.floor(angle * 360 / (2 * Math.PI));
    }

    public static CustomLocation createLocation(double x, double y, double z, float yaw, float pitch) {
        return new CustomLocation(SomCore.World, x + (x >= 0 ? 0.5 : -0.5), y, z + (z >= 0 ? 0.5 : -0.5), yaw, pitch);
    }

    public static String BoolText(boolean bool) {
        return bool ? "§b有効" : "§c無効";
    }

    public static String hexColor(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, ChatColor.of(color) + "");
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<?> ReSize(List<?> list, int size) {
        while (list.size() > size) {
            list.remove(0);
        }
        return list;
    }
}
