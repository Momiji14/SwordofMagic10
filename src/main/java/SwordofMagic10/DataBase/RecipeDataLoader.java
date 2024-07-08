package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Gathering.ProduceData;
import SwordofMagic10.Player.Shop.RecipeData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public class RecipeDataLoader {
    private static final HashMap<String, RecipeData> recipeDataList = new HashMap<>();
    public static RecipeData getRecipeData(String id) {
        if (!recipeDataList.containsKey(id)) Log("§c存在しないRecipeが参照されました -> " + id);
        return recipeDataList.get(id);
    }
    public static Collection<RecipeData> getRecipeData() {
        return recipeDataList.values();
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (RecipeData recipe : getRecipeData()) {
            complete.add(recipe.getId());
        }
        return complete;
    }

    public static void load() {
        recipeDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "RecipeData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                if (data.isSet("Prefix")) {
                    for (String prefix : data.getStringList("Prefix")) {
                        RecipeData recipe = new RecipeData();
                        recipe.setId(data.getString("RecipeID").replace("%Prefix%", prefix));
                        for (String str : data.getStringList("Recipe")) {
                            recipe.addRecipeSlot(ItemDataLoader.fromText(str.replace("%Prefix%", prefix)));
                        }
                        recipeDataList.put(recipe.getId(), recipe);
                    }
                } else {
                    String id = SomLoader.fileId(file);
                    RecipeData recipe = new RecipeData();
                    recipe.setId(id);
                    for (String str : data.getStringList("Recipe")) {
                        recipe.addRecipeSlot(ItemDataLoader.fromText(str));
                    }
                    recipeDataList.put(id, recipe);
                }
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }
    }
}
