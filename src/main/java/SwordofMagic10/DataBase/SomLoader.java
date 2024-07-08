package SwordofMagic10.DataBase;

import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public interface SomLoader {
    static String fileId(File file) {
        return file.getName().replace(".yml", "");
    }

    static void error(File file, Exception e) {
        e.printStackTrace();
        Log("§e" + file.getName() + "§cのロード中にエラーが発生しました");
    }

    static List<File> dumpFile(File file) {
        List<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File tmpFile : files) {
            if (!tmpFile.getName().equals(".sync")) {
                if (tmpFile.isDirectory()) {
                    list.addAll(dumpFile(tmpFile));
                } else if (tmpFile.getName().contains(".yml")) {
                    list.add(tmpFile);
                }
            }
        }
        return list;
    }

    static void load() {
        HelpDataLoader.load();
        ItemDataLoader.load();
        MobDataLoader.load();
        MapDataLoader.load();
        DungeonDataLoader.load();
        QuestDataLoader.load();
        RecipeDataLoader.load();
        SkillGroupLoader.load();
        SkillDataLoader.load();
        AchievementDataLoader.load();
        ProduceDataLoader.load();
        ShopDataLoader.load();
        SpawnerDataLoader.load();
        TypingDataLoader.load();

        DefensiveBattle.initialize();
        MapDataLoader.start();
    }
}
