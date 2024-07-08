package SwordofMagic10.Player.Skill;

public enum SkillParameterType {
    Damage("ダメージ", "", "%", 0, 100),
    HeadDamage("ヘッドショット", "+", "%", 0, 100),
    Heal("ヒール", "", "%", 0, 100),
    Reach("射程", "", "m", 1),
    Radius("範囲", "", "m", 1),
    Angle("角度", "", "°", 0),
    Duration("持続時間", "", "秒", 1),
    Interval("間隔", "", "秒", 1),
    Percent("確率", "", "%", 0, 100),
    Count("回数", "", "回", 0, 1),
    ManaPasser("マナ供給", "", "%", 0, 100),
    DivineMight("加算割合", "", "%", 0, 100),
    DivineMightMin("最低加算", "", "秒", 1, 1),
    DiscernEvil("加算割合", "", "%", 0, 100),
    DiscernEvilMin("最低加算", "", "秒", 1, 1),
    HarmonyPreetable("追加ダメージ", "+", "%", 0, 100),
    FireBlindly("追加ダメージ", "+", "%", 0, 100),
    Pass("短縮割合", "-", "%", 0, 100),
    ;

    private final String display;
    private final String prefix;
    private final String suffix;
    private final int digit;
    private final double displayMultiply;

    SkillParameterType(String display, String prefix, String suffix, int digit) {
        this.display = display;
        this.prefix = prefix;
        this.suffix = suffix;
        this.digit = digit;
        this.displayMultiply = 1;
    }

    SkillParameterType(String display, String prefix, String suffix, int digit, double displayMultiply) {
        this.display = display;
        this.prefix = prefix;
        this.suffix = suffix;
        this.digit = digit;
        this.displayMultiply = displayMultiply;
    }

    public String getDisplay() {
        return display;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getDigit() {
        return digit;
    }

    public double getDisplayMultiply() {
        return displayMultiply;
    }
}
