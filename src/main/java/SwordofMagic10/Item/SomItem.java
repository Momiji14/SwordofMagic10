package SwordofMagic10.Item;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.Menu.EnhanceMenu;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static SwordofMagic10.Component.Function.*;

public class SomItem implements Cloneable {

    private UUID uuid;
    private String id;
    private Material icon;
    private String display;
    private List<String> lore;
    private int tier = 1;
    private int sell = -1;
    private ItemCategory itemCategory;
    private Color color;
    private final List<Pattern> patterns = new ArrayList<>();
    private final List<HowToGet> howToGet = new ArrayList<>();

    private boolean isLock = false;

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

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

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setLore(String lore) {
        this.lore = Collections.singletonList("§a" + lore);
    }

    public int getTier() {
        return tier;
    }

    public SomItem setTier(int tier) {
        this.tier = tier;
        return this;
    }

    public int getSell() {
        return sell;
    }

    public void setSell(int sell) {
        this.sell = sell;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    public List<HowToGet> getHowToGet() {
        return howToGet;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean equal(SomItem item) {
        if (id.equals(item.getId()) && tier == item.getTier()) {
            if (item instanceof SomEquipment equip1 && this instanceof  SomEquipment equip2) {
                return equip1.getLevel() == equip2.getLevel()
                        && equip1.getExp() == equip2.getExp()
                        && equip1.getEquipmentCategory() == equip2.getEquipmentCategory()
                        && equip1.getQuality() == equip2.getQuality();
            } else if (item instanceof SomRune rune1 && this instanceof  SomRune rune2) {
                return rune1.getLevel() == rune2.getLevel()
                        && rune1.getQuality() == rune2.getQuality();
            } else {
                return itemCategory == item.getItemCategory();
            }
        }
        return false;
    }

    @Override
    public SomItem clone() {
        try {
            SomItem clone = (SomItem) super.clone();
            clone.tier = getTier();
            clone.isLock = isLock();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getColorDisplay() {
        if (this instanceof SomQuality quality) {
            return quality.getQualityRank().getColor() + getDisplay();
        } else return getDisplay();
    }

    public List<String> toStringList() {
        List<String> list = new ArrayList<>();
        list.add(decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay()));
        list.addAll(getLore());
        list.add(decoSeparator("アイテム情報"));
        list.add(decoLore("カテゴリ") + itemCategory.getDisplay());
        list.add(decoLore("ティア") + tier);
        list.add(decoLore("売値") + (sell >= 0 ? sell : "§4不可"));
        if (this instanceof SomQuality quality) {
            list.set(0, decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay() + " §e[" + quality.getLevel() + "]"));
        }
        if (this instanceof SomEquipment equipment) {
            list.set(0, decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay() + " §e[" + equipment.getLevel() + "+" + equipment.getPlus() + "]"));
            list.add(decoSeparator("装備ステータス"));
            list.add(decoLore("装備種") + equipment.getEquipmentCategory().getDisplay());
            for (StatusType statusType : StatusType.values()) {
                double value = equipment.getStatusLevelSync(statusType, Classes.MaxLevel, getTier());
                if (value != 0) {
                    list.add(decoLore(statusType.getDisplay()) + scale(value, 2));
                }
            }
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("ティア") + getTier());
            list.add(decoLore("品質") + equipment.getQualityDisplay());
            list.add(decoLore("レベル") + equipment.getLevel());
            list.add(decoLore("強化値") + equipment.getPlus() + "/" + EnhanceMenu.MaxPlus);
            list.add(decoLore("精錬値") + equipment.getExp() + "/" + EnhanceMenu.reqExp(equipment));
            list.add(decoSeparator("ルーンスロット"));
            for (SomRune rune : equipment.getRune()) {
                list.add("§7・§e" + Config.RomNum(rune.getTier()) + rune.getColorDisplay() + "§e[" + rune.getLevel() + "]");
            }
            for (int i = 0; i < tier - equipment.getRune().size(); i++) {
                list.add("§7・§c未使用スロット");
            }
        } else if (this instanceof SomRune rune) {
            list.add(decoSeparator("ルーンステータス"));
            for (StatusType statusType : StatusType.values()) {
                double value = rune.getStatusLevelSync(statusType, Classes.MaxLevel, getTier());
                if (value != 0) {
                    list.add(decoLore(statusType.getDisplay()) + scale(value, 2));
                }
            }
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("ティア") + getTier());
            list.add(decoLore("品質") + rune.getQualityDisplay());
            list.add(decoLore("レベル") + rune.getLevel() + "/" + Classes.MaxLevel);
            list.add(decoLore("精錬値") + rune.getExp() + "/" + EnhanceMenu.reqExp(rune));
        } else if (this instanceof SomCook cook) {
            list.add(decoSeparator("料理情報"));
            cook.getFixed().forEach(((statusType, value) -> list.add(decoLore(statusType.getDisplay()) + scale(value, 0, true))));
            cook.getMultiply().forEach(((statusType, value) -> list.add(decoLore(statusType.getDisplay()) + scale(value*100, 1, true) + "%")));
        } else if (this instanceof SomWorker worker) {
            list.add(decoSeparator("労働者能力"));
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                int level = worker.getLevel(type);
                list.add(decoLore(type.getDisplay()) + "§eLv" + level + " §a" + scale((double) worker.getExp(type) / GatheringMenu.getReqExp(level)*100, 2) + "% §8" + worker.getExp(type));
            }
        }
        return list;
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(getIcon());
        List<String> lore = toStringList();
        item.setNonDecoDisplay(lore.get(0));
        lore.remove(0);
        item.addLore(lore);
        switch (getIcon()) {
            case POTION -> item.setPotionColor(color);
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> item.setLeatherArmorColor(color);
        }
        return item;
    }

    public SomText toSomText() {
        return  toSomText(1);
    }

    public SomText toSomText(int amount) {
        SomText text = SomText.create();
        StringBuilder builder = new StringBuilder();
        for (String str : toStringList()) {
            builder.append(str).append("\n");
        }
        text.addHover(getDisplay() + (amount > 1 ? ("x" + amount) : ""), builder.toString());
        return text;
    }

    public SomJson toJson() {
        SomJson json = new SomJson();
        json.set("Id", getId());
        json.set("Lock", isLock());
        json.set("Tier", getTier());
        if (this instanceof SomQuality quality) {
            json.set("Level", quality.getLevel());
            json.set("Exp", quality.getExp());
            json.set("Quality", quality.getQuality());
        }
        if (this instanceof SomEquipment equipment) {
            json.set("Plus", equipment.getPlus());
            for (SomRune rune : equipment.getRune()) {
                json.addArray("Rune", rune.toJson());
            }
        }
        if (this instanceof SomWorker worker) {
            json.set("Type", worker.getType().toString());
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                json.set(type + ".Level", worker.getLevel(type));
                json.set(type + ".Exp", worker.getExp(type));
            }
        }
        return json;
    }

    public static SomItem fromJson(SomJson json) {
        SomItem item = ItemDataLoader.getItemData(json.getString("Id"));
        item.setLock(json.getBoolean("Lock", item.isLock()));
        item.setTier(json.getInt("Tier", item.getTier()));
        if (item instanceof SomQuality quality) {
            quality.setLevel(json.getInt("Level", quality.getLevel()));
            quality.setExp(json.getInt("Exp", quality.getExp()));
            quality.setQuality(json.getDouble("Quality", quality.getQuality()));
        }
        if (item instanceof SomEquipment equipment) {
            equipment.setPlus(json.getInt("Plus", equipment.getPlus()));
            if (json.has("Rune")) {
                for (String runeData : json.getList("Rune")) {
                    SomRune rune = (SomRune) fromJson(new SomJson(runeData));
                    equipment.addRune(rune);
                }
            }
        }
        if (item instanceof SomWorker worker) {
            worker.setType(GatheringMenu.Type.valueOf(json.getString("Type")));
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                worker.setLevel(type, json.getInt(type + ".Level", worker.getLevel(type)));
                worker.setExp(type, json.getInt(type + ".Exp", worker.getExp(type)));
            }
        }
        return item;
    }

    public enum ItemCategory {
        Equipment("装備"),
        SpecialStone("特殊石"),
        Rune("ルーン"),
        Tool("ツール"),
        Mining("採掘素材"),
        Lumber("伐採素材"),
        Collect("採集素材"),
        Fishing("漁獲素材"),
        Hunting("狩猟素材"),
        Cook("料理"),
        Material("素材"),
        Worker("労働者権利書"),
        Sell("売却用")
        ;

        private final String display;

        ItemCategory(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public boolean isEquipment() {
            return this == Equipment;
        }
    }

}
