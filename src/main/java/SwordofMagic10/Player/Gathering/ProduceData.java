package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Shop.RecipeData;

import java.util.HashMap;

import static SwordofMagic10.Component.Function.decoLore;


public class
ProduceData {
    private String id;
    private String display;
    private SomItem item;
    private int amount;
    private int cost;
    private int exp = 0;
    private RecipeData recipeData;

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

    public SomItem getItem() {
        return item;
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

    public void setStack(SomItemStack stack) {
        item = stack.getItem();
        amount = stack.getAmount();
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public RecipeData getRecipe() {
        return recipeData;
    }

    public void setRecipe(RecipeData recipeData) {
        this.recipeData = recipeData;
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = this.item.viewItem();
        item.addSeparator("加工情報");
        item.addLore(decoLore("加工経験値") + exp);
        item.addLore(decoLore("加工コスト") + cost);
        for (SomItemStack stack : getRecipe().getRecipeSlot()) {
            item.addLore(decoLore(stack.getItem().getDisplay()) + "§ax" + stack.getAmount());
        }
        return item;
    }
}
