package SwordofMagic10.Player;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Dungeon.DungeonMenu;
import SwordofMagic10.Player.Quest.QuestMenu;

public class Setting {

    public static String boolText(boolean bool) {
        return bool ? "§b有効" : "§c無効";
    }

    private final PlayerData playerData;

    public Setting(PlayerData playerData) {
        this.playerData = playerData;
    }

    private boolean playMode = true;
    private int particleDensity = 100;
    private boolean pvpMode = false;
    private boolean damageLog = true;
    private boolean expLog = true;
    private boolean itemLog = true;
    private boolean gatheringLog = true;
    private boolean skillErrorMessage = true;
    private boolean rideAble = true;
    private boolean quickCast = false;
    private boolean dashOnEvasion = false;

    public boolean isPlayMode() {
        return playMode;
    }

    public void setPlayMode(boolean playMode) {
        this.playMode = playMode;
    }

    public int getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
        playerData.sendMessage("§eパーティクル密度§aを§e" + particleDensity + "§aに§b設定§aしました");
    }

    public boolean isPvPMode() {
        return pvpMode;
    }

    public void setPvPMode(boolean pvpMode) {
        this.pvpMode = pvpMode;
        playerData.sendMessage("§cPvPモード§aを" + boolText(pvpMode) + "§aにしました");
    }

    public void togglePvPMode() {
        setPvPMode(!isPvPMode());
    }

    public boolean isDamageLog() {
        return damageLog;
    }

    public void setDamageLog(boolean damageLog) {
        this.damageLog = damageLog;
        playerData.sendMessage("§eダメージログ§aを" + boolText(damageLog) + "§aにしました");
    }

    public void toggleDamageLog() {
        setDamageLog(!isDamageLog());
    }

    public boolean isExpLog() {
        return expLog;
    }

    public void setExpLog(boolean expLog) {
        this.expLog = expLog;
        playerData.sendMessage("§e経験値ログ§aを" + boolText(expLog) + "§aにしました");
    }

    public void toggleExpLog() {
        setExpLog(!isExpLog());
    }

    public boolean isItemLog() {
        return itemLog;
    }

    public void setItemLog(boolean itemLog) {
        this.itemLog = itemLog;
        playerData.sendMessage("§eアイテムログ§aを" + boolText(itemLog) + "§aにしました");
    }

    public void toggleItemLog() {
        setItemLog(!isItemLog());
    }

    public boolean isGatheringLog() {
        return gatheringLog;
    }

    public void setGatheringLog(boolean gatheringLog) {
        this.gatheringLog = gatheringLog;
        playerData.sendMessage("§eギャザリングログ§aを" + boolText(gatheringLog) + "§aにしました");
    }

    public void toggleGatheringLog() {
        setGatheringLog(!isGatheringLog());
    }

    public boolean isSkillErrorMessage() {
        return skillErrorMessage;
    }

    public void setSkillErrorMessage(boolean skillErrorMessage) {
        this.skillErrorMessage = skillErrorMessage;
        playerData.sendMessage("§eスキルログ§aを" + boolText(skillErrorMessage) + "§aにしました");
    }

    public void toggleSkillErrorMessage() {
        setSkillErrorMessage(!isSkillErrorMessage());
    }

    public boolean isRideAble() {
        return rideAble;
    }

    public void setRideAble(boolean rideAble) {
        this.rideAble = rideAble;
        playerData.sendMessage("§eライド§aを" + boolText(rideAble) + "§aにしました");
    }

    public void toggleRideAble() {
        setRideAble(!isRideAble());
    }

    public boolean isDashOnEvasion() {
        return dashOnEvasion;
    }

    public void setDashOnEvasion(boolean dashOnEvasion) {
        this.dashOnEvasion = dashOnEvasion;
        playerData.sendMessage("§eダッシュ時回避§aを" + boolText(dashOnEvasion) + "§aにしました");
    }

    public void toggleDashOnEvasion() {
        setDashOnEvasion(!isDashOnEvasion());
    }

    public boolean isQuickCast() {
        return quickCast;
    }

    public void setQuickCast(boolean quickCast) {
        this.quickCast = quickCast;
        playerData.sendMessage("§eクイックキャスト§aを" + boolText(quickCast) + "§aにしました");
    }

    public void toggleQuickCast() {
        setQuickCast(!isQuickCast());
    }

    public SomJson save() {
        SomJson json = new SomJson();
        json.set("PlayMode", playMode);
        json.set("ParticleDensity", particleDensity);
        json.set("DamageLog", damageLog);
        json.set("ExpLog", expLog);
        json.set("ItemLog", itemLog);
        json.set("GatheringLog", gatheringLog);
        json.set("QuickCast", quickCast);
        json.set("DashOnEvasion", dashOnEvasion);
        json.set("SkillErrorMessage", skillErrorMessage);
        json.set("RideAble", rideAble);

        DungeonMenu dungeonMenu = playerData.getDungeonMenu();
        json.set("Dungeon.LaterJoin", dungeonMenu.isLaterJoin());
        json.set("Dungeon.PartyJoin", dungeonMenu.isPartyJoin());
        json.set("Dungeon.AutoRetry", dungeonMenu.isAutoRetry());

        QuestMenu questMenu = playerData.getQuestMenu();
        json.set("Quest.TalkSpeed", questMenu.getTalkSpeed());
        return json;
    }

    public void load(SomJson json) {
        setPlayMode(json.getBoolean("PlayMode", playMode));
        setParticleDensity(json.getInt("ParticleDensity", particleDensity));
        setDamageLog(json.getBoolean("DamageLog", damageLog));
        setExpLog(json.getBoolean("ExpLog", expLog));
        setItemLog(json.getBoolean("ItemLog", itemLog));
        setGatheringLog(json.getBoolean("GatheringLog", gatheringLog));
        setDashOnEvasion(json.getBoolean("DashOnEvasion", dashOnEvasion));
        setQuickCast(json.getBoolean("QuickCast", quickCast));
        setSkillErrorMessage(json.getBoolean("SkillErrorMessage", skillErrorMessage));
        setRideAble(json.getBoolean("RideAble", rideAble));

        DungeonMenu dungeonMenu = playerData.getDungeonMenu();
        dungeonMenu.setLaterJoin(json.getBoolean("Dungeon.LaterJoin", dungeonMenu.isLaterJoin()));
        dungeonMenu.setPartyJoin(json.getBoolean("Dungeon.PartyJoin", dungeonMenu.isPartyJoin()));
        dungeonMenu.setAutoRetry(json.getBoolean("Dungeon.AutoRetry", dungeonMenu.isAutoRetry()));

        QuestMenu questMenu = playerData.getQuestMenu();
        questMenu.setTalkSpeed(json.getInt("Quest.TalkSpeed", questMenu.getTalkSpeed()));
    }
}
