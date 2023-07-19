package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

import static SwordofMagic10.Component.Function.loreText;
import static SwordofMagic10.SomCore.Log;

public class ItemDataLoader {


    private static final HashMap<String, SomItem> itemDataList = new HashMap<>();
    private static final HashMap<String, List<SomEquipment>> seriesDataList = new HashMap<>();
    private static final List<SomItem> list = new ArrayList<>();

    static {
        SomItem item = new SomItem();
        item.setId("NotFoundItemData");
        item.setDisplay("§cNotFoundItemData");
        item.setLore(Collections.singletonList("§cエラーアイテム"));
        item.setIcon(Material.BARRIER);
        itemDataList.put(item.getId(), item);
    };

    public static SomItem getItemData(String id) {
        if (!itemDataList.containsKey(id)) {
            Log("§c存在しないItemが参照されました -> " + id, true);
            return itemDataList.get("NotFoundItemData");
        }
        return itemDataList.get(id).clone();
    }

    public static List<SomItem> getItemDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (SomItem item : getItemDataList()) {
            complete.add(item.getId());
        }
        return complete;
    }
    public static List<SomEquipment> getSeries(String series) {
        if (!seriesDataList.containsKey(series)) {
            Log("§c存在しないSeriesが参照されました -> " + series, true);
            return new ArrayList<>();
        }
        List<SomEquipment> list = new ArrayList<>();
        for (SomEquipment equipment : seriesDataList.get(series)) {
            list.add(equipment.clone());
        }
        return list;
    }


    private static final HashMap<StatusType, Double> statusWeight = new HashMap<>() {{
        put(StatusType.MaxHealth, 25.0);
        put(StatusType.MaxMana, 25.0);
        put(StatusType.HealthRegen, 0.1);
        put(StatusType.ManaRegen, 0.1);
        put(StatusType.ATK, 3.0);
        put(StatusType.MAT, 3.0);
        put(StatusType.DEF, 3.0);
        put(StatusType.MDF, 3.0);
        put(StatusType.SPT, 3.0);
        put(StatusType.Critical, 1.5);
        put(StatusType.CriticalDamage, 1.0);
        put(StatusType.CriticalResist, 1.5);
    }};

    public static void load() {
        itemDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "ItemData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                SomItem item = new SomItem();
                SomItem.ItemCategory category = SomItem.ItemCategory.valueOf(data.getString("Category", "None"));
                switch (category) {
                    case Equipment -> {
                        SomEquipment equipment = new SomEquipment();
                        equipment.setLevel(data.getInt("Level", 1));
                        equipment.setEquipmentCategory(EquipmentCategory.valueOf(data.getString("EquipmentCategory")));
                        statusWeight.forEach((statusType, multiply) -> {
                            if (data.isSet(statusType.toString())) equipment.setStatus(statusType, data.getDouble(statusType.toString()) * multiply);
                        });
                        item = equipment;
                        if (data.isSet("Series")) {
                            String series = data.getString("Series");
                            if (!seriesDataList.containsKey(series)) seriesDataList.put(series, new ArrayList<>());
                            seriesDataList.get(series).add(equipment);
                        }
                    }
                    case Tool -> {
                        SomTool tool = new SomTool();
                        tool.setType(SomTool.Type.valueOf(data.getString("ToolType")));
                        item = tool;
                    }
                    case Cook -> {
                        SomCook cook = new SomCook();
                        for (StatusType statusType : StatusType.BaseStatus()) {
                            if (data.isSet("Fixed." + statusType)) cook.setFixed(statusType, data.getDouble("Fixed." + statusType));
                            if (data.isSet("Multiply." + statusType)) cook.setMultiply(statusType, data.getDouble("Multiply." + statusType));
                        }
                        item = cook;
                    }
                    case Sell -> item.setSell(3);

                }
                item.setId(id);
                item.setDisplay(data.getString("Display"));
                item.setIcon(Material.valueOf(data.getString("Icon")));
                if (data.isSet("Color.r")) {
                    item.setColor(Color.fromRGB(data.getInt("Color.r"), data.getInt("Color.g"), data.getInt("Color.b")));
                }
                if (data.isSet("Pattern")) {
                    for (String str : data.getStringList("Pattern")) {
                        String[] split = str.split(":");
                        item.addPattern(new Pattern(DyeColor.valueOf(split[0]), PatternType.valueOf(split[1])));
                    }
                }
                item.setTier(data.getInt("Tier", 1));
                item.setItemCategory(category);
                item.setLore(data.isSet("Lore") ? loreText(data.getStringList("Lore")) : Collections.singletonList("§a" + item.getDisplay() + "です"));
                if (data.isSet("Sell")) item.setSell(data.getInt("Sell"));
                itemDataList.put(id, item);
                list.add(item);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        for (StatusType statusType : SomRune.Table) {
            SomRune rune = new SomRune();
            rune.setId("RuneOf" + statusType);
            rune.setDisplay(statusType.getDisplay() + "のルーン");
            rune.setItemCategory(SomItem.ItemCategory.Rune);
            rune.setLore(statusType.getDisplay() + "が上昇するルーンです");
            switch (statusType) {
                case MaxHealth -> {
                    rune.setIcon(Material.HEART_POTTERY_SHERD);
                    rune.setStatus(statusType, 10);
                }
                case MaxMana -> {
                    rune.setIcon(Material.BREWER_POTTERY_SHERD);
                    rune.setStatus(statusType, 10);
                }
                case HealthRegen -> {
                    rune.setIcon(Material.SHEAF_POTTERY_SHERD);
                    rune.setStatus(statusType, 0.2);
                }
                case ManaRegen -> {
                    rune.setIcon(Material.SHELTER_POTTERY_SHERD);
                    rune.setStatus(statusType, 0.2);
                }
                case ATK -> {
                    rune.setIcon(Material.BLADE_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case MAT -> {
                    rune.setIcon(Material.BURN_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case DEF -> {
                    rune.setIcon(Material.PRIZE_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case MDF -> {
                    rune.setIcon(Material.SKULL_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case SPT -> {
                    rune.setIcon(Material.FRIEND_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case Critical -> {
                    rune.setIcon(Material.ARMS_UP_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
                case CriticalDamage -> {
                    rune.setIcon(Material.DANGER_POTTERY_SHERD);
                    rune.setStatus(statusType, 8);
                }
                case CriticalResist -> {
                    rune.setIcon(Material.MOURNER_POTTERY_SHERD);
                    rune.setStatus(statusType, 5);
                }
            }
            rune.setSell(100);
            itemDataList.put(rune.getId(), rune);
            list.add(rune);
        }

        SomWorker worker = new SomWorker();
        worker.setId("SomWorker");
        worker.setDisplay("労働者の権利書");
        worker.setLore("労働者の権利書です");
        worker.setIcon(Material.MAP);
        worker.setItemCategory(SomItem.ItemCategory.Worker);
        worker.setSell(10000);
        itemDataList.put(worker.getId(), worker);
        list.add(worker);

        list.sort(Comparator.comparing(SomItem::getId));
        Log("§a[ItemDataLoader]§b" + itemDataList.size() + "+" + seriesDataList.size() +"個をロードしました");
    }

    public static SomItemStack fromText(String text) {
        String[] split = text.split(",");
        String itemId = split[0];
        SomItem item = ItemDataLoader.getItemData(itemId);
        int amount = 1;
        for (String str : split) {
            String[] split2 = str.split(":");
            switch (split2[0]) {
                case "Amount" -> amount = Integer.parseInt(split2[1]);
                case "Tier" -> item.setTier(Integer.parseInt(split2[1]));
                case "Level" -> {
                    if (item instanceof SomQuality quality) {
                        quality.setLevel(Integer.parseInt(split2[1]));
                    }
                }
                case "Quality" -> {
                    if (item instanceof SomQuality quality) {
                        quality.setQuality(Double.parseDouble(split2[1]));
                    }
                }
            }
        }
        return new SomItemStack(item, amount);
    }
}
