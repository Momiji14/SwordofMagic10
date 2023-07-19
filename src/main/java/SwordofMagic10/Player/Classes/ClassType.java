package SwordofMagic10.Player.Classes;

import SwordofMagic10.DataBase.SkillGroupLoader;
import SwordofMagic10.Item.EquipmentCategory;
import SwordofMagic10.Player.Skill.SkillGroup;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.loreText;

public enum ClassType {
    SwordKaiser("ソードカイザー", "§c", "SRK", Material.IRON_SWORD, EquipmentCategory.Sword, "Slash", "SwordMan", "近距離で戦う前衛クラス\n高火力のスキルで敵を殲滅しよう"),
    GranMagia("グランマギア", "§d", "GRM", Material.BLAZE_ROD, EquipmentCategory.Rod, "MagicBall", "Wizard", "魔法を扱い攻撃するクラス\n広範囲な攻撃魔法で敵を殲滅しよう"),
    Avengista("アベンジスタ", "§2", "AVG", Material.IRON_HORSE_ARMOR, EquipmentCategory.Gun, "BulletBit", "Scout", "銃器を駆使して攻撃や援護するクラス\nヘッドショットや特殊弾を駆使して戦おう\n※AIMが必要なため操作難度が高いです"),
    Celestia("セレスティア", "§b", "CES", Material.BRUSH, EquipmentCategory.Mace, "Swing", "Cleric", "味方を支援するクラス\n回復やバフを駆使して味方を支援しよう"),
    ;

    private final String display;
    private final String color;
    private final String nick;
    private final Material icon;
    private final List<EquipmentCategory> equipAble = new ArrayList<>();
    private final List<String> lore;
    private final List<SkillGroup> skillGroups = new ArrayList<>();
    private final String defaultGroupID;
    private SkillGroup defaultGroup;
    private final String normalSkill;

    ClassType(String display, String color, String nick, Material icon, EquipmentCategory equipAble, String normalSkill, String defaultGroup, String lore) {
        this.display = display;
        this.color = color;
        this.nick = nick;
        this.icon = icon;
        this.equipAble.add(equipAble);
        this.equipAble.add(EquipmentCategory.Helmet);
        this.equipAble.add(EquipmentCategory.Chest);
        this.equipAble.add(EquipmentCategory.Legs);
        this.equipAble.add(EquipmentCategory.Boots);
        this.equipAble.add(EquipmentCategory.Shield);
        this.equipAble.add(EquipmentCategory.Trinket);
        this.equipAble.add(EquipmentCategory.Amulet);
        this.normalSkill = normalSkill;
        defaultGroupID = defaultGroup;
        this.lore = loreText(List.of(lore.split("\n")));
    }

    public String getDisplay() {
        return display;
    }

    public String getColorDisplay() {
        return color + display;
    }

    public String getColor() {
        return color;
    }

    public String getNick() {
        return nick;
    }

    public String getColorNick() {
        return color + nick;
    }

    public Material getIcon() {
        return icon;
    }

    public List<EquipmentCategory> getEquipAble() {
        return equipAble;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<SkillGroup> getSkillGroups() {
        return skillGroups;
    }

    public void addSkillGroup(SkillGroup skillGroup) {
        skillGroups.add(skillGroup);
    }

    public String getNormalSkill() {
        return normalSkill;
    }

    public SkillGroup getDefaultSkillGroup() {
        if (defaultGroup == null) defaultGroup = SkillGroupLoader.getSkillGroup(defaultGroupID);
        return defaultGroup;
    }
}
