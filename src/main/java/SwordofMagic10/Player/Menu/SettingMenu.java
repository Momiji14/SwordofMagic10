package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Setting;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class SettingMenu extends GUIManager {
    public static final CustomItemStack DamageLogIcon = new CustomItemStack(Material.RED_DYE).setDisplay("ダメージログ").addLore("§aダメージログの表示を切り替えます").setCustomData("DamageLog", true);
    public static final CustomItemStack ExpLogIcon = new CustomItemStack(Material.EXPERIENCE_BOTTLE).setDisplay("経験値ログ").addLore("§a経験値ログの表示を切り替えます").setCustomData("ExpLog", true);
    public static final CustomItemStack ItemLogIcon = new CustomItemStack(Material.CHEST).setDisplay("アイテムログ").addLore("§aアイテムログの表示を切り替えます").setCustomData("ItemLog", true);
    public static final CustomItemStack BuffLogIcon = new CustomItemStack(Material.POTION).setDisplay("バフログ").addLore("§aバフログの表示を切り替えます").setPotionColor(Color.fromRGB(255, 0,0)).setCustomData("BuffLog", true);
    public static final CustomItemStack SkillLogIcon = new CustomItemStack(Material.END_CRYSTAL).setDisplay("スキルログ").addLore("§aスキルログの表示を切り替えます").setCustomData("SkillLog", true);

    public static final CustomItemStack ClimbIcon = new CustomItemStack(Material.LADDER).setDisplay("クライム").addLore("§aクライムを切り替えます").setCustomData("Climb", true);
    public static final CustomItemStack WallKickIcon = new CustomItemStack(Material.IRON_BOOTS).setDisplay("壁キック").addLore("§a壁キックを切り替えます").setCustomData("WallKick", true);
    public static final CustomItemStack EquipmentVisibleIcon = new CustomItemStack(Material.LEATHER_CHESTPLATE).setDisplay("装備外見表示").addLore("§a装備外見表示を切り替えます").setCustomData("EquipmentVisible", true);
    public static final CustomItemStack OffhandVisibleIcon = new CustomItemStack(Material.SHIELD).setDisplay("オフハンド表示").addLore("§aオフハンド表示を切り替えます").setCustomData("OffhandVisible", true);
    public static final CustomItemStack QuickCastIcon = new CustomItemStack(Material.CLOCK).setDisplay("クイックキャスト").addLore("§aクイックモードを切り替えます").setCustomData("QuickCast", true);
    public static final CustomItemStack RideAbleIcon = new CustomItemStack(Material.PLAYER_HEAD).setDisplay("ライド").addLore("§aライドを切り替えます").setCustomData("RideAble", true);
    public static final CustomItemStack DashEVAIcon = new CustomItemStack(Material.FEATHER).setDisplay("ダッシュ時回避").addLore("§aダッシュ時回避を切り替えます").setCustomData("DashEVA", true);
    public static final CustomItemStack PvPModeIcon = new CustomItemStack(Material.IRON_SWORD).setDisplay("PvP").addLore("§aPvPモードを切り替えます").setCustomData("PvPMode", true);
    public static final CustomItemStack TalkSpeedIcon = new CustomItemStack(Material.SPRUCE_SIGN).setDisplay("トークスピード").addLore("§aNPCとの会話速度を設定します").setCustomData("TalkSpeed", true);


    public static final CustomItemStack AutoBuffIcon = new CustomItemStack(Material.DRAGON_BREATH).setDisplay("自動バフ再使用").addLore("§a自動バフ再使用を切り替えます").setCustomData("AutoBuff", true);
    public static final CustomItemStack UseBuffOnInventoryIcon = new CustomItemStack(Material.ITEM_FRAME).setDisplay("インベントリバフ使用").addLore("§aインベントリからのバフ使用を切り替えます").setCustomData("UseBuffOnInventory", true);
    public static final CustomItemStack ViewSelfNamePlateIcon = new CustomItemStack(Material.NAME_TAG).setDisplay("自身ネームプレート表示").addLore("§a自身のネームプレート表示を切り替えます").setCustomData("ViewSelfNamePlate", true);
    public static final CustomItemStack NightVisionIcon = new CustomItemStack(Material.POTION).setDisplay("暗視").addLore("§a暗視を切り替えます").setPotionColor(Color.fromRGB(0, 0, 128)).setCustomData("NightVision", true);
    public static final CustomItemStack DefeatMessageIcon = new CustomItemStack(Material.BAMBOO_SIGN).setDisplay("敗北メッセージ").addLore("§a敗北メッセージの表示を切り替えます").setCustomData("DefeatMessage", true);

    public static final CustomItemStack ParticleDensityIcon = new CustomItemStack(Material.GLOWSTONE).setDisplay("パーティクル密度[自分]").addLore("§a自分のパーティクル密度を設定します").setCustomData("ParticleDensity", true);
    public static final CustomItemStack ParticleDensityOtherIcon = new CustomItemStack(Material.GUNPOWDER).setDisplay("パーティクル密度[他人]").addLore("§a他人のパーティクル密度を設定します").setCustomData("ParticleDensityOther", true);
    public static final CustomItemStack ParticleDensityEnemyIcon = new CustomItemStack(Material.REDSTONE).setDisplay("パーティクル密度[エネミー]").addLore("§aエネミーのパーティクル密度を設定します").setCustomData("ParticleDensityEnemy", true);


    public SettingMenu(PlayerData playerData) {
        super(playerData, "設定メニュー", 3);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            Setting setting = playerData.getSetting();
            if (CustomItemStack.hasCustomData(clickedItem, "DamageLog")) {
                setting.toggleDamageLog();
            } else if (CustomItemStack.hasCustomData(clickedItem, "ExpLog")) {
                setting.toggleExpLog();
            } else if (CustomItemStack.hasCustomData(clickedItem, "ItemLog")) {
                setting.toggleItemLog();
            } else if (CustomItemStack.hasCustomData(clickedItem, "BuffLog")) {
                setting.toggleBuffLog();
            } else if (CustomItemStack.hasCustomData(clickedItem, "SkillLog")) {
                setting.toggleSkillLog();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Climb")) {
                setting.toggleClimb();
            } else if (CustomItemStack.hasCustomData(clickedItem, "WallKick")) {
                setting.toggleWallKick();
            } else if (CustomItemStack.hasCustomData(clickedItem, "EquipmentVisible")) {
                setting.toggleEquipmentVisible();
            } else if (CustomItemStack.hasCustomData(clickedItem, "OffhandVisible")) {
                setting.toggleOffhandVisible();
            } else if (CustomItemStack.hasCustomData(clickedItem, "QuickCast")) {
                setting.toggleQuickCast();
            } else if (CustomItemStack.hasCustomData(clickedItem, "DashEVA")) {
                setting.toggleDashOnEvasion();
            } else if (CustomItemStack.hasCustomData(clickedItem, "RideAble")) {
                setting.toggleRideAble();
            } else if (CustomItemStack.hasCustomData(clickedItem, "PvPMode")) {
                setting.togglePvPMode();
            } else if (CustomItemStack.hasCustomData(clickedItem, "AutoBuff")) {
                setting.toggleAutoBuff();
            } else if (CustomItemStack.hasCustomData(clickedItem, "UseBuffOnInventory")) {
                setting.toggleUseBuffOnInventory();
            } else if (CustomItemStack.hasCustomData(clickedItem, "DefeatMessage")) {
                setting.toggleDefeatMessage();
            } else if (CustomItemStack.hasCustomData(clickedItem, "ViewSelfNamePlate")) {
                setting.toggleViewSelfNamePlate();
            } else if (CustomItemStack.hasCustomData(clickedItem, "NightVision")) {
                setting.toggleNightVision();
            } else if (CustomItemStack.hasCustomData(clickedItem, "TalkSpeed")) {
                playerData.getQuestMenu().setTalkSpeed(playerData.getQuestMenu().getTalkSpeed() % 100 + 10);
            } else if (CustomItemStack.hasCustomData(clickedItem, "ParticleDensity")) {
                setting.setParticleDensity(setting.getParticleDensity() % 100 + 10);
            } else if (CustomItemStack.hasCustomData(clickedItem, "ParticleDensityOther")) {
                setting.setParticleDensityOther(setting.getParticleDensityOther() % 100 + 10);
            } else if (CustomItemStack.hasCustomData(clickedItem, "ParticleDensityEnemy")) {
                setting.setParticleDensityEnemy(setting.getParticleDensityEnemy() % 100 + 10);
            }
            SomSound.Tick.play(playerData);
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        setItem(0, ClimbIcon);
        setItem(1, WallKickIcon);
        setItem(2, EquipmentVisibleIcon);
        setItem(3, OffhandVisibleIcon);
        setItem(4, QuickCastIcon);
        setItem(5, DashEVAIcon);
        setItem(6, RideAbleIcon);
        setItem(7, PvPModeIcon);
        setItem(8, TalkSpeedIcon);

        setItem(9, AutoBuffIcon);
        setItem(10, UseBuffOnInventoryIcon);
        setItem(11, ViewSelfNamePlateIcon);
        setItem(12, NightVisionIcon);
        setItem(13, DefeatMessageIcon);

        setItem(18, DamageLogIcon);
        setItem(19, ExpLogIcon);
        setItem(20, ItemLogIcon);
        setItem(21, BuffLogIcon);
        setItem(22, SkillLogIcon);

        setItem(23, ParticleDensityIcon);
        setItem(24, ParticleDensityOtherIcon);
        setItem(25, ParticleDensityEnemyIcon);
    }
}
