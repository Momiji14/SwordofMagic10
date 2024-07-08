package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Gathering.ProduceData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.DataBase.ItemDataLoader.SellMultiply;
import static SwordofMagic10.SomCore.Log;

public class ProduceDataLoader {


    private static final HashMap<String, ProduceData> produceDataList = new HashMap<>();
    private static final List<ProduceData> list = new ArrayList<>();

    public static ProduceData getProduceData(String id) {
        if (!produceDataList.containsKey(id)) {
            Log("§c存在しないProduceDataが参照されました -> " + id);
            throw new RuntimeException("§c存在しないProduceDataが参照されました -> " + id);
        }
        return produceDataList.get(id);
    }

    public static List<ProduceData> getProduceDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (ProduceData processingData : getProduceDataList()) {
            complete.add(processingData.getId());
        }
        return complete;
    }

    public static void load() {
        produceDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "ProduceData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                data.set("Exp", null);
                data.save(file);
                if (data.isSet("Prefix")) {
                    for (String prefix : data.getStringList("Prefix")) {
                        ProduceData produceData = new ProduceData();
                        produceData.setId(data.getString("ProduceID").replace("%Prefix%", prefix));
                        produceData.setDisplay(data.getString("Display").replace("%Prefix%", prefix));
                        produceData.setStack(ItemDataLoader.fromText(data.getString("Item").replace("%Prefix%", prefix)));
                        produceData.setCost(data.getInt("Cost"));
                        produceData.setRecipe(RecipeDataLoader.getRecipeData(data.getString("Recipe").replace("%Prefix%", prefix)));
                        produceDataList.put(produceData.getId(), produceData);
                        list.add(produceData);
                    }
                } else {
                    String id = SomLoader.fileId(file);
                    ProduceData produceData = new ProduceData();
                    produceData.setId(id);
                    produceData.setDisplay(data.getString("Display"));
                    produceData.setStack(ItemDataLoader.fromText(data.getString("Item")));
                    produceData.setCost(data.getInt("Cost"));
                    produceData.setRecipe(RecipeDataLoader.getRecipeData(data.getString("Recipe")));
                    produceDataList.put(id, produceData);
                    list.add(produceData);
                }
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }
        list.sort((produce1, produce2) -> {
            SomItem item1 = produce1.getItem();
            SomItem item2 = produce2.getItem();
            if (item1.getItemCategory() == item2.getItemCategory()) {
                return Integer.compare(item1.getTier(), item2.getTier());
            } else return item1.getItemCategory().compareTo(item2.getItemCategory());
        });

        for (SomItem item : ItemDataLoader.getItemDataList()) {
            try {
                sellLoad(item);
            } catch (Exception | StackOverflowError e) {
                Log("§a[ProduceDataLoader]§c" + item.getId() + "の価格自動設定中にエラーが発生しました");
            }
        }

        Log("§a[ProduceDataLoader]§b" + produceDataList.size() + "個をロードしました");
    }

    public static int sellLoad(SomItem item) {
        if (SellMultiply.containsKey(item.getId()) && item.getSell() == -1) {
            double totalSell = 0;
            for (ProduceData produceData : list) {
                if (produceData.getItem().getId().equals(item.getId())) {
                    double currentSell = 0;
                    for (SomItemStack stack : produceData.getRecipe().getRecipeSlot()) {
                        currentSell += sellLoad(stack.getItem()) * stack.getAmount();
                    }
                    currentSell /= produceData.getAmount();
                    if (currentSell > totalSell) totalSell = currentSell;
                }
            }
            int sell = (int) Math.ceil(totalSell);
            ItemDataLoader.setSell(item.getId(), (int) (sell * SellMultiply.get(item.getId())));
            return sell;
        } else {
            return item.getSell();
        }
    }
}
