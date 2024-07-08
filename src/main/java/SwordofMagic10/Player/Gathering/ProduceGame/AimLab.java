package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Component.Function.scale;

public class AimLab extends GUIManager {

    private final ProduceGame.Game AimLab = ProduceGame.Game.AimLab;
    public AimLab(PlayerData playerData) {
        super(playerData, ProduceGame.Game.AimLab.getDisplay(), 6);
    }

    private long gameStartTime = 0;
    private int aimLabCount = 0;
    private int aimLabMissCount = 0;
    private double aimLabMaxCPS = 0;
    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "AimLab")) {
                if (gameStartTime == 0) gameStartTime = System.currentTimeMillis();
                aimLabCount++;
                content[slot] = Config.FlameItem;
                double count = CustomItemStack.getCustomDataDouble(clickedItem, "AimLab");
                playerData.getProduce().ProducePlayerTick(count);
                playerData.getStatistics().add(Statistics.Type.AimLabClick, 1);
                SomSound.Tick.play(playerData);
                setItem(slot, Config.FlameItem);
                SomTask.delay(this::aimLab, 10);
                SomSound.Tick.play(playerData);
            } else {
                aimLabMissCount++;
                SomSound.Nope.play(playerData);
            }
            double millis = (System.currentTimeMillis() - gameStartTime) / 1000.0;
            if (millis > 5) {
                double cps = aimLabCount/millis;
                double miss = aimLabMissCount/millis;
                double currentCPS = aimLabMaxCPS;
                if (cps > currentCPS) {
                    aimLabMaxCPS = cps;
                }
                if (miss > 10) {
                    playerData.sendMessage("§cミス§aしすぎたため§c中断§aします");
                    playerData.closeInventory();
                    SomCore.WarnLog(playerData, "AimLab Miss " + miss);
                }
                if (cps+miss > 20) {
                    SomCore.WarnLog(playerData, "AimLab CPS " + (cps+miss));
                }
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
        aimLabCount = 0;
        aimLabMissCount = 0;
        aimLabMaxCPS = 0;
        gameStartTime = 0;
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if (playerData.getProduceGame().isInGame()) {
            double endTime = (System.currentTimeMillis() - gameStartTime) / 1000.0;
            if (endTime > 5) {
                double currentCPS = playerData.getStatistics().get(Statistics.Type.AimLabCPS);
                if (aimLabMaxCPS > currentCPS) {
                    playerData.sendMessage("§e" + Statistics.Type.AimLabCPS.getDisplay() + "§aを§b更新§aしました §e[" + scale(currentCPS, 3) + " -> " + scale(aimLabMaxCPS, 3) + "]", SomSound.Level);
                    playerData.getStatistics().set(Statistics.Type.AimLabCPS, aimLabMaxCPS);
                } else playerData.sendMessage("§e[" + AimLab.getDisplay() + "] " + scale(aimLabMaxCPS, 3) + "CPS");
            } else playerData.sendMessage("§e[" + AimLab.getDisplay() + "] §cCPSは5秒以上のAimLabでのみ集計されます");
            playerData.getProduceGame().leaveGame();
        }
    }

    private ItemStack[] content = new ItemStack[54];
    @Override
    public void update() {
        clear();
        content = new ItemStack[54];
        CustomItemStack flame = Config.FlameItem;
        Arrays.fill(content, flame);
        for (int i = 0; i < 7; i++) {
            aimLab();
        }
        setContents(content);
    }

    public void aimLab() {
        double power = playerData.getProduce().getPower();
        int slot = randomInt(0, 54);
        while (content[slot] != Config.FlameItem) {
            slot = randomInt(0, 54);
        }
        if (randomDouble(0, 1) < 0.025) {
            power *= 5;
            content[slot] = new CustomItemStack(Material.JUKEBOX).setNonDecoDisplay("§e制作進行+" + scale(power, 2)).setCustomData("AimLab", 6.5);
        } else {
            content[slot] = new CustomItemStack(Material.CRAFTING_TABLE).setNonDecoDisplay("§e制作進行+" + scale(power, 2)).setCustomData("AimLab", 1.3);
        }
        setItem(slot, content[slot]);
    }
}
