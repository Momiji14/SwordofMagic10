package SwordofMagic10.Player.Help;

import SwordofMagic10.Component.CustomItemStack;
import org.bukkit.Material;

import java.util.List;

public class HelpData {
    private String id;
    private Material icon;
    private String display;
    private String category;
    private List<String> lore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(getIcon());
        item.setDisplay(getDisplay());
        item.addLore(getLore());
        return item;
    }
}
