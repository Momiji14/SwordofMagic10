package SwordofMagic10.Player.Skill;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Player.Classes.ClassType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SkillGroup {
    private String id;
    private String display;
    private String nick;
    private Material icon;
    private List<String> lore = new ArrayList<>();
    private final List<SkillData> list = new ArrayList<>();
    private ClassType ownerClass;
    private boolean hide = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<SkillData> getList() {
        return list;
    }

    public void addList(SkillData skillData) {
        list.add(skillData);
    }

    public ClassType getOwnerClass() {
        return ownerClass;
    }

    public void setOwnerClass(ClassType ownerClass) {
        this.ownerClass = ownerClass;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(icon);
        item.setDisplay(display);
        item.addLore(lore);
        item.addSeparator("使用可能スキル");
        for (SkillData skillData : list) {
            item.addLore("§7・§e" + skillData.getDisplay());
        }
        return item;
    }
}
