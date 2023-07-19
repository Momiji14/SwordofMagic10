package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Player.Achievement.AchievementData;
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

public class AchievementDataLoader {


    private static final HashMap<String, AchievementData> achievementDataList = new HashMap<>();
    private static final List<AchievementData> list = new ArrayList<>();

    public static AchievementData getAchievementData(String id) {
        if (!achievementDataList.containsKey(id)) {
            Log("§c存在しないAchievementが参照されました -> " + id);
            return null;
        }
        return achievementDataList.get(id);
    }

    public static List<AchievementData> getAchievementDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (AchievementData achievement : getAchievementDataList()) {
            complete.add(achievement.getId());
        }
        return complete;
    }

    public static void load() {
        achievementDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "AchievementData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                AchievementData achievementData = new AchievementData();
                achievementData.setId(id);
                achievementData.setIcon(Material.valueOf(data.getString("Icon")));
                achievementData.setDisplay(data.getString("Display"));
                achievementData.setLore(loreText(data.getStringList("Lore")));
                achievementData.setHide(data.getBoolean("Hide", false));
                achievementData.setAnimation(data.getStringList("Animation"));
                achievementDataList.put(id, achievementData);
                list.add(achievementData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        list.sort(Comparator.comparing(AchievementData::getId));
        Log("§a[AchievementDataLoader]§b" + achievementDataList.size() + "個をロードしました");
    }
}
