package SwordofMagic10.Entity;

public enum DamageEffect {
    None("無"),
    Fire("炎"),
    Ice("氷"),
    Holy("聖"),
    ;

    private final String display;

    DamageEffect(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
