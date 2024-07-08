package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Component.Function.scale;

public class LightsOut extends GUIManager {

    private final ProduceGame.Game LightsOut = ProduceGame.Game.LightsOut;
    public LightsOut(PlayerData playerData) {
        super(playerData, ProduceGame.Game.LightsOut.getDisplay(), 6);
    }

    private long gameStartTime;
    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            int[] xy = getSlot(slot);
            int x = xy[0];
            int y = xy[1];
            content[slot] = lightsOut(!CustomItemStack.getCustomDataBoolean(content[slot], "LightsOut"));
            if (x > 0) {
                int index = getSlot(x-1, y);
                content[index] = lightsOut(!CustomItemStack.getCustomDataBoolean(content[index], "LightsOut"));
            }
            if (x < 8) {
                int index = getSlot(x+1, y);
                content[index] = lightsOut(!CustomItemStack.getCustomDataBoolean(content[index], "LightsOut"));
            }
            if (y > 0) {
                int index = getSlot(x, y-1);
                content[index] = lightsOut(!CustomItemStack.getCustomDataBoolean(content[index], "LightsOut"));
            }
            if (y < 5) {
                int index = getSlot(x, y+1);
                content[index] = lightsOut(!CustomItemStack.getCustomDataBoolean(content[index], "LightsOut"));
            }
            setContents(content);
            for (ItemStack item : content) {
                if (!CustomItemStack.getCustomDataBoolean(item, "LightsOut")) {
                    return;
                }
            }
            playerData.getProduce().ProducePlayerTick(50);
            playerData.getStatistics().add(Statistics.Type.LightsOutClearCount, 1);
            double endTime = (System.currentTimeMillis() - gameStartTime)/1000.0;
            double time = playerData.getStatistics().get(Statistics.Type.LightsOutClearTime);
            if (time == 0 || time > endTime) {
                playerData.sendMessage("§e" + Statistics.Type.LightsOutClearTime.getDisplay() + "§aを§b更新§aしました §e[" + scale(time, 3) + " -> " + scale(endTime, 3) + "]", SomSound.Level);
                playerData.getStatistics().set(Statistics.Type.LightsOutClearTime, endTime);
            } else playerData.sendMessage("§e[" + LightsOut.getDisplay() + "] " + scale(endTime, 3) + "秒");
            for (int i = 0; i < 54; i++) {
                setItem(i, new CustomItemStack(Material.BARRIER).setNonDecoDisplay(" "));
            }
            SomTask.wait(250);
            update();
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if (playerData.getProduceGame().isInGame()) {
            playerData.getProduceGame().leaveGame();
        }
    }

    private ItemStack[] content = new ItemStack[54];
    @Override
    public void update() {
        gameStartTime = System.currentTimeMillis();
        clear();
        content = new ItemStack[54];
        boolean[] map = new boolean[54];
        Arrays.fill(map, true);
        Set<Integer> slots = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            int slot = randomInt(0, 54);
            while (slots.contains(slot)) {
                slot = randomInt(0, 54);
            }
            slots.add(slot);
        }
        for (int slot : slots) {
            int[] xy = getSlot(slot);
            int x = xy[0];
            int y = xy[1];
            map[slot] = !map[slot];
            if (x > 0) {
                int index = getSlot(x-1, y);
                map[index] = !map[index];
            }
            if (x < 8) {
                int index = getSlot(x+1, y);
                map[index] = !map[index];
            }
            if (y > 0) {
                int index = getSlot(x, y-1);
                map[index] = !map[index];
            }
            if (y < 5) {
                int index = getSlot(x, y+1);
                map[index] = !map[index];
            }
        }
        for (int i = 0; i < 54; i++) {
            content[i] = lightsOut(map[i]);
        }
        setContents(content);
    }

    public CustomItemStack lightsOut(boolean bool) {
        return new CustomItemStack(bool ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setNonDecoDisplay(" ").setCustomData("LightsOut", bool);
    }
}
