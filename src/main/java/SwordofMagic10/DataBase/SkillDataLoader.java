package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Skill.SkillData;
import SwordofMagic10.Player.Skill.SkillGroup;
import SwordofMagic10.Player.Skill.SkillParameterType;
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

public class SkillDataLoader {


    private static final HashMap<String, SkillData> skillDataList = new HashMap<>();
    private static final List<SkillData> list = new ArrayList<>();

    public static SkillData getSkillData(String id) {
        if (!skillDataList.containsKey(id)) {
            Log("§c存在しないSkillDataが参照されました -> " + id);
            return null;
        }
        return skillDataList.get(id);
    }

    public static List<SkillData> getSkillDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (SkillData skillData : getSkillDataList()) {
            complete.add(skillData.getId());
        }
        return complete;
    }

    public static void load() {
        skillDataList.clear();
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "SkillData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                SkillGroup skillGroup = SkillGroupLoader.getSkillGroup(data.getString("SkillGroup"));
                SkillData skillData = new SkillData();
                skillData.setId(id);
                skillData.setDisplay(data.getString("Display"));
                try {
                    skillData.setIcon(Material.valueOf(data.getString("Icon")));
                } catch (Exception e) {
                    skillData.setIcon(Material.BARRIER);
                    Log("§b" + id + "§aの§eIcon§aが間違っています §c" + data.getString("Icon"));
                }
                skillData.setLore(loreText(data.getStringList("Lore")));
                skillData.setGroup(skillGroup);
                skillData.setStack(data.getInt("Stack", 1));
                skillData.setManaCost(data.getDouble("ManaCost"));
                skillData.setCastTime((int) (data.getDouble("CastTime")*20));
                skillData.setRigidTime((int) (data.getDouble("RigidTime")*20));
                skillData.setCoolTime((int) (data.getDouble("CoolTime")*20));
                for (SkillParameterType type : SkillParameterType.values()) {
                    if (data.isSet("Parameter." + type)) {
                        skillData.setParameter(type, data.getDouble("Parameter." + type));
                    }
                }
                for (StatusType type : StatusType.values()) {
                    if (data.isSet("Parameter." + type)) {
                        skillData.setStatus(type, data.getDouble("Parameter." + type));
                    }
                }
                skillGroup.addList(skillData);
                skillDataList.put(id, skillData);
                list.add(skillData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }

        list.sort(Comparator.comparing(SkillData::getId));
        Log("§a[SkillDataLoader]§b" + skillDataList.size() + "個をロードしました");
    }
}
