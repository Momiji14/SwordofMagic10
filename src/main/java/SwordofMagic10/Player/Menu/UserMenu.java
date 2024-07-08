package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.Item.SomEquip;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.QuickGUI.RuneCrusher;
import SwordofMagic10.Player.Shop.SellManager;
import SwordofMagic10.Player.Skill.SkillGroup;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Entity.StatusType.*;
import static SwordofMagic10.SomCore.SpawnLocation;

public class UserMenu extends GUIManager {
    public static final CustomItemStack DungeonRetire = new CustomItemStack(Material.REDSTONE_BLOCK).setDisplay("§cダンジョンリタイア").addLore("§aダンジョンから退場します").setCustomData("DungeonRetire", true);
    public static final CustomItemStack DungeonGate = new CustomItemStack(Material.ENDER_EYE).setDisplay("ダンジョンゲート").addLore("§aダンジョンゲートへ移動します").setCustomData("DungeonGate", true);
    public static final CustomItemStack SpawnPoint = new CustomItemStack(Material.ENDER_PEARL).setDisplay("街の中心部").addLore("§a街の中心部へ移動します").setCustomData("SpawnPoint", true);
    public static final CustomItemStack StorageIcon = new CustomItemStack(Material.ENDER_CHEST).setDisplay("ストレージ").addLore("§aストレージを開きます").setCustomData("Storage", true);
    public static final CustomItemStack EquipmentIcon = new CustomItemStack(Material.IRON_CHESTPLATE).setDisplay("装備一覧").addLore("§a装備一覧を開きます").setCustomData("Equipment", true);
    public static final CustomItemStack PalletIcon = new CustomItemStack(Material.ENCHANTED_BOOK).setDisplay("パレット").addLore("§aパレット設定を開きます").setCustomData("Pallet", true);
    public static final CustomItemStack QuestIcon = new CustomItemStack(Material.WRITABLE_BOOK).setDisplay("クエスト").addLore("§aクエストメニューを開きます").setCustomData("Quest", true);
    public static final CustomItemStack HelpIcon = new CustomItemStack(Material.BOOK).setDisplay("ヘルプ").addLore("§aヘルプを開きます").setCustomData("Help", true);
    public static final CustomItemStack SettingIcon = new CustomItemStack(Material.COMMAND_BLOCK).setDisplay("設定一覧").addLore("§a設定一覧を開きます").setCustomData("Setting", true);
    public static final CustomItemStack MarketIcon = new CustomItemStack(Material.CHEST_MINECART).setDisplay("マーケット").addLore("§aマーケットを開きます").setCustomData("Market", true);
    public static final CustomItemStack OrderIcon = new CustomItemStack(Material.HOPPER_MINECART).setDisplay("オーダー").addLore("§aオーダーを開きます").setCustomData("Order", true);


