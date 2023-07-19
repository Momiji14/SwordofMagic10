package SwordofMagic10.Player.Achievement;

import org.bukkit.Material;

import java.util.List;

public class AchievementData {

    private String id;
    private String display;
    private Material icon;
    private List<String> lore;
    private boolean isHide;
    private List<String> animation;

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

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public List<String> getAnimation() {
        return animation;
    }

    public void setAnimation(List<String> animation) {
        this.animation = animation;
    }
}
