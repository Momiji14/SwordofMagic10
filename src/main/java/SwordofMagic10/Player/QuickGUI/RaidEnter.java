package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RaidEnter extends QuickGUI {
    private static RaidEnter raidEnter;
    public static void open(PlayerData playerData) {
        raidEnter.openIns(playerData);
    }

    public RaidEnter() {
        super(Type.RaidEnter);
        setItem(0, new CustomItemStack(Material.END_PORTAL_FRAME).setDisplay("伝説的戦闘").addLore("§aパーティ用超高難易度レイドです").addLore("§c生半可な装備と考えでは太刀打ちできません").addLore("").addLore("§e・自動復活がありません").addLore("§e・復活回数に制限があります(1人2回)").addLore("§e・リバイブの再使用時間が5分になります").setCustomData("LegendRaid", true));
        setItem(2, new CustomItemStack(Material.CALIBRATED_SCULK_SENSOR).setDisplay("防衛戦").addLore("§a防衛戦です").setCustomData("DefensiveBattle", true));
        raidEnter = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (playerData.isInCity()) {
                if (CustomItemStack.hasCustomData(clickedItem, "LegendRaid")) {
                    playerData.getDungeonMenu().open(true);
                } else if (CustomItemStack.hasCustomData(clickedItem, "DefensiveBattle")) {
                    DefensiveBattleMenu.open(playerData);
                }
            }
        }
    }
}
