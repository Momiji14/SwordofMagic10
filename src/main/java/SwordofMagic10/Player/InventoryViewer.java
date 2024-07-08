package SwordofMagic10.Player;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.*;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Player.Gathering.Produce.ProduceGameIcon;

public class InventoryViewer {

    private final CustomItemStack EvasionIcon = new CustomItemStack(Material.FEATHER).setDisplay("§e回避").addLore(loreText(List.of("視点方向へ回避します\nスニーク状態だと反転します\n水中だと挙動が変化します\n※無敵ではありません".split("\n"))));

    public static boolean isViewSlot(int slot) {
        return 0 <= slot && slot != 8 && slot != 17 && slot != 26 && slot < 35;
    }

    private final PlayerData playerData;
    private final Player player;
    private final Sort[] sort = new Sort[]{Sort.Tier, Sort.Name, Sort.Category, Sort.None};
    private boolean nextUpdate = false;
    private SomText nextUpdateText = null;
    private SomSound nextUpdateSound = null;

    public InventoryViewer(PlayerData playerData) {
        this.playerData = playerData;
        player = playerData.getPlayer();
        SomTask.timerPlayer(playerData, () -> {
            if (nextUpdate) {
                nextUpdate = false;
                if (nextUpdateText != null) {
                    playerData.sendSomText(nextUpdateText, nextUpdateSound);
                    nextUpdateText = null;
                    nextUpdateSound = null;
                }
                update();
            }
        }, 20, 3);
    }

    public Sort[] getSort() {
        return sort;
    }

    public Sort getSort(int i) {
        return sort[i];
    }

    public void setSort(int i, Sort sort) {
        this.sort[i] = sort;
        playerData.sendMessage("§eアイテムソート[" + (i+1) + "]§aを§e" + sort.getDisplay() + "§aにしました");
    }

    public void setSortNoMessage(int i, Sort sort) {
        this.sort[i] = sort;
    }

    public void nextUpdate() {
        nextUpdate = true;
    }

    public void setNextUpdateText(SomText nextUpdateText, SomSound nextUpdateSound) {
        this.nextUpdateText = nextUpdateText;
        this.nextUpdateSound = nextUpdateSound;
    }

