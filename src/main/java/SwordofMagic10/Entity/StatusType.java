package SwordofMagic10.Entity;

public enum StatusType {
    MaxHealth("最大体力"),
    Health("体力"),
    HealthRegen("体力回復"),
    MaxMana("最大マナ"),
    Mana("マナ"),
    ManaRegen("マナ回復"),
    ATK("攻撃力"),
    MAT("魔法力"),
    DEF("防御力"),
    MDF("魔防力"),
    SPT("支援力"),
    Hate("ヘイト率"),
    Critical("会心率"),
    CriticalDamage("会心威力"),
    CriticalResist("会心耐性"),
    Movement("移動速度"),
    CastTime("詠唱短縮"),
    RigidTime("硬直短縮"),
    CoolTime("再使用短縮"),
    DamageResist("ダメージ軽減"),
    ;

    private final String display;

    StatusType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static StatusType[] BaseStatus() {
        return new StatusType[]{MaxHealth, MaxMana, HealthRegen, ManaRegen, ATK, MAT, DEF, MDF, SPT, Critical, CriticalDamage, CriticalResist, Movement, CastTime, CoolTime, RigidTime, DamageResist};
    }
}
