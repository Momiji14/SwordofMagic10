package SwordofMagic10.Item;

import java.util.HashMap;

public class SomTool extends SomQuality {

    private Type type;
    private final HashMap<String, Double> multiply = new HashMap<>();

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getMultiply(String id) {
        return multiply.getOrDefault(id, 0.0);
    }

    public void setMultiply(String id, double value) {
        multiply.put(id, value);
    }

    public double getPower() {
        return (1 + getLevel() * 0.01) * (0.75 + getQuality()*0.5) * (1 + getTier() * 0.05);
    }

    public enum Type {
        Mining("採掘"),
        Lumber("伐採"),
        Collect("採集"),
        Fishing("漁獲"),
        Hunting("狩猟"),
        ;

        private final String display;

        Type(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
