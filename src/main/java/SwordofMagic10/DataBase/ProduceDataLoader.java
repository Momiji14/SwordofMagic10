package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Gathering.ProduceData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public class ProduceDataLoader {


    private static final HashMap<String, ProduceData> produceDataList = new HashMap<>();
    private static final List<ProduceData> list = new ArrayList<>();

    public static ProduceData getProduceData(String id) {
        if (!produceDataList.containsKey(id)) {
            Log("§c存在しないProduceDataが参照されました -> " + id);
            return null;
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
                String id = SomLoader.fileId(file);
                ProduceData produceData = new ProduceData();
                produceData.setId(id);
                produceData.setDisplay(data.getString("Display"));
                produceData.setStack(ItemDataLoader.fromText(data.getString("Item")));
                produceData.setCost(data.getInt("Cost"));
                produceData.setExp(data.getInt("Exp"));
                produceData.setRecipe(RecipeDataLoader.getRecipeData(data.getString("Recipe")));
                produceDataList.put(id, produceData);
                list.add(produceData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        for (SomItem item : ItemDataLoader.getItemDataList()) {
            if (item.getId().contains("の原木")) {
                String id = item.getId().replace("原木", "木材");
                SomItem newItem = ItemDataLoader.getItemData(id);
                ProduceData produceData = new ProduceData();
                produceData.setId(id);
                produceData.setDisplay(id);
                produceData.setItem(newItem);
                produceData.setAmount(1);
                produceData.setCost(3);
                produceData.setExp(3);
                produceData.setRecipe(RecipeDataLoader.getRecipeData(id));
                produceDataList.put(id, produceData);
                list.add(produceData);
            }
        }

        list.sort(Comparator.comparing(produceData -> produceData.getItem().getItemCategory()));
        Log("§a[ProduceDataLoader]§b" + produceDataList.size() + "個をロードしました");
    }
}
