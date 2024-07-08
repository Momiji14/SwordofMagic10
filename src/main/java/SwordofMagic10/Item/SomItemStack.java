package SwordofMagic10.Item;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomText;

import static SwordofMagic10.Component.Function.decoLore;

public class SomItemStack implements Cloneable {

    private SomItem item;
    private int amount;

    public SomItem getItem() {
        return item;
    }

    public SomItemStack(SomItem item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public void setItem(SomItem item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void removeAmount(int amount) {
        this.amount -= amount;
    }

    public SomItem.ItemCategory sortItemCategory() {
        return item.getItemCategory();
    }

    public String sortName() {
        return item.getDisplay();
    }

    public int sortSell() {
        return item.getSell();
    }

    public int sortTier() {
        return item.getTier();
    }

    public int sortLevel() {
        if (item instanceof SomQuality quality) {
            return quality.getLevel();
        } else return 0;
    }

    public double sortQuality() {
        if (item instanceof SomQuality quality) {
            return quality.getQuality();
        } else return 0.0;
    }

    public EquipmentCategory sortEquipmentCategory() {
        if (item instanceof SomEquip equip) {
            return equip.getEquipmentCategory();
        } else return EquipmentCategory.None;
    }

    public int sortEquipmentExp() {
        if (item instanceof SomQuality quality) {
            return quality.getExp();
        } else return 0;
    }

    public double sortRunePower() {
        if (item instanceof SomRune rune) {
            return rune.getPower();
        } else return 0.0;
    }

    public boolean sortFavorite() {
        return item.isFavorite();
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = this.item.viewItem();
        item.addSeparator("スタック情報");
        item.addLore(decoLore("個数") + amount);
        item.addLore(decoLore("売値") + (this.item.getSell() >= 0 ? this.item.getSell() * amount : "§4不可"));
        return item;
    }

    public SomText toSomText() {
        return  item.toSomText(amount);
    }

    public SomJson toJson() {
        SomJson json = item.toJson();
        json.set("Amount", amount);
        return json;
    }

    public static SomItemStack fromJson(String jsonData) {
        SomJson json = new SomJson(jsonData);
        return new SomItemStack(SomItem.fromJson(json), json.getInt("Amount", 1));
    }

    @Override
    public SomItemStack clone() {
        try {
            SomItemStack clone = (SomItemStack) super.clone();
            clone.item = getItem().clone();
            clone.amount = getAmount();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}