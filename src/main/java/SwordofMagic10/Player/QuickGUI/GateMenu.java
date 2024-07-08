package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GateMenu extends QuickGUI {
    private static GateMenu gateMenu;
    public static void open(PlayerData playerData) {
        gateMenu.openIns(playerData);
    }

    public GateMenu() {
        super(Type.Gate);
        for (MapData mapData : MapDataLoader.getMapDataList()) {
            if (mapData.hasSlot()) {
                setItem(mapData.getSlot(), mapData.viewItem());
            }
        }
        gateMenu = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "MapData")) {
                MapData mapData = MapDataLoader.getMapData(CustomItemStack.getCustomData(clickedItem, "MapData"));
                if (playerData.getMapData() != mapData) {
                    if (MapClearCheck(playerData, mapData)) {
                        mapData.teleportGate(playerData);
                    }
                } else {
                    playerData.sendMessage("§e現在地§aです", SomSound.Nope);
                }
            }
        }
    }

    public static boolean MapClearCheck(PlayerData playerData, MapData mapData) {
        boolean bool = !mapData.hasPrevMap() || !mapData.getPrevMap().hasPrevMap() || playerData.getDungeonMenu().hasClearTime(mapData.getPrevMap().getId(), DungeonDifficulty.Easy);
        if (!bool) playerData.sendMessage("§e" + mapData.getPrevMap().getDisplay() + "[Easy]§aを§bクリア§aしていないため§e利用§aできません", SomSound.Nope);
        return bool;
    }
}
