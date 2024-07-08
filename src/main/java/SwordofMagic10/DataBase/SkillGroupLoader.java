package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Skill.SkillGroup;
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

public class SkillGroupLoader {


    private static final HashMap<String, SkillGroup> skillGroupList = new HashMap<>();
    private static final List<SkillGroup> list = new ArrayList<>();

    public static SkillGroup getSkillGroup(String id) {
        if (!skillGroupList.containsKey(id)) {
            Log("§c存在しないSkillGroupが参照されました -> " + id);
            return null;
        }
        return skillGroupList.get(id);
    }

    public static List<SkillGroup> getSkillGroupList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (SkillGroup SkillGroup : getSkillGroupList()) {
            complete.add(SkillGroup.getId());
        }
        return complete;
    }

    public static void load() {
        skillGroupList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "SkillGroup"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                SkillGroup skillGroup = new SkillGroup();
                skillGroup.setId(id);
                skillGroup.setDisplay(data.getString("Display"));
                skillGroup.setNick(data.getString("Nick"));
                skillGroup.setIcon(Material.valueOf(data.getString("Icon")));
                skillGroup.setLore(loreText(data.getStringList("Lore")));
                skillGroup.setHide(data.getBoolean("Hide", false));
                ClassType.valueOf(data.getString("OwnerClass")).addSkillGroup(skillGroup);
                skillGroupList.put(id, skillGroup);
                list.add(skillGroup);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        list.sort(Comparator.comparing(SkillGroup::getId));
        Log("§a[SkillGroupLoader]§b" + skillGroupList.size() + "個をロードしました");
    }
}
