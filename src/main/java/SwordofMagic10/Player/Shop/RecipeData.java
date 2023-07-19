package SwordofMagic10.Player.Shop;

import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeData {

    private String id;
    List<SomItemStack> recipeSlot = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SomItemStack> getRecipeSlot() {
        return recipeSlot;
    }

    public void addRecipeSlot(SomItemStack stack) {
        recipeSlot.add(stack);
    }
}
