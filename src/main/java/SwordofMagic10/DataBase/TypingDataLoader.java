package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.SomCore.Log;

public class TypingDataLoader {
    private static final HashMap<String, String> typingDataList = new HashMap<>();
    private static final List<String> list = new ArrayList<>();

    public static HashMap<String, String> getList() {
        return typingDataList;
    }

    public static String get(String title) {
        return typingDataList.get(title);
    }

    public static String getRandomTitle() {
        return list.get(randomInt(0, list.size()));
    }

    public static void load() {
        typingDataList.clear();
        FileConfiguration data = YamlConfiguration.loadConfiguration(new File(Config.DataBase, "Typing.yml"));
        for (String text : data.getStringList("List")) {
            String[] split = text.split(",");
            typingDataList.put(split[0], split[1]);
            list.add(split[0]);
        }
        Log("§a[TypingDataLoader]§b" + typingDataList.size() + "個をロードしました");
    }
}
