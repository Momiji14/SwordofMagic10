package SwordofMagic10.Player.Achievement;

import SwordofMagic10.Component.CustomItemStack;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class AchievementData {

    private String id;
    private String display;
    private Material icon;
    private List<String> lore;
    private boolean isHide;
    private final List<Frame> animation = new ArrayList<>();

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

    public List<Frame> getAnimation() {
        return animation;
    }

    public void setAnimation(List<String> animation) {
        for (String str : animation) {
            String[] split = str.split(",");
            int tick = 0;
            if (split.length > 1) {
                String[] split2 = split[1].split(":");
                switch (split2[0]) {
                    case "Tick" -> tick = Integer.parseInt(split2[1]);
                }
                this.animation.add(new Frame(split[0], tick));
            }
        }
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(icon).setCustomData("Achievement", id);
        item.setDisplay(getDisplay());
        item.addLore(getLore());
        item.addSeparator("アニメーション");
        for (Frame frame : getAnimation()) {
            item.addLore(frame.animation());
        }
        return item;
    }

    public record Frame(String animation, int tick) {}
}
