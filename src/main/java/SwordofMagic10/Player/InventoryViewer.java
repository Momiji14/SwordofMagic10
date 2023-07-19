package SwordofMagic10.Player;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static SwordofMagic10.Component.Function.*;

public class InventoryViewer {

    private final CustomItemStack EvasionIcon = new CustomItemStack(Material.FEATHER).setDisplay("§e回避").addLore(loreText(List.of("視点方向へ回避します\nスニーク状態だと反転します\n水中だと挙動が変化します\n※無敵ではありません".split("\n"))));

    public static boolean isViewSlot(int slot) {
        return 0 <= slot && slot != 8 && slot != 17 && slot != 26 && slot < 35;
    }

    private final ReentrantReadWriteLock invRWL = new ReentrantReadWriteLock();
    private final Lock invRead = invRWL.readLock();
    private final Lock invWrite = invRWL.writeLock();
    private final PlayerData playerData;
    private final Player player;

    public InventoryViewer(PlayerData playerData) {
        this.playerData = playerData;
        player = playerData.getPlayer();
    }

    public void nextUpdate() {
        update();
    }

    private int scroll = 0;
    private CustomItemStack[] contents = new CustomItemStack[41];
    private void update() {
        if (!playerData.isLoading()) {
            invWrite.lock();
            try {
                contents = new CustomItemStack[41];
                contents[17] = Config.UpScrollIcon.clone().setAmountReturn(scroll);
                contents[26] = playerData.isBE() ? Config.UserMenuIcon_BE : Config.UserMenuIcon;
                contents[35] = Config.DownScrollIcon.clone().setAmountReturn(scrollAble()-scroll);

                int slot = 9;
                int i = 0;
                int index = scroll * 8;
                playerData.getItemInventory().readLock();
                try {
                    for (SomItemStack stack : playerData.getItemInventory().getInventory()) {
                        if (i >= index) {
                            if (slot < 36) {
                                contents[slot] = stack.viewItem().addLore(decoLore("スロット番号") + index);
                                contents[slot].setAmountReturn(stack.getAmount());
                                contents[slot].setCustomData("InventoryIndex", index);
                                index++;
                                slot++;
                                if (Math.floorMod(slot, 9) == 8) slot++;
                            } else break;
                        }
                        i++;
                    }
                } finally {
                    playerData.getItemInventory().readUnlock();
                }

                contents[8] = viewEquipment(EquipSlot.MainHand);
                contents[40] = viewEquipment(EquipSlot.OffHand);
                contents[39] = viewEquipment(EquipSlot.Helmet);
                contents[38] = viewEquipment(EquipSlot.Chest);
                contents[37] = viewEquipment(EquipSlot.Legs);
                contents[36] = viewEquipment(EquipSlot.Boots);
            } finally {
                invWrite.unlock();
                updateBar(false);
                apply(0, contents.length);
            }
        }
    }

    public void updateBar() {
        updateBar(true);
    }

    public synchronized void updateBar(boolean apply) {
        if (!playerData.isLoading()) {
            if (playerData.getClasses().getMainClass() != null) {
                if (playerData.getGatheringMenu().isJoin()) {
                    int i = 0;
                    for (SomTool.Type type : SomTool.Type.values()) {
                        contents[i] = playerData.getGatheringMenu().getToolViewItem(type);
                        i++;
                    }
                    contents[5] = null;
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
                contents[6] = new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§7アイテムスロット[" + (player.isSneaking() ? 2 : 1) + "]");
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
                        inventory.setItem(i, contents[i]);
                    }
                } else {
                    inventory.setItem(i, null);
                }
            }
        }
    }

    public CustomItemStack viewEquipment(EquipSlot equipSlot) {
        SomEquipment equipment = playerData.getEquipment(equipSlot);
        return equipment != null ? equipment.viewItem() : null;
    }


    public void clickInventory(InventoryClickEvent event) {
        invWrite.lock();
        try {
            ItemStack clickedItem = event.getCurrentItem();
            InventoryView view = event.getView();
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
                }

                if (view.getBottomInventory() == event.getClickedInventory()) {
                    if (Config.UserMenuIcon.proximate(clickedItem)) {
                        playerData.getUserMenu().open();
                    } else if (Config.UpScrollIcon.proximate(clickedItem)) {
                        removeScroll();
                    } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                        addScroll();
                    }

                    if (view.getTitle().equals("Crafting") || view.getTopInventory() == playerData.getUserMenu().getInventory()  || view.getTopInventory() == playerData.getEquipmentMenu().getInventory()) {
                        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
                            SomItem stack = playerData.getInventoryViewer().getSomItemStack(clickedItem).getItem();
                            if (stack instanceof SomEquipment equipment) {
                                playerData.equip(equipment);
                            }
                            if (stack instanceof SomTool tool) {
                                playerData.getGatheringMenu().equip(tool);
                            }
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
        } finally {
            invWrite.unlock();
        }
    }

    public int scrollAble() {
        return (int) Math.ceil(playerData.getItemInventory().size()/8.0)-3;
    }

    public void addScroll() {
        scroll = MinMax(scrollAble(), 0, scroll+1);
    }

    public void removeScroll() {
        scroll--;
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
}
