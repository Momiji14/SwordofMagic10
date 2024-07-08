package SwordofMagic10.Item;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MobDataLoader;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.Menu.EnhanceMenu;
import SwordofMagic10.Player.Skill.SkillParameterType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Component.Function.decoSeparator;
import static SwordofMagic10.Entity.StatusType.*;
import static SwordofMagic10.Item.SomEquipment.MaxRuneSlot;
import static SwordofMagic10.SomCore.Log;
import static org.bukkit.Material.*;

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

    private boolean favorite = false;

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

    public int getRawSell() {
        return sell;
    }

    public int getSell() {
        return ItemDataLoader.getSell(id);
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean equal(SomItem item) {
        if (id.equals(item.getId()) && tier == item.getTier() && itemCategory == item.getItemCategory()) {
            if (item instanceof SomQuality quality1 && this instanceof SomQuality quality2) {
                if (quality1.getLevel() != quality2.getLevel() || quality1.getExp() != quality2.getExp() || quality1.getQuality() != quality2.getQuality()) {
                    return false;
                }
            }
            if (item instanceof SomPlus plus1 && this instanceof SomPlus plus2) {
                if (plus1.getPlus() != plus2.getPlus()) {
                    return false;
                }
            }
            if (item instanceof SomRune rune1 && this instanceof SomRune rune2) {
                if (rune1.getPower() != rune2.getPower()) {
                    return false;
                }
            }
            if (item instanceof SomEquipment equip1 && this instanceof SomEquipment equip2) {
                if (equip1.getEquipmentCategory() != equip2.getEquipmentCategory()) {
                    return false;
                }
            }
            if (item instanceof SomWorker worker1 && this instanceof SomWorker worker2) {
                for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                    if (worker1.getLevel(type) != worker2.getLevel(type) || worker1.getExp(type) != worker2.getExp(type)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public SomItem clone() {
        try {
            SomItem clone = (SomItem) super.clone();
            clone.tier = getTier();
            clone.favorite = isFavorite();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getColorDisplay() {
        if (this instanceof SomQuality quality) {
            return quality.getQualityRank().getColor() + getDisplay();
        } else return "§f" + getDisplay();
    }

    public String getColorTierDisplay() {
        String display;
        if (this instanceof SomQuality quality) {
            display = quality.getQualityRank().getColor() + getDisplay();
        } else display = "§f" + getDisplay();
        return display + (ItemDataLoader.getItemData(getId()).getTier() < getTier() ? "T" + getTier() : "");
    }

    public List<String> toStringList() {
        List<String> list = new ArrayList<>();
        list.add(decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay()));
        list.addAll(getLore());
        list.add(decoSeparator("アイテム情報"));
        list.add(decoLore("カテゴリ") + itemCategory.getDisplay());
        list.add(decoLore("ティア") + tier);
        list.add(decoLore("売値") + (getSell() >= 0 ? getSell() : "§4不可"));
        if (this instanceof SomPlus plus) {
            list.set(0, decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay() + "§r §e[" + plus.getLevel() + "+" + plus.getPlus() + "]"));
        } else if (this instanceof SomQuality quality) {
            list.set(0, decoText("§e" + Config.RomNum(tier) + "§r " + getColorDisplay() + "§r §e[" + quality.getLevel() + "]"));
        }
        if (this instanceof SomEquipment equipment) {
            list.add(decoSeparator("装備ステータス"));
            list.add(decoLore("装備種") + equipment.getEquipmentCategory().getDisplay());
            for (StatusType statusType : StatusType.values()) {
                double value = equipment.getStatusLevelSync(statusType, Classes.MaxLevel, getTier());
                if (value != 0) {
                    list.add(decoLore(statusType.getDisplay()) + scale(value, 2));
                }
            }
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("品質") + equipment.getQualityDisplay() + " §8(" + scale(equipment.getQuality()*100, 3) + "%)");
            list.add(decoLore("レベル") + equipment.getLevelDisplay());
            list.add(decoLore("強化値") + equipment.getPlus() + "/" + EnhanceMenu.MaxPlus);
            list.add(decoLore("精錬値") + equipment.getExp() + "/" + EnhanceMenu.reqExp(equipment));
            list.add(decoSeparator("ルーンスロット"));
            for (SomRune rune : equipment.getRune()) {
                list.add("§7・§e" + Config.RomNum(rune.getTier()) + rune.getColorDisplay() + "§e[" + rune.getLevel() + "] §b" + scale(rune.getPower()*100, 3) + "%");
            }
            for (int i = 0; i < Math.min(tier, MaxRuneSlot) - equipment.getRune().size(); i++) {
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
            list.add(decoLore("品質") + rune.getQualityDisplay() + " §8(" + scale(rune.getQuality()*100, 3) + "%)");
            list.add(decoLore("ルーン力") + scale(rune.getPower()*100, 3) + "%");
            list.add(decoLore("レベル") + rune.getLevel() + "/" + Classes.MaxLevel);
            list.add(decoLore("精錬値") + rune.getExp() + "/" + EnhanceMenu.reqExp(rune));
        } else if (this instanceof SomAlchemyStone stone) {
            list.add(decoSeparator("ステータス"));
            for (StatusType statusType : StatusType.values()) {
                double value = stone.getStatusLevelSync(statusType, Classes.MaxLevel, getTier());
                if (value != 0) {
                    list.add(decoLore(statusType.getDisplay()) + scale(value, 2));
                }
            }
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("品質") + stone.getQualityDisplay() + " §8(" + scale(stone.getQuality()*100, 3) + "%)");
            list.add(decoLore("レベル") + stone.getLevelDisplay());
            list.add(decoLore("強化値") + stone.getPlus() + "/" + EnhanceMenu.MaxPlus);
            list.add(decoLore("精錬値") + stone.getExp() + "/" + EnhanceMenu.reqExp(stone));
            list.add(decoSeparator("精錬素材"));
            stone.getExpMap().forEach((id, exp) -> list.add(decoLore(id) + "+" + exp));
        } else if (this instanceof SomTool tool) {
            list.add(decoSeparator("ツール情報"));
            list.add(decoLore(tool.getType().getDisplay() + "力") + scale(tool.getPower()*100));
            tool.getMultiply().forEach((id, value) -> list.add("§7・§r" + ItemDataLoader.getItemData(id).getColorDisplay() + " §a" + scale((value-1)*100, true) + "%"));
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("品質") + tool.getQualityDisplay() + " §8(" + scale(tool.getQuality()*100, 3) + "%)");
            list.add(decoLore("レベル") + tool.getLevelDisplay());
            list.add(decoLore("強化値") + tool.getPlus() + "/" + EnhanceMenu.MaxPlus);
            list.add(decoLore("精錬値") + tool.getExp() + "/" + EnhanceMenu.reqExp(tool));
        } else if (this instanceof SomPotion potion) {
            list.add(decoSeparator("料理情報"));
            list.add(decoLore("効果時間") + potion.getDuration() + "秒");
            for (StatusType statusType : StatusType.values()) {
                double fixed = potion.getFixed().getOrDefault(statusType, 0.0);
                double multiply = potion.getMultiply().getOrDefault(statusType, 0.0);
                if (fixed != 0) list.add(decoLore(statusType.getDisplay()) + scale(fixed, -1, true));
                if (multiply != 0) list.add(decoLore(statusType.getDisplay()) + scale(multiply*100, 1, true) + "%");
            }
        } else if (this instanceof SomAmulet amulet) {
            list.add(decoSeparator("ステータス"));
            for (StatusType statusType : StatusType.values()) {
                double value = amulet.getAmuletStatus(statusType);
                if (value != 0) {
                    list.add(decoLore(statusType.getDisplay()) + scale(value*100, 3, true) + "% §8(" + scale(amulet.getStatus(statusType)*100, 3) + "%)");
                }
            }
            list.add(decoSeparator("願瓶枠"));
            for (SomAmulet.Bottle bottle : amulet.getBottles()) {
                list.add("§7・§e" + Config.RomNum(bottle.getTier()) + bottle.getColorDisplay());
            }
            for (int i = 0; i < amulet.getBottleSlot() - amulet.getBottles().size(); i++) {
                list.add("§7・§c未使用枠");
            }
            list.add(decoSeparator("強化情報"));
            list.add(decoLore("品質") + amulet.getQualityDisplay() + " §8(" + scale(amulet.getQuality()*100, 3) + "%)");
            list.add(decoLore("強化値") + amulet.getPlus() + "/" + EnhanceMenu.MaxPlus);
            list.add(decoLore("精錬値") + amulet.getExp() + "/" + EnhanceMenu.reqExp(amulet));
        }else if (this instanceof SomAmulet.Bottle bottle) {
            if (!bottle.getParameter().isEmpty() || !bottle.getStatus().isEmpty()) {
                list.add(decoSeparator("願瓶情報"));
                for (SkillParameterType type : SkillParameterType.values()) {
                    double value = bottle.getParameter(type);
                    if (value != 0) {
                        list.add(decoLore(type.getDisplay())  + scale(value * type.getDisplayMultiply(), type.getDigit()));
                    }
                }
                for (StatusType statusType : StatusType.values()) {
                    double value = bottle.getStatus(statusType);
                    if (value != 0) {
                        list.add(decoLore(statusType.getDisplay()) + scale(value*100, true) + "%");
                    }
                }
            }
        }else if (this instanceof SomWorker worker) {
            list.add(decoSeparator("労働者能力"));
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                list.add(decoLore(type.getDisplay() + "力") + scale(worker.getPower(type)*100));
            }
            list.add(decoSeparator("労働者レベル"));
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                int level = worker.getLevel(type);
                list.add(decoLore(type.getDisplay()) + "§eLv" + level + " §a" + scale(worker.getExp(type) / GatheringMenu.getReqExp(level)*100, 2) + "% §8" + scale(worker.getExp(type)));
            }
        } else if (this instanceof SomPet pet) {
            list.set(0, decoText("§e" + Config.RomNum(tier) + "§r " + pet.getPetName() + "§r §e[" + pet.getLevel() + "]"));
            list.add(decoSeparator("ペット"));
            list.add(decoLore("種別") + pet.getMobData().getId());
            list.add(decoLore("レベル") + pet.getLevel());
            list.add(decoLore("経験値") + scale(pet.getExpPercent()*100, 3) + "%");
            list.add(decoSeparator("性格"));
            list.add("§7・§a" + pet.getPerson1().getDisplay());
            list.add("§7・§a" + pet.getPerson2().getDisplay());
            list.add("§7・§a" + pet.getPerson3().getDisplay());
            list.add("§7・§a" + pet.getPerson4().getDisplay());
            list.add("§7・§a" + pet.getPetName());
            list.add(decoSeparator("ステータス"));
            list.add(decoLore(Health.getDisplay()) + scale(pet.getHealth()) + "/" + scale(pet.getMaxHealth()));
            list.add(decoLore(Mana.getDisplay()) + scale(pet.getMana()) + "/" + scale(pet.getMaxMana()));
            for (StatusType statusType : PetStatus()) {
                switch (statusType) {
                    case Health, MaxHealth, Mana, MaxMana -> {}
                    default -> list.add(decoLore(statusType.getDisplay()) + scale(pet.getStatus(statusType), 2));
                }
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
            case CROSSBOW -> item.reloadCrossBowArrow();
        }
        if (this instanceof SomTool tool) {
            if (tool.getPlus() > 10) {
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, tool.getPlus()-10);
            }
            item.setCustomData("ToolExp", tool.getExp());
        }
        if (isFavorite() || (this instanceof SomPet pet && pet.isSummon())) {
            item.setGlowing();
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
        String display;
        if (this instanceof SomPet pet) {
            display = pet.getPetName();
        } else {
            display = getColorTierDisplay();
        }
        if (amount > 1) display += "§ex" + amount;
        text.addHover(display, builder.toString());
        return text;
    }

    public SomJson toJson() {
        SomJson json = new SomJson();
        json.set("Id", getId());
        json.set("Favorite", isFavorite());
        json.set("Tier", getTier());
        if (this instanceof SomQuality quality) {
            json.set("Level", quality.getLevel());
            json.set("RawLevel", quality.getRawLevel());
            json.set("Exp", quality.getExp());
            json.set("Quality", quality.getQuality());
        }
        if (this instanceof SomPlus plus) {
            json.set("Plus", plus.getPlus());
        }
        if (this instanceof SomRune rune) {
            json.set("RunePower", rune.getPower());
        }
        if (this instanceof SomEquipment equipment) {
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
        if (this instanceof SomAmulet amulet) {
            amulet.getStatus().forEach((statusType, value) -> json.set(statusType.toString(), value));
            for (SomAmulet.Bottle bottle : amulet.getBottles()) {
                json.addArray("Bottle", bottle.toJson());
            }
        }
        if (this instanceof SomPet pet) {
            json.set("PetName", pet.getPetName());
            json.set("MobData", pet.getMobData().getId());
            json.set("Level", pet.getLevel());
            json.set("Exp", pet.getExp());
            json.set("Person1", pet.getPerson1().toString());
            json.set("Person2", pet.getPerson2().toString());
            json.set("Person3", pet.getPerson3().toString());
            json.set("Person4", pet.getPerson4().toString());
            for (StatusType statusType : PetStatus()) {
                json.set(statusType.toString(), pet.getPetStatus(statusType));
            }
            json.set("Health", pet.getHealth());
            json.set("Mana", pet.getMana());
        }
        return json;
    }

    public static SomItem fromJson(SomJson json) {
        SomItem item = ItemDataLoader.getItemData(json.getString("Id"));
        item.setFavorite(json.getBoolean("Favorite", item.isFavorite()));
        item.setTier(json.getInt("Tier", item.getTier()));
        if (item instanceof SomQuality quality) {
            quality.setLevel(json.getInt("Level", quality.getLevel()));
            quality.setRawLevel(json.getInt("RawLevel", quality.getLevel()));
            quality.setExp(json.getInt("Exp", quality.getExp()));
            quality.setQuality(json.getDouble("Quality", quality.getQuality()));
        }
        if (item instanceof SomPlus plus) {
            plus.setPlus(json.getInt("Plus", plus.getPlus()));
        }
        if (item instanceof SomRune rune) {
            rune.setPower(json.getDouble("RunePower", rune.randomPower()));
        }
        if (item instanceof SomEquipment equipment) {
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
                worker.setExp(type, json.getDouble(type + ".Exp", worker.getExp(type)));
            }
        }
        if (item instanceof SomAmulet amulet) {
            for (StatusType statusType : StatusType.AmuletStatus()) {
                if (json.has(statusType.toString())) amulet.setStatus(statusType, json.getDouble(statusType.toString(), 0));
            }
            if (json.has("Bottle")) {
                for (String runeData : json.getList("Bottle")) {
                    SomAmulet.Bottle bottle = (SomAmulet.Bottle) fromJson(new SomJson(runeData));
                    amulet.addBottle(bottle);
                }
            }
        }
        if (item instanceof SomPet pet) {
            pet.setMobData(MobDataLoader.getMobData(json.getString("MobData")));
            pet.setPetName(json.getString("PetName"));
            pet.setLevel(json.getInt("Level", pet.getLevel()));
            pet.setExp(json.getDouble("Exp", pet.getExp()));
            pet.setPerson1(SomPet.Person1.valueOf(json.getString("Person1", SomPet.Person1.random().toString())));
            pet.setPerson2(SomPet.Person2.valueOf(json.getString("Person2", SomPet.Person2.random().toString())));
            pet.setPerson3(SomPet.Person3.valueOf(json.getString("Person3", SomPet.Person3.random().toString())));
            pet.setPerson4(SomPet.Person4.valueOf(json.getString("Person4", SomPet.Person4.random().toString())));
            for (StatusType statusType : PetStatus()) {
                if (json.has(statusType.toString())) {
                    pet.setPetStatus(statusType, json.getDouble(statusType.toString(), 0));
                }
            }
            pet.updateStatus();
            pet.setHealth(json.getDouble("Health", 0));
            pet.setMana(json.getDouble("Mana", 0));
        }
        return item;
    }

    public enum ItemCategory {
        Potion("ポーション", POTION),
        Cook("料理", BREAD),
        Equipment("装備", IRON_CHESTPLATE),
        Tool("ツール", IRON_PICKAXE),
        Worker("労働者権利書", MAP),
        Pet("ペット", SPAWNER),
        Record("記録", WRITABLE_BOOK),
        AmuletBottle("願瓶", DRAGON_BREATH),
        Rune("ルーン", HEART_POTTERY_SHERD),
        SpecialStone("特殊石", SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
        Mining("採掘素材", IRON_ORE),
        Lumber("伐採素材", OAK_WOOD),
        Collect("採集素材", POPPY),
        Fishing("漁獲素材", FISHING_ROD),
        Hunting("狩猟素材", CROSSBOW),
        RunePowder("ルーンの粉", GLOWSTONE_DUST),
        Material("素材", SUGAR),
        Sell("売却用", SUNFLOWER),
        Quest("クエスト", ITEM_FRAME),
        ;

        private final String display;
        private final Material icon;

        ItemCategory(String display, Material icon) {
            this.display = display;
            this.icon = icon;
        }

        public String getDisplay() {
            return display;
        }

        public org.bukkit.Material getIcon() {
            return icon;
        }

        public boolean isEquipment() {
            return this == Equipment;
        }

        public CustomItemStack viewItem() {
            return new CustomItemStack(getIcon()).setDisplay(getDisplay()).setCustomData("Category", toString());
        }
    }

}