    public static final CustomItemStack RuneCrusherIcon = new CustomItemStack(Material.PISTON).setDisplay("ルーン自動粉砕").addLore("§eルーン§aドロップ時に§b自動§aで§e粉砕§aします").addLore("§c※利用するには" + RuneCrusher.AutoCrash.getDisplay() + "&cが必要です").setCustomData("RuneCrusher", true);
    public static final CustomItemStack BuyerIcon = new CustomItemStack(Material.GOLD_NUGGET).setDisplay("買取屋").addLore("§aどこでも§e買取屋§aが開けます").addLore("§c※利用するには" + SellManager.AnyWhereRank.getDisplay() + "&cが必要です").setCustomData("Buyer", true);
    public static final CustomItemStack RuneSynthesisIcon = new CustomItemStack(Material.FRIEND_POTTERY_SHERD).setDisplay("ルーン合成").addLore("§aどこでも§eルーン合成§aが開けます").addLore("§c※利用するには" + RuneSynthesis.AnyWhereRank.getDisplay() + "&cが必要です").setCustomData("RuneSynthesis", true);
    public static final CustomItemStack EnhanceIcon = new CustomItemStack(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).setDisplay("アイテム強化").addLore("§aどこでも§eアイテム強化§aが開けます").addLore("§c※利用するには" + EnhanceMenu.AnyWhereRank.getDisplay() + "&cが必要です").setCustomData("Enhance", true);
    public static final CustomItemStack UpgradeIcon = new CustomItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).setDisplay("アイテム精錬").addLore("§aどこでも§eアイテム精錬§aが開けます").addLore("§c※利用するには" + UpgradeMenu.AnyWhereRank.getDisplay() + "&cが必要です").setCustomData("Upgrade", true);
    public UserMenu(PlayerData playerData) {
        super(playerData, "§lユーザーメニュー", 5);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && !playerData.isDeath()) {
            if (CustomItemStack.hasCustomData(clickedItem, "EquipSlot")) {
                EquipSlot equipSlot = EquipSlot.valueOf(CustomItemStack.getCustomData(clickedItem, "EquipSlot"));
                playerData.unEquip(equipSlot);
                update();
            }
            if (CustomItemStack.hasCustomData(clickedItem, "ToolType")) {
                SomTool.Type type = SomTool.Type.valueOf(CustomItemStack.getCustomData(clickedItem, "ToolType"));
                playerData.getGatheringMenu().unEquip(type);
                update();
            }

            if (CustomItemStack.hasCustomData(clickedItem, "Equipment")) {
                playerData.getEquipmentMenu().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Pallet")) {
                playerData.getPalletMenu().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Storage")) {
                playerData.getStorageMenu().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Quest")) {
                playerData.getQuestMenu().getQuestReceive().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Help")) {
                playerData.getHelpMenu().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Setting")) {
                playerData.getSettingMenu().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Market")) {
                playerData.getMarket().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Order")) {
                playerData.getOrder().open();
            } else if (CustomItemStack.hasCustomData(clickedItem, "RuneCrasher")) {
                playerData.getSetting().toggleRuneAutoCrash();
            } else if (CustomItemStack.hasCustomData(clickedItem, "Buyer")) {
                if (playerData.hasRank(SellManager.AnyWhereRank)) {
                    playerData.getSellManager().open();
                } else playerData.sendMessageReqRank(SellManager.AnyWhereRank);
            } else if (CustomItemStack.hasCustomData(clickedItem, "RuneSynthesis")) {
                if (playerData.hasRank(RuneSynthesis.AnyWhereRank)) {
                    playerData.getRuneSynthesis().open();
                } else playerData.sendMessageReqRank(RuneSynthesis.AnyWhereRank);
            } else if (CustomItemStack.hasCustomData(clickedItem, "Enhance")) {
                if (playerData.hasRank(EnhanceMenu.AnyWhereRank)) {
                    playerData.getEnhanceMenu().open();
                } else playerData.sendMessageReqRank(EnhanceMenu.AnyWhereRank);
            } else if (CustomItemStack.hasCustomData(clickedItem, "Upgrade")) {
                if (playerData.hasRank(UpgradeMenu.AnyWhereRank)) {
                    playerData.getUpgradeMenu().open();
                } else playerData.sendMessageReqRank(UpgradeMenu.AnyWhereRank);
            }

            if (CustomItemStack.hasCustomData(clickedItem, "DungeonRetire")) {
                if (playerData.getDungeonMenu().isInDungeon()) {
                    if (!playerData.getDungeonMenu().getDungeon().isClearFlag()) {
                        playerData.getDungeonMenu().getDungeon().leavePlayer(playerData);
                    } else {
                        playerData.sendMessage("§eクリア演出中§aは§cリタイア§aできません", SomSound.Nope);
                    }
                } else if (playerData.isInDefensiveBattle()) {
                    playerData.getDefensiveBattle().resetPlayer(playerData);
                } else {
                    MapDataLoader.getMapData("Alaine").teleportGoal(playerData);
                }
            } else if (CustomItemStack.hasCustomData(clickedItem, "DungeonGate")) {
                playerData.teleport(DungeonInstance.DungeonEnter);
            } else if (CustomItemStack.hasCustomData(clickedItem, "SpawnPoint")) {
                if (playerData.isInCity()) {
                    playerData.teleport(SpawnLocation);
                } else {
                    playerData.sendMessage("§cダンジョン内§aでは§e利用§aできません", SomSound.Nope);
                }
            }
            SomSound.Tick.play(playerData);
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
        if (!playerData.isTutorialClear()) {
            playerData.sendMessage("§c現在は開けません", SomSound.Nope);
            return;
        }
        if (playerData.getDungeonMenu().isInDungeon()) {
            if (playerData.getDungeonMenu().getDungeon().isClearFlag()) {
                return;
            }
        }
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    private final CustomItemStack NoFlame = new CustomItemStack(Material.BARRIER).setNonDecoDisplay(" ");
    @Override
    public void update() {
        CustomItemStack flame = Config.FlameItem(playerData);
        setItem(0, flame);
        setItem(1, flame);
        for (int i = 0; i < 6; i++) {
            setItem(30+i, flame);
        }
        for (int i = 0; i < 5; i++) {
            setItem(2+i*9, flame);
        }
        for (int i = 0; i < 2; i++) {
            setItem(7+i*9, flame);
        }

        setItem(3, statusViewItem(playerData));
        setItem(4, otherViewItem(playerData));
        setItem(5, playerData.getStatistics().viewItem());
        setItem(6, MarketIcon);
        setItem(7, OrderIcon);


        setItem(12, StorageIcon);
        setItem(13, PalletIcon);
        setItem(14, QuestIcon);
        setItem(15, SettingIcon);
        setItem(16, HelpIcon);

        setItem(21, RuneCrusherIcon);
        setItem(22, BuyerIcon);
        setItem(23, RuneSynthesisIcon);
        setItem(24, EnhanceIcon);
        setItem(25, UpgradeIcon);

        if (playerData.isTutorialClear()) {
            if (playerData.isInCity()) {
                setItem(8, DungeonGate);
            } else {
                setItem(8, DungeonRetire);
            }
        } else setItem(8, NoFlame);
        setItem(17, SpawnPoint);

        int slot = 9;
        setItem(slot, viewEquipment(EquipSlot.MainHand));
        setItem(slot+9, viewEquipment(EquipSlot.OffHand));
        setItem(slot+18, viewEquipment(EquipSlot.Amulet));
        setItem(slot+27, viewEquipment(EquipSlot.AlchemyStone));
        setItem(slot+1, viewEquipment(EquipSlot.Helmet));
        setItem(slot+10, viewEquipment(EquipSlot.Chest));
        setItem(slot+19, viewEquipment(EquipSlot.Legs));
        setItem(slot+28, viewEquipment(EquipSlot.Boots));

        slot = 40;
        setItem(slot-1, gatheringViewItem(playerData));
        for (SomTool.Type toolType : SomTool.Type.values()) {
            CustomItemStack item = playerData.getGatheringMenu().getToolViewItem(toolType).setCustomData("ToolType", toolType.toString());
            setItem(slot, item);
            slot++;
        }
    }

    public CustomItemStack viewEquipment(EquipSlot equipSlot) {
        SomEquip equipItem = playerData.getEquipment(equipSlot);
        return equipItem != null ? equipItem.viewItem().setCustomData("EquipSlot", equipSlot.toString()) : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c" + equipSlot.getDisplay());
    }

    public static CustomItemStack statusViewItem(PlayerData playerData) {
        CustomItemStack status = new CustomItemStack(Material.PAINTING);
        status.setDisplay("ステータス");
        status.addLore(decoLore(Health.getDisplay()) + scale(playerData.getHealth()) + "/" + scale(playerData.getMaxHealth()));
        status.addLore(decoLore(Mana.getDisplay()) + scale(playerData.getMana()) + "/" + scale(playerData.getMaxMana()));
        for (StatusType statusType : values()) {
            switch (statusType) {
                case Health, MaxHealth, Mana, MaxMana -> {}
                case DamageMultiply -> status.addLore(decoLore(statusType.getDisplay()) + scale((playerData.getStatus(statusType)-1)*100, 2) + "%");
                case DamageResist, CastTime, CoolTime, RigidTime -> status.addLore(decoLore(statusType.getDisplay()) + scale((1-1.0/playerData.getStatus(statusType))*100, 2) + "%");
                default -> status.addLore(decoLore(statusType.getDisplay()) + scale(playerData.getStatus(statusType), 2));
            }
        }
        return status;
    }

    public static CustomItemStack gatheringViewItem(PlayerData playerData) {
        CustomItemStack gathering = new CustomItemStack(Material.CRAFTING_TABLE).setDisplay("ギャザリング");
        gathering.addLore(decoLore("労働者勤務状況") + playerData.getGatheringMenu().getWorkerList().size() + "/" + playerData.getGatheringMenu().getWorkerSlot());
        for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
            gathering.addSeparator(type.getDisplay());
            int level = playerData.getGatheringMenu().getLevel(type);
            gathering.addLore(decoLore("レベル") + level + "/" + Classes.MaxLevel);
            gathering.addLore(decoLore("経験値") + scale(playerData.getGatheringMenu().getExp(type)) + "/" + scale(GatheringMenu.getReqExp(level)));
            SomTool.Type toolType;
            switch (type) {
                case Mining -> toolType = SomTool.Type.Mining;
                case Lumber -> toolType = SomTool.Type.Lumber;
                case Collect -> toolType = SomTool.Type.Collect;
                case Fishing -> toolType = SomTool.Type.Fishing;
                case Hunting -> toolType = SomTool.Type.Hunting;
                default -> toolType = null;
            }
            if (toolType != null) {
                double power = playerData.getGatheringMenu().getLevel(type)*0.1;
                if (playerData.getGatheringMenu().hasTool(toolType)) power += playerData.getGatheringMenu().getTool(toolType).getPower();
                double bonus = playerData.getGatheringMenu().overBonus(type);
                gathering.addLore(decoLore(type.getDisplay() + "力") + scale(power*100) + (bonus > 0 ? "§c" : "§8") + "+" + scale(bonus*100, 1) + "%");
            } else {
                gathering.addLore(decoLore(GatheringMenu.Type.Produce.getDisplay() + "力") + scale(playerData.getProduce().getPower()*100));
            }
        }
        return gathering;
    }

    public static CustomItemStack otherViewItem(PlayerData playerData) {
        CustomItemStack otherItem = new CustomItemStack(Material.END_CRYSTAL).setDisplay("プレイヤー情報");
        otherItem.addLore(decoLore("ランク") + playerData.getRank().getDisplay());
        otherItem.addLore(decoLore("所持メル") + playerData.getMel() + "メル");
        otherItem.addLore(decoLore("場所") + playerData.getMapData().getDisplay() + (playerData.getDungeonMenu().isInDungeon() ? "[" + playerData.getDungeonMenu().getDungeon().getMatchType().getDisplay() + "]" : "[フィールド]"));
        otherItem.addLore(decoLore("インベントリ") + playerData.getItemInventory().size() + "/" + playerData.getRank().getInventorySlot());
        otherItem.addLore(decoLore("ストレージ") + playerData.getItemStorage().size() + "/" + playerData.getRank().getStorageSlot());
        otherItem.addSeparator("クラス");
        Classes classes = playerData.getClasses();
        for (ClassType classType : ClassType.values()) {
            otherItem.addLore(classType.getColorDisplay() + " §eLv" + classes.getLevel(classType) + " §a" + scale(classes.getExpPercent(classType)*100, 2) + "%");
        }
        otherItem.addSeparator("スキルグループ");
        for (ClassType classType : ClassType.values()) {
            StringBuilder text = new StringBuilder();
            List<SkillGroup> list = classes.getSkillGroup(classType);
            for (SkillGroup skillGroup : list) {
                text.append(skillGroup.getNick()).append("/");
            }
            text.deleteCharAt(text.length()-1);
            otherItem.addLore(classType.getColorDisplay() + " " + text);
        }
        return otherItem;
    }
}
