package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.Shop.ShopData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.SomCore.Log;


public class ShopDataLoader {

    private static final HashMap<String, ShopData> shopDataList = new HashMap<>();
    public static ShopData getShopData(String id) {
        if (!shopDataList.containsKey(id)) Log("§c存在しないShopが参照されました -> " + id, true);
        return shopDataList.get(id);
    }
    public static Collection<ShopData> getShopDataList() {
        return shopDataList.values();
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (ShopData shopData : getShopDataList()) {
            complete.add(shopData.getId());
        }
        return complete;
    }
    public static void load() {
        shopDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "ShopData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                ShopData shopData = new ShopData();
                shopData.setId(id);
                shopData.setDisplay(data.getString("Display"));
                int slot = 0;
                for (String str : data.getStringList("ShopSlot")) {
                    ShopData.ShopSlot shopSlot = new ShopData.ShopSlot();
                    String[] split = str.split(",");
                    SomItemStack stack = ItemDataLoader.fromText(str);
                    shopSlot.setItem(stack.getItem());
                    shopSlot.setAmount(stack.getAmount());
                    int inputSlot = 0;
                    for (int i = 1; i < split.length; i++) {
                        String[] tmp = split[i].split(":");
                        String key = tmp[0];
                        String value = tmp[1];
                        switch (key) {
                            case "Mel" -> shopSlot.setMel(Integer.parseInt(value));
                            case "Slot" -> inputSlot = Integer.parseInt(value);
                            case "Recipe" -> shopSlot.setRecipe(RecipeDataLoader.getRecipeData(value));
                        }
                    }
                    if (inputSlot == -1) {
                        slot = (int) (Math.ceil(slot/9.0)*9);
                    } else if (inputSlot != 0) {
                        slot = inputSlot;
                    }
                    shopData.setShopSlot(slot, shopSlot);
                    slot++;
                }
                shopData.setMaxPage((int) Math.ceil(slot/45d));
                shopDataList.put(id, shopData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        for (DungeonInstance dungeonInstance : DungeonDataLoader.getDungeonList()) {
            if (!dungeonInstance.isLegendRaid()) {
                String id = dungeonInstance.getId();
                ShopData shopData = new ShopData();
                shopData.setId(id);
                shopData.setDisplay(dungeonInstance.getDisplay() + "の装備");
                MapData mapData = MapDataLoader.getMapData(id);
                int slot = 0;
                for (String series : dungeonInstance.getSeries()) {
                    //int slot = index * 9;
                    for (SomEquipment baseEquip : ItemDataLoader.getSeries(series)) {
                        SomEquipment equipment = baseEquip.clone();
                        ShopData.ShopSlot shopSlot = new ShopData.ShopSlot();
                        equipment.setLevel(mapData.getMaxLevel());
                        shopSlot.setItem(equipment);
                        shopSlot.setMel(100);
                        shopSlot.setRecipe(RecipeDataLoader.getRecipeData(id));
                        shopData.setShopSlot(slot, shopSlot);
                        slot++;
                    }
                    //index++;
                }
                shopDataList.put(id, shopData);
            }
        }
        Log("§a[ShopDataLoader]§b" + shopDataList.size() + "個をロードしました");
    }
}
