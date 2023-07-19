package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.Component.Function.scale;
import static SwordofMagic10.Entity.StatusType.*;
import static SwordofMagic10.SomCore.SpawnLocation;

public class UserMenu extends GUIManager {
    public static final CustomItemStack DungeonRetire = new CustomItemStack(Material.REDSTONE_BLOCK).setDisplay("§cダンジョンリタイア").addLore("§aダンジョンから退場します");
    public static final CustomItemStack DungeonNPC = new CustomItemStack(Material.ENDER_EYE).setDisplay("ダンジョン入場受付").addLore("§aダンジョン入場受付へ移動します");
    public static final CustomItemStack SpawnPoint = new CustomItemStack(Material.ENDER_PEARL).setDisplay("街の中心部").addLore("§a街の中心部へ移動します");
    public static final CustomItemStack StorageIcon = new CustomItemStack(Material.ENDER_CHEST).setDisplay("ストレージ").addLore("§aストレージを開きます");
    public static final CustomItemStack EquipmentIcon = new CustomItemStack(Material.IRON_CHESTPLATE).setDisplay("装備一覧").addLore("§a装備一覧を開きます");
    public static final CustomItemStack PalletIcon = new CustomItemStack(Material.ENCHANTED_BOOK).setDisplay("パレット").addLore("§aパレット設定を開きます");
    public static final CustomItemStack QuestIcon = new CustomItemStack(Material.WRITABLE_BOOK).setDisplay("クエスト").addLore("§aクエストメニューを開きます");
    public static final CustomItemStack HelpIcon = new CustomItemStack(Material.BOOK).setDisplay("ヘルプ").addLore("§aヘルプを開きます");

