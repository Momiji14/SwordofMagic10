package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Player.Help.HelpData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.loreText;
import static SwordofMagic10.SomCore.Log;

public class HelpDataLoader {


    private static final HashMap<String, HelpData> helpDataList = new HashMap<>();
    private static final HashMap<String, List<HelpData>> categoryList = new HashMap<>();
    private static final List<HelpData> list = new ArrayList<>();

    public static HashMap<String, List<HelpData>> getCategoryList() {
        return categoryList;
    }

    public static List<HelpData> getCategoryList(String category) {
        return categoryList.get(category);
    }

    public static HelpData getHelpData(String id) {
        if (!helpDataList.containsKey(id)) {
            Log("§c存在しないHelpが参照されました -> " + id);
            return null;
        }
        return helpDataList.get(id);
    }

    public static List<HelpData> getHelpDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (HelpData helpData : getHelpDataList()) {
            complete.add(helpData.getId());
        }
        return complete;
    }

    public static void load() {
        helpDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "HelpData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                String category = data.getString("Category");
                HelpData helpData = new HelpData();
                helpData.setId(id);
                helpData.setIcon(Material.valueOf(data.getString("Icon")));
                helpData.setDisplay(data.getString("Display"));
                helpData.setCategory(category);
                helpData.setLore(loreText(data.getStringList("Lore")));
                helpDataList.put(id, helpData);
                if (!categoryList.containsKey(category)) categoryList.put(category, new ArrayList<>());
                categoryList.get(category).add(helpData);
                list.add(helpData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        list.sort(Comparator.comparing(HelpData::getId));
        Log("§a[HelpDataLoader]§b" + helpDataList.size() + "個をロードしました");
    }
}
