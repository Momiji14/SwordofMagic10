package SwordofMagic10.Item;

import SwordofMagic10.DataBase.RecipeDataLoader;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.Shop.RecipeData;

import java.util.ArrayList;
import java.util.HashMap;

public class SomTool extends SomPlus implements Cloneable {

    private Type type;
    private final HashMap<String, Double> multiply = new HashMap<>();
    private double power = 1;
    private String remake;
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getMultiply(String id) {
        return multiply.getOrDefault(id, 1.0);
    }

    public void setMultiply(String id, double value) {
        multiply.put(id, value);
    }

    public HashMap<String, Double> getMultiply() {
        return multiply;
    }

    public double getPower() {
        return Math.pow(10, power-1) * (1 + getLevel() * 0.01) * (0.75 + getQuality()*0.5) * Math.pow(1.1, getTier()-1) * (1 + getPlus() * 0.1);
    }

    public double getRawPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public boolean hasRemake() {
        return remake != null;
    }

    public RecipeData getRemake() {
        return RecipeDataLoader.getRecipeData(remake);
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

    @Override
    public SomTool clone() {
        SomTool clone = (SomTool) super.clone();
        clone.setLevel(getLevel());
        clone.setExp(getExp());
        clone.setQuality(getQuality());
        return clone;
    }

    public enum Type {
        Mining("採掘", GatheringMenu.Type.Mining),
        Lumber("伐採", GatheringMenu.Type.Lumber),
        Collect("採集", GatheringMenu.Type.Collect),
        Fishing("漁獲", GatheringMenu.Type.Fishing),
        Hunting("狩猟", GatheringMenu.Type.Hunting),
        ;

        private final String display;
        private final GatheringMenu.Type gathering;

        Type(String display, GatheringMenu.Type gathering) {
            this.display = display;
            this.gathering = gathering;
        }

        public String getDisplay() {
            return display;
        }

        public GatheringMenu.Type getGathering() {
            return gathering;
        }
    }
}