    public static final CustomItemStack DamageLogIcon = new CustomItemStack(Material.RED_DYE).setDisplay("ダメージログ").addLore("§aダメージログの表示を切り替えます");
    public static final CustomItemStack ExpLogIcon = new CustomItemStack(Material.EXPERIENCE_BOTTLE).setDisplay("経験値ログ").addLore("§a経験値ログの表示を切り替えます");
    public static final CustomItemStack ItemLogIcon = new CustomItemStack(Material.CHEST).setDisplay("アイテムログ").addLore("§aアイテムログの表示を切り替えます");
    public static final CustomItemStack GatheringLogIcon = new CustomItemStack(Material.IRON_PICKAXE).setDisplay("ギャザリングログ").addLore("§aギャザリングログの表示を切り替えます");
    public static final CustomItemStack PvPModeIcon = new CustomItemStack(Material.IRON_SWORD).setDisplay("PvP").addLore("§aPvPモードを切り替えます");
    public static final CustomItemStack SkillLogIcon = new CustomItemStack(Material.END_CRYSTAL).setDisplay("スキルログ").addLore("§aスキルログの表示を切り替えます");
    public static final CustomItemStack RideAbleIcon = new CustomItemStack(Material.PLAYER_HEAD).setDisplay("ライド").addLore("§aライドを切り替えます");
    public static final CustomItemStack DashEVAIcon = new CustomItemStack(Material.FEATHER).setDisplay("ダッシュ時回避").addLore("§aダッシュ時回避を切り替えます");
    public static final CustomItemStack QuickCastIcon = new CustomItemStack(Material.CLOCK).setDisplay("クイックキャスト").addLore("§aクイックモードを切り替えます");
    public UserMenu(PlayerData playerData) {
        super(playerData, "§lユーザーメニュー", 3);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && !playerData.isDeath()) {
            if (EquipmentIcon.proximate(clickedItem)) {
                playerData.getEquipmentMenu().open();
            } else if (PalletIcon.proximate(clickedItem)) {
                playerData.getPalletMenu().open();
            } else if (StorageIcon.proximate(clickedItem)) {
                playerData.getStorageMenu().open();
            } else if (QuestIcon.proximate(clickedItem)) {
                playerData.getQuestMenu().open();
            } else if (HelpIcon.proximate(clickedItem)) {
                playerData.getHelpMenu().open();
            }

            if (DungeonRetire.proximate(clickedItem)) {
                DungeonInstance dungeon = playerData.getDungeonMenu().getDungeon();
                if (dungeon != null) {
                    if (!dungeon.isClearFlag()) {
                        playerData.getDungeonMenu().getDungeon().leavePlayer(playerData);
                    } else {
                        playerData.sendMessage("§eクリア演出中§aは§cリタイア§aできません", SomSound.Nope);
                    }
                }
            } else if (DungeonNPC.proximate(clickedItem)) {
                playerData.teleport(DungeonInstance.DungeonEnterNPC);
            } else if (SpawnPoint.proximate(clickedItem)) {
                playerData.teleport(SpawnLocation);
            }

            if (DamageLogIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleDamageLog();
            } else if (ExpLogIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleExpLog();
            } else if (ItemLogIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleItemLog();
            } else if (GatheringLogIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleGatheringLog();
            } else if (SkillLogIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleSkillErrorMessage();
            } else if (QuickCastIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleQuickCast();
            } else if (DashEVAIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleDashOnEvasion();
            } else if (RideAbleIcon.proximate(clickedItem)) {
                playerData.getSetting().toggleRideAble();
            } else if (PvPModeIcon.proximate(clickedItem)) {
                playerData.getSetting().togglePvPMode();
            }
            SomSound.Tick.play(playerData);
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
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

    @Override
    public void update() {
        clear();
        CustomItemStack status = new CustomItemStack(Material.PAINTING);
        status.setDisplay("ステータス");
        status.addLore(decoLore(Health.getDisplay()) + scale(playerData.getHealth()) + "/" + scale(playerData.getMaxHealth()));
        status.addLore(decoLore(Mana.getDisplay()) + scale(playerData.getMana()) + "/" + scale(playerData.getMaxMana()));
        for (StatusType statusType : values()) {
            switch (statusType) {
                case Health, MaxHealth, Mana, MaxMana -> {}
                case DamageResist -> status.addLore(decoLore(statusType.getDisplay()) + scale((1/playerData.getStatus(statusType))*100, 2) + "%");
                default -> status.addLore(decoLore(statusType.getDisplay()) + scale(playerData.getStatus(statusType), 2));
            }
        }

        CustomItemStack gathering = new CustomItemStack(Material.IRON_PICKAXE).setDisplay("ギャザリング");
        gathering.addLore(decoLore("労働者勤務状況") + playerData.getGatheringMenu().getWorkerList().size() + "/" + playerData.getGatheringMenu().getWorkerSlot());
        for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
            gathering.addSeparator(type.getDisplay());
            int level = playerData.getGatheringMenu().getLevel(type);
            gathering.addLore(decoLore("レベル") + level + "/" + Classes.MaxLevel);
            gathering.addLore(decoLore("経験値") + playerData.getGatheringMenu().getExp(type) + "/" + GatheringMenu.getReqExp(level));
        }

        setItem(0, StorageIcon);
        setItem(1, EquipmentIcon);
        setItem(2, PalletIcon);
        setItem(3, status);
        setItem(4, QuestIcon);
        setItem(5, gathering);
        setItem(7, HelpIcon);

        setItem(18, DamageLogIcon);
        setItem(19, ExpLogIcon);
        setItem(20, ItemLogIcon);
        setItem(21, GatheringLogIcon);
        setItem(22, SkillLogIcon);
        setItem(23, QuickCastIcon);
        setItem(24, DashEVAIcon);
        setItem(25, RideAbleIcon);
        setItem(26, PvPModeIcon);

        if (playerData.getDungeonMenu().isInDungeon()) {
            setItem(8, DungeonRetire);
        } else if (DungeonInstance.DungeonEnterNPC.distance(player.getLocation()) < 16) {
            setItem(8, SpawnPoint);
        } else {
            setItem(8, DungeonNPC);
        }
    }
}
