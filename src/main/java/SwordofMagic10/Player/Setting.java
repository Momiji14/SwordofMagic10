package SwordofMagic10.Player;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.Dungeon.DungeonMenu;
import SwordofMagic10.Player.Quest.QuestMenu;
import SwordofMagic10.Player.QuickGUI.RuneCrusher;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

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
    private int particleDensityOther = 100;
    private int particleDensityEnemy = 100;
    private boolean pvpMode = false;
    private boolean damageHolo = true;
    private boolean damageLog = true;
    private boolean expLog = true;
    private boolean itemLog = true;
    private boolean buffLog = false;
    private boolean skillLog = true;
    private boolean rideAble = true;
    private boolean quickCast = false;
    private boolean dashOnEvasion = false;
    private boolean climb = true;
    private boolean wallKick = true;
    private boolean equipmentVisible = true;
    private boolean offhandVisible = true;
    private boolean autoBuff = false;
    private boolean useBuffOnInventory = true;
    private boolean viewSelfNamePlate = true;
    private boolean defeatMessage = true;
    private boolean nightVision = false;
    private boolean runeAutoCrash = false;
    private List<String> pushStorage = new ArrayList<>();

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
        playerData.sendMessage("§eパーティクル密度[自分]§aを§e" + particleDensity + "%§aに§b設定§aしました");
    }

    public int getParticleDensityOther() {
        return particleDensityOther;
    }

    public void setParticleDensityOther(int particleDensityOther) {
        this.particleDensityOther = particleDensityOther;
        playerData.sendMessage("§eパーティクル密度[他人]§aを§e" + particleDensityOther + "%§aに§b設定§aしました");
    }

    public int getParticleDensityEnemy() {
        return particleDensityEnemy;
    }

    public void setParticleDensityEnemy(int particleDensityEnemy) {
        this.particleDensityEnemy = particleDensityEnemy;
        playerData.sendMessage("§eパーティクル密度[エネミー]§aを§e" + particleDensityEnemy + "%§aに§b設定§aしました");
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

    public boolean isDamageHolo() {
        return damageHolo;
    }

    public void setDamageHolo(boolean damageHolo) {
        this.damageHolo = damageHolo;
        playerData.sendMessage("§eダメージホロ§aを" + boolText(damageHolo) + "§aにしました");
    }

    public void toggleDamageHolo() {
        setDamageHolo(!isDamageHolo());
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

    public boolean isBuffLog() {
        return buffLog;
    }

    public void setBuffLog(boolean buffLog) {
        this.buffLog = buffLog;
        playerData.sendMessage("§eバフログ§aを" + boolText(buffLog) + "§aにしました");
    }

    public void toggleBuffLog() {
        setBuffLog(!isBuffLog());
    }

    public boolean isSkillLog() {
        return skillLog;
    }

    public void setSkillLog(boolean skillLog) {
        this.skillLog = skillLog;
        playerData.sendMessage("§eスキルログ§aを" + boolText(skillLog) + "§aにしました");
    }

    public void toggleSkillLog() {
        setSkillLog(!isSkillLog());
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

    public boolean isClimb() {
        return climb;
    }

    public void setClimb(boolean climb) {
        this.climb = climb;
        playerData.sendMessage("§eクライム§aを" + boolText(climb) + "§aにしました");
    }

    public void toggleClimb() {
        setClimb(!isClimb());
    }

    public boolean isWallKick() {
        return wallKick;
    }

    public void setWallKick(boolean wallKick) {
        this.wallKick = wallKick;
        playerData.sendMessage("§e壁キック§aを" + boolText(wallKick) + "§aにしました");
    }

    public void toggleWallKick() {
        setWallKick(!isWallKick());
    }

    public boolean isEquipmentVisible() {
        return equipmentVisible;
    }

    public void setEquipmentVisible(boolean equipmentVisible) {
        this.equipmentVisible = equipmentVisible;
        playerData.sendMessage("§e装備外見表示§aを" + boolText(equipmentVisible) + "§aにしました");
    }

    public void toggleEquipmentVisible() {
        setEquipmentVisible(!isEquipmentVisible());
    }

    public boolean isOffhandVisible() {
        return offhandVisible;
    }

    public void setOffhandVisible(boolean offhandVisible) {
        this.offhandVisible = offhandVisible;
        playerData.sendMessage("§eオフハンド表示§aを" + boolText(offhandVisible) + "§aにしました");
    }

    public void toggleOffhandVisible() {
        setOffhandVisible(!isOffhandVisible());
    }

    public boolean isAutoBuff() {
        return autoBuff;
    }

    public void setAutoBuff(boolean autoPotion) {
        this.autoBuff = autoPotion;
        playerData.sendMessage("§e自動バフ再使用§aを" + boolText(autoPotion) + "§aにしました");
    }

    public void toggleAutoBuff() {
        setAutoBuff(!isAutoBuff());
    }

    public boolean isUseBuffOnInventory() {
        return useBuffOnInventory;
    }

    public void setUseBuffOnInventory(boolean useBuffOnInventory) {
        this.useBuffOnInventory = useBuffOnInventory;
        playerData.sendMessage("§eインベントリバフ使用§aを" + boolText(useBuffOnInventory) + "§aにしました");
    }

    public void toggleUseBuffOnInventory() {
        setUseBuffOnInventory(!isUseBuffOnInventory());
    }

    public boolean isViewSelfNamePlate() {
        return viewSelfNamePlate;
    }

    public void setViewSelfNamePlate(boolean viewSelfNamePlate) {
        this.viewSelfNamePlate = viewSelfNamePlate;
        playerData.sendMessage("§e自身ネームプレート表示§aを" + boolText(viewSelfNamePlate) + "§aにしました");
    }

    public boolean isDefeatMessage() {
        return defeatMessage;
    }

    public void setDefeatMessage(boolean defeatMessage) {
        this.defeatMessage = defeatMessage;
        playerData.sendMessage("§e敗北メッセージ§aを" + boolText(defeatMessage) + "§aにしました");
    }

    public void toggleDefeatMessage() {
        setDefeatMessage(!isDefeatMessage());
    }

    public void toggleViewSelfNamePlate() {
        setViewSelfNamePlate(!isViewSelfNamePlate());
    }

    public boolean isNightVision() {
        return playerData.getPlayer().hasPotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        applyNightVision(nightVision);
        playerData.sendMessage("§e暗視§aを" + boolText(nightVision) + "§aにしました");
    }

    public void toggleNightVision() {
        setNightVision(!isNightVision());
    }

    public void applyNightVision(boolean nightVision) {
        SomTask.sync(() -> {
            if (nightVision) {
                playerData.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));
            } else {
                playerData.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        });
    }


    public boolean isRuneAutoCrash() {
        return runeAutoCrash;
    }


    public void setRuneAutoCrash(boolean runeAutoCrash) {
        if (playerData.hasRank(RuneCrusher.AutoCrash)) {
            this.runeAutoCrash = runeAutoCrash;
        } else {
            playerData.sendMessageReqRank(RuneCrusher.AutoCrash);
        }
    }

    public void toggleRuneAutoCrash() {
        setRuneAutoCrash(!isRuneAutoCrash());
    }

    public void togglePushStorage(String id) {
        if (ItemDataLoader.getComplete().contains(id)) {
            SomItem item = ItemDataLoader.getItemData(id);
            if (pushStorage.contains(id)) {
                pushStorage.remove(id);
            } else {
                pushStorage.add(id);
            }
            playerData.sendMessage(item.getColorDisplay() + "§aの§e自動搬入§aを" + boolText(pushStorage.contains(id)) + "§aしました", SomSound.Tick);
        } else {
            playerData.sendMessage("§a存在しない§eItemID§aです", SomSound.Nope);
        }
    }

    public SomJson save() {
        SomJson json = new SomJson();
        json.set("PlayMode", playMode);
        json.set("ParticleDensity", particleDensity);
        json.set("ParticleDensityOther", particleDensityOther);
        json.set("ParticleDensityEnemy", particleDensityEnemy);
        json.set("DamageHolo", damageHolo);
        json.set("DamageLog", damageLog);
        json.set("ExpLog", expLog);
        json.set("ItemLog", itemLog);
        json.set("BuffLog", buffLog);
        json.set("QuickCast", quickCast);
        json.set("DashOnEvasion", dashOnEvasion);
        json.set("SkillLog", skillLog);
        json.set("RideAble", rideAble);
        json.set("Climb", climb);
        json.set("WallKick", wallKick);
        json.set("EquipmentVisible", equipmentVisible);
        json.set("OffhandVisible", offhandVisible);
        json.set("AutoBuff", autoBuff);
        json.set("UseBuffOnInventory", useBuffOnInventory);
        json.set("ViewSelfNamePlate", viewSelfNamePlate);
        json.set("DefeatMessage", defeatMessage);
        json.set("NightVision", nightVision);
        json.set("RuneAutoCrash", runeAutoCrash);

        DungeonMenu dungeonMenu = playerData.getDungeonMenu();
        json.set("Dungeon.LaterJoin", dungeonMenu.isLaterJoin());
        json.set("Dungeon.AutoRetry", dungeonMenu.isAutoRetry());

        QuestMenu questMenu = playerData.getQuestMenu();
        json.set("Quest.TalkSpeed", questMenu.getTalkSpeed());

        InventoryViewer inventoryViewer = playerData.getInventoryViewer();

        for (int i = 0; i < inventoryViewer.getSort().length; i++) {
            json.set("InventoryViewer.Sort-" + i, inventoryViewer.getSort(i).toString());
        }
        return json;
    }

    public void load(SomJson json) {
        playMode = json.getBoolean("PlayMode", playMode);
        particleDensity = json.getInt("ParticleDensity", particleDensity);
        particleDensityOther = json.getInt("ParticleDensityOther", particleDensityOther);
        particleDensityEnemy = json.getInt("ParticleDensityEnemy", particleDensityEnemy);
        damageHolo = json.getBoolean("DamageHolo", damageHolo);
        damageLog = json.getBoolean("DamageLog", damageLog);
        expLog = json.getBoolean("ExpLog", expLog);
        itemLog = json.getBoolean("ItemLog", itemLog);
        buffLog = json.getBoolean("BuffLog", buffLog);
        dashOnEvasion = json.getBoolean("DashOnEvasion", dashOnEvasion);
        quickCast = json.getBoolean("QuickCast", quickCast);
        skillLog = json.getBoolean("SkillLog", skillLog);
        rideAble = json.getBoolean("RideAble", rideAble);
        climb = json.getBoolean("Climb", climb);
        wallKick = json.getBoolean("WallKick", wallKick);
        equipmentVisible = json.getBoolean("EquipmentVisible", equipmentVisible);
        offhandVisible = json.getBoolean("OffhandVisible", offhandVisible);
        autoBuff = json.getBoolean("AutoBuff", autoBuff);
        useBuffOnInventory = json.getBoolean("UseBuffOnInventory", useBuffOnInventory);
        viewSelfNamePlate = json.getBoolean("ViewSelfNamePlate", viewSelfNamePlate);
        defeatMessage = json.getBoolean("DefeatMessage", defeatMessage);
        applyNightVision(json.getBoolean("NightVision", nightVision));
        runeAutoCrash = json.getBoolean("RuneAutoCrash", runeAutoCrash);

        DungeonMenu dungeonMenu = playerData.getDungeonMenu();
        dungeonMenu.setLaterJoin(json.getBoolean("Dungeon.LaterJoin", dungeonMenu.isLaterJoin()));
        dungeonMenu.setAutoRetry(json.getBoolean("Dungeon.AutoRetry", dungeonMenu.isAutoRetry()));

        QuestMenu questMenu = playerData.getQuestMenu();
        questMenu.setTalkSpeedNoMessage(json.getInt("Quest.TalkSpeed", questMenu.getTalkSpeed()));

        InventoryViewer inventoryViewer = playerData.getInventoryViewer();
        for (int i = 0; i < inventoryViewer.getSort().length; i++) {
            try {
                inventoryViewer.setSortNoMessage(i, InventoryViewer.Sort.valueOf(json.getString("InventoryViewer.Sort-" + i, inventoryViewer.getSort(i).toString())));
            } catch (Exception ignore) {}
        }

    }
}
