package SwordofMagic10.Component;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.time.format.DateTimeFormatter;

public interface Config {
    String ServiceStart = "2023/06/11 00:00:00";
    DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    String EnemyMetaAddress = "SomEnemy";
    String SomParticleMetaAddress = "SomParticle";
    File DataBase = new File("../DataBase");
    ItemStack AirItem = new ItemStack(Material.AIR);
    CustomItemStack FlameItem = new CustomItemStack(Material.IRON_BARS).setNonDecoDisplay(" ");
    CustomItemStack UpScrollIcon = new CustomItemStack(Material.ITEM_FRAME).setNonDecoDisplay("§e上にスクロール");
    CustomItemStack DownScrollIcon = new CustomItemStack(Material.ITEM_FRAME).setNonDecoDisplay("§e下にスクロール");
    CustomItemStack UserMenuIcon = new CustomItemStack(Material.BOOK).setNonDecoDisplay("§eユーザーメニュー");
    CustomItemStack UserMenuIcon_BE = new CustomItemStack(Material.BOOK).setNonDecoDisplay("§eユーザーメニュー[BE]").addLore("§c※BEは/menuを使用してください");

    static CustomItemStack NonePallet(int slot) {
        return new CustomItemStack(Material.IRON_BARS).setNonDecoDisplay("§7未設定スロット[" + (slot+1) + "]");
    }

    static int FixedSlot(int slot) {
        int next = slot;
        if (slot >= 8) next--;
        if (slot >= 17) next--;
        if (slot >= 26) next--;
        if (slot >= 35) next--;
        if (slot >= 44) next--;
        return next;
    }

    static String RomNum(int num) {
        String text = "0";
        switch (num) {
            case 1 -> text = "☆";
            case 2 -> text = "☆☆";
            case 3 -> text = "☆☆☆";
            case 4 -> text = "☆☆☆☆";
            case 5 -> text = "★";
            case 6 -> text = "★☆";
            case 7 -> text = "★☆☆";
            case 8 -> text = "★☆☆☆";
            case 9 -> text = "★☆☆☆☆";
            case 10 -> text = "★★";
        }
        return text;
    }

    static boolean isRightSlot(int slot) {
        return Math.floorMod(slot+1, 9) != 0;
    }
}
