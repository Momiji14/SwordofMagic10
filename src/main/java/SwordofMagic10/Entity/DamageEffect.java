package SwordofMagic10.Entity;

public enum DamageEffect {
    None("無"),
    Fire("炎"),
    Water("水"),
    Ice("氷"),
    Elect("雷"),
    Holy("聖"),
    Dark("闇"),
    ;

    private final String display;

    DamageEffect(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
