package SwordofMagic10.Player.Shop;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;

import java.util.HashMap;

import static SwordofMagic10.Component.Function.decoLore;


public class ShopData {

    private String id;
    private String display;
    private int maxPage;
    private final HashMap<Integer, ShopSlot> shopSlot = new HashMap<>();

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

    public void setShopSlot(int slot, ShopSlot shopSlot) {
        this.shopSlot.put(slot, shopSlot);
    }

    public HashMap<Integer, ShopSlot> getShopSlot() {
        return shopSlot;
    }

    public ShopSlot getShopSlot(int slot) {
        return shopSlot.get(slot);
    }


    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public static class ShopSlot {
        private SomItem item;
        private int amount = 1;
        private int mel = 0;
        private RecipeData recipeData;

        public SomItem getItem() {
            return item.clone();
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

        public int getMel() {
            return mel;
        }

        public void setMel(int mel) {
            this.mel = mel;
        }

        public RecipeData getRecipe() {
            return recipeData;
        }

        public void setRecipe(RecipeData recipeData) {
            this.recipeData = recipeData;
        }

        public boolean hasRecipe() {
            return recipeData != null;
        }

        public CustomItemStack viewItem() {
            CustomItemStack item = this.item.viewItem();
            item.addSeparator("販売情報");
            item.addLore(decoLore("販売価格") + mel + "メル");
            if (hasRecipe()) for (SomItemStack stack : getRecipe().getRecipeSlot()) {
                item.addLore(decoLore(stack.getItem().getDisplay()) + "§ax" + stack.getAmount());
            }
            return item;
        }
    }
}
