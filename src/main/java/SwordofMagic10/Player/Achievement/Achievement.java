package SwordofMagic10.Player.Achievement;

import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Achievement extends GUIManager {
    public Achievement(PlayerData playerData) {
        super(playerData, "アチーブメント", 6);
    }

    @Override
    public void topClick(InventoryClickEvent event) {

    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {

    }
}