    private int scroll = 0;
    private CustomItemStack[] contents = new CustomItemStack[41];
    private void update() {
        if (!playerData.isLoading()) {
            contents = new CustomItemStack[41];
            contents[17] = Config.UpScrollIcon.clone().setAmountReturn(scroll);
            contents[26] = playerData.isBE() ? Config.UserMenuIcon_BE : Config.UserMenuIcon;
            contents[35] = Config.DownScrollIcon.clone().setAmountReturn(scrollAble()-scroll);

            int slot = 9;
            int i = 0;
            int index = scroll * 8;
            for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                if (i >= index) {
                    if (slot < 36) {
                        contents[slot] = stack.viewItem().addLore(decoLore("スロット番号") + index);
                        contents[slot].setAmountReturn(stack.getAmount());
                        contents[slot].setCustomData("InventoryIndex", index);
                        slot++;
                        if (Math.floorMod(slot, 9) == 8) slot++;
                        index++;
                    } else break;
                }
                i++;
            }
            contents[8] = viewEquipment(EquipSlot.MainHand);
            if (playerData.getSetting().isOffhandVisible()) contents[40] = viewEquipment(EquipSlot.OffHand);
            if (playerData.getSetting().isEquipmentVisible()) {
                contents[39] = viewEquipment(EquipSlot.Helmet);
                contents[38] = viewEquipment(EquipSlot.Chest);
                contents[37] = viewEquipment(EquipSlot.Legs);
                contents[36] = viewEquipment(EquipSlot.Boots);
            }
            updateBar(false);
            apply(0, contents.length);
        }
    }

    public void updateBar() {
        updateBar(true);
    }

    public void updateBar(boolean apply) {
        if (!playerData.isLoading()) {
            if (playerData.isTutorialClear()) {
                if (playerData.getGatheringMenu().isJoin()) {
                    int i = 0;
                    for (SomTool.Type type : SomTool.Type.values()) {
                        contents[i] = playerData.getGatheringMenu().getToolViewItem(type);
                        i++;
                    }
                    contents[5] = ProduceGameIcon;
                } else {
                    int offset = playerData.getPalletMenu().getPallet().length / 2;
                    for (int i = 0; i < offset; i++) {
                        int pallet = player.isSneaking() ? offset + i : i;
                        SomSkill skill = playerData.getPalletMenu().getPallet()[pallet];
                        CustomItemStack viewItem;
                        if (skill != null) {
                            viewItem = skill.viewItem(playerData.getLevel());
                            if (skill.getCurrentStack() <= 0) {
                                viewItem.setIcon(Material.NETHER_STAR);
                                viewItem.setAmount(playerData.getSkillManager().getCoolTime(skill) / 20 + 1);
                            } else {
                                viewItem.setAmount(skill.getCurrentStack());
                            }
                        } else {
                            viewItem = Config.NonePallet(pallet);
                        }
                        contents[i] = viewItem;
                    }
                }
                SomPotion itemPallet = playerData.getPalletMenu().getItemPallet()[player.isSneaking() ? 1 : 0];
                contents[6] = itemPallet != null ? itemPallet.viewItem() : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§7アイテムスロット[" + (player.isSneaking() ? 2 : 1) + "]");
                contents[7] = playerData.isEvasionAble() ? EvasionIcon : EvasionIcon.clone().setIcon(Material.NETHER_STAR);
                if (apply) apply(0, 8);
            }
        }
    }

    public void apply(int start, int end) {
        if (playerData.isPlayMode()) {
            PlayerInventory inventory = player.getInventory();
            for (int i = start; i < end; i++) {
                if (contents[i] != null) {
                    if (!contents[i].equals(inventory.getItem(i))) {
                        if (playerData.isBE()) {
                            if (contents[i].getType() == Material.POTION) {
                                contents[i].setType(Material.DRAGON_BREATH);
                            }
                        }
                        inventory.setItem(i, contents[i]);
                    }
                } else {
                    inventory.setItem(i, null);
                }
            }
        }
    }

    public CustomItemStack viewEquipment(EquipSlot equipSlot) {
        SomEquip equipItem = playerData.getEquipment(equipSlot);
        return equipItem != null ? equipItem.viewItem() : null;
    }


    public void clickInventory(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        InventoryView view = event.getView();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            for (GUIManager guiManager : playerData.getGuiManagerList()) {
                if (view.getTitle().equalsIgnoreCase(guiManager.getDisplay())) {
                    if (view.getTopInventory() == event.getClickedInventory()) {
                        guiManager.topClick(event);
                    } else if (view.getBottomInventory() == event.getClickedInventory()) {
                        guiManager.bottomClick(event);
                    }
                }
                if (guiManager instanceof GUIManager.Sub sub) {
                    if (view.getTitle().equalsIgnoreCase(sub.subDisplay())) {
                        if (view.getTopInventory() == event.getClickedInventory()) {
                            sub.subTopClick(event);
                        } else if (view.getBottomInventory() == event.getClickedInventory()) {
                            sub.subBottomClick(event);
                        }
                    }
                }
            }

            if (view.getBottomInventory() == event.getClickedInventory()) {
                if (Config.UserMenuIcon.proximate(clickedItem)) {
                    playerData.getUserMenu().open();
                } else if (Config.UpScrollIcon.proximate(clickedItem)) {
                    removeScroll(clickType.isShiftClick() ? 10 : 1);
                } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                    addScroll(clickType.isShiftClick() ? 10 : 1);
                }

                boolean openUserMenu = view.getTopInventory() == playerData.getUserMenu().getInventory();
                if (view.getTitle().equals("Crafting") || openUserMenu) {
                    if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
                        SomItem item = playerData.getInventoryViewer().getSomItemStack(clickedItem).getItem();
                        if (item instanceof SomEquip equip) {
                            playerData.equip(equip);
                        }
                        if (item instanceof SomTool tool) {
                            playerData.getGatheringMenu().equip(tool);
                        }
                        if (item instanceof SomPotion potion) {
                            if (playerData.getSetting().isUseBuffOnInventory()) {
                                potion.use(playerData);
                            } else {
                                playerData.sendMessage("§eインベントリバフ使用§aが§c無効§aになっています", SomSound.Nope);
                            }
                        }
                        if (item instanceof SomRecord record) {
                            record.use(playerData);
                        }
                        if (item instanceof SomPet pet) {
                            if (pet.isSummon()) {
                                pet.death();
                            } else {
                                pet.summon(playerData.getLocation());
                            }
                        }
                        if (openUserMenu) playerData.getUserMenu().update();
                    }
                }

                switch (slot) {
                    case 0, 1, 2, 3, 4, 5, 6 -> {
                        if (playerData.getGatheringMenu().isJoin()) {
                            playerData.getEquipmentMenu().open();
                        } else {
                            playerData.getPalletMenu().open();
                        }
                    }
                    case 8 -> playerData.unEquip(EquipSlot.MainHand);
                    case 40 -> playerData.unEquip(EquipSlot.OffHand);
                    case 39 -> playerData.unEquip(EquipSlot.Helmet);
                    case 38 -> playerData.unEquip(EquipSlot.Chest);
                    case 37 -> playerData.unEquip(EquipSlot.Legs);
                    case 36 -> playerData.unEquip(EquipSlot.Boots);
                }
            }
            player.setItemOnCursor(Config.AirItem);
            nextUpdate();
        }
    }

    public int scrollAble() {
        return (int) Math.ceil(playerData.getItemInventory().size()/8.0)-3;
    }

    public void addScroll(int i) {
        scroll = MinMax(scrollAble(), 0, scroll+i);
    }

    public void removeScroll(int i) {
        scroll -= i;
        if (scroll < 0) scroll = 0;
    }

    public int getScroll() {
        return scroll;
    }

    public boolean isSomItemStack(ItemStack item) {
        return CustomItemStack.hasCustomData(item, "InventoryIndex");
    }

    public SomItemStack getSomItemStack(ItemStack item) {
        return playerData.getItemInventory().getInventory().get(CustomItemStack.getCustomDataInt(item, "InventoryIndex"));
    }

    public void sort(List<SomItemStack> list) {
        for (Sort sort : sort) {
            try {
                switch (sort) {
                    case Category -> list.sort(Comparator.comparing(stack -> stack.getItem().getItemCategory()));
                    case Name -> list.sort(Comparator.comparing(SomItemStack::sortName));
                    case Amount -> list.sort(Comparator.comparing(SomItemStack::getAmount).reversed());
                    case Mel -> list.sort(Comparator.comparingInt(SomItemStack::sortSell).reversed());
                    case Tier -> list.sort(Comparator.comparingInt(SomItemStack::sortTier).reversed());
                    case Level -> list.sort(Comparator.comparingInt(SomItemStack::sortLevel).reversed());
                    case Quality -> list.sort(Comparator.comparingDouble(SomItemStack::sortQuality).reversed());
                    case EquipmentCategory -> list.sort(Comparator.comparing(SomItemStack::sortEquipmentCategory));
                    case EquipmentExp -> list.sort(Comparator.comparingInt(SomItemStack::sortEquipmentExp).reversed());
                    case RunePower -> list.sort(Comparator.comparingDouble(SomItemStack::sortRunePower).reversed());
                    case Favorite -> list.sort(Comparator.comparing(SomItemStack::sortFavorite).reversed());
                }
            } catch (Exception e) {
                playerData.sendMessage("§cアイテムソートが正しく行えませんでした\n§c別の順序でソートしてください", SomSound.Nope);
            }
        }
    }

    public enum Sort {
        Category("カテゴリ", Material.CRAFTING_TABLE),
        Name("名前", Material.NAME_TAG),
        Amount("個数", Material.CHEST),
        Mel("売値", Material.GOLD_NUGGET),
        Tier("ティア", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
        Level("レベル", Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE),
        Quality("品質", Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
        EquipmentCategory("装備カテゴリ", Material.IRON_CHESTPLATE),
        EquipmentExp("精錬値", Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
        RunePower("ルーン力", Material.FRIEND_POTTERY_SHERD),
        Favorite("お気に入り", Material.APPLE),
        None("無ソート", Material.BARRIER),
        ;

        private final String display;
        private final Material icon;

        Sort(String display, Material icon) {
            this.display = display;
            this.icon = icon;
        }

        public String getDisplay() {
            return display;
        }

        public CustomItemStack viewItem() {
            return new CustomItemStack(icon).setNonDecoDisplay("§e" + display).setCustomData("Sort", toString());
        }

        public Sort next() {
            if (this.ordinal()+1 < Sort.values().length) {
                return Sort.values()[this.ordinal()+1];
            } else return Sort.values()[0];
        }

        public Sort back() {
            if (this.ordinal()-1 >= 0) {
                return Sort.values()[this.ordinal()-1];
            } else return Sort.values()[Sort.values().length-1];
        }
    }
}
