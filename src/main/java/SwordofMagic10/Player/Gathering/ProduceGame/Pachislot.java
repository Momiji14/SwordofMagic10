package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class Pachislot extends GUIManager {

    private final ProduceGame.Game Pachislot = ProduceGame.Game.Pachislot;

    private boolean rolling = false;
    private boolean[] stop = new boolean[3];
    private final int[] slotTable = new int[]{5,7,9,12,15};
    private final int[][] slot = new int[3][3];

    public Pachislot(PlayerData playerData) {
        super(playerData, "スロット", 6);
    }

    private int afkCount = 0;
    private long afkTime = 0;
    private int fever = 0;
    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (rolling) {
                if (CustomItemStack.hasCustomData(clickedItem, "Stop")) {
                    int index = CustomItemStack.getCustomDataInt(clickedItem, "Stop");
                    stop[index] = true;
                    SomSound.Tick.play(playerData);
                }
                if (CustomItemStack.hasCustomData(clickedItem, "LeftStop")) {
                    for (int i = 0; i < stop.length; i++) {
                        if (!stop[i]) {
                            stop[i] = true;
                            SomSound.Tick.play(playerData);
                            break;
                        }
                    }
                }
            } else {
                if (CustomItemStack.hasCustomData(clickedItem, "Rolling")) {
                    if (fever > 0 || !playerData.sendMessageIsAFK()) {
                        start();
                        afkCount = 0;
                    } else {
                        if (System.currentTimeMillis()-afkTime > 60000) {
                            afkCount++;
                            afkTime = System.currentTimeMillis();
                            SomCore.WarnLog(playerData, "Pachislot AFK " + afkCount + "min");
                        }
                    }
                }
            }
        }
    }

    private BukkitTask task;
    public void start() {
        SomSound.Tick.play(playerData);
        stop = new boolean[3];
        rolling = true;
        button();

        if (task != null) task.cancel();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!rolling) this.cancel();
                if (stop[0] && stop[1] && stop[2]) {
                    this.cancel();
                    new BukkitRunnable() {
                        int total = 0;
                        int per = 1;
                        boolean[][] clear = new boolean[3][3];
                        int phase = 0;
                        int chain = 0;
                        @Override
                        public void run() {
                            if (phase == 1) {
                                for (int x = 0; x < clear.length; x++) {
                                    for (int y = 0; y < clear[0].length; y++) {
                                        if (clear[x][y]) {
                                            slot[x][y] = 0;
                                        }
                                    }
                                }
                                updateView();
                                SomSound.Tick.play(playerData);
                                phase = 2;
                            } else if(phase == 2) {
                                for (int x = 0; x < clear.length; x++) {
                                    for (int y = 0; y < clear[0].length; y++) {
                                        if (clear[x][y]) {
                                            if (randomDouble(0, 1) > (fever == 0 ? 0.05 : 0.3)) {
                                                slot[x][y] = randomSlot();
                                            } else {
                                                slot[x][y] = 50;
                                            }
                                        }
                                    }
                                }
                                updateView();
                                phase = 0;
                            } else if (per > 0) {
                                per = 0;
                                clear = new boolean[3][3];
                                for (int x = 0; x < slot.length; x++) {
                                    if (check(x, 0, x, 1, x, 2)) {
                                        per += slot[x][0];
                                        chain++;
                                    }
                                }
                                for (int y = 0; y < slot.length; y++) {
                                    if (check(0, y, 1, y, 2, y)) {
                                        per += slot[0][y];
                                        chain++;
                                    }
                                }

                                if (check(0, 1, 2)) {
                                    per += slot[0][0];
                                    chain++;
                                }
                                if (check(2, 1, 0)) {
                                    per += slot[0][2];
                                    chain++;
                                }

                                total += per;
                                if (per > 0) {
                                    phase = 1;
                                }
                            } else {
                                int score = total*chain;
                                this.cancel();
                                if (score > 0) {
                                    playerData.getProduce().ProducePlayerTick(score);
                                    playerData.getStatistics().add(Statistics.Type.PachislotCount, score);
                                }
                                playerData.sendMessage("§e[" + Pachislot.getDisplay() + "] +" + total + "x" + chain + "=" + score);
                                double currentScore = playerData.getStatistics().get(Statistics.Type.Pachislot);
                                if (score > currentScore) {
                                    playerData.sendMessage("§e" + Statistics.Type.Pachislot.getDisplay() + "§aを§b更新§aしました §e[" + scale(currentScore, 0) + " -> " + score + "]", SomSound.Level);
                                    playerData.getStatistics().set(Statistics.Type.Pachislot, score);
                                }
                                rolling = false;
                                if (fever > 0) {
                                    fever--;
                                    if (fever == 0) normalFlame();
                                }
                                button();
                            }
                        }
                        private boolean check(int y, int y2, int y3) {
                            return check(0, y, 1, y2, 2, y3);
                        }
                        private boolean check(int x, int y, int x2, int y2, int x3, int y3) {
                            if (slot[x][y] == slot[x2][y2] && slot[x2][y2] == slot[x3][y3]) {
                                clear[x][y] = true;
                                clear[x2][y2] = true;
                                clear[x3][y3] = true;
                                if (slot[x][y] == 50) {
                                    if (fever == 0) {
                                        fever = 15;
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (playerData.isOnline() && fever > 0) {
                                                    feverFlame();
                                                } else {
                                                    this.cancel();
                                                    normalFlame();
                                                }
                                            }
                                        }.runTaskTimerAsynchronously(SomCore.plugin(), 3, 3);
                                    } else  fever += 3;
                                }
                                return true;
                            }
                            return false;
                        }
                    }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 10);
                } else {
                    updateTick();
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 3);
        SomSound.Tick.play(playerData);
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        fever = 0;
        if (task != null) task.cancel();
        if (playerData.getProduceGame().isInGame()) {
            rolling = false;
            playerData.getProduceGame().leaveGame();
        }
    }

    @Override
    public void open() {
        for (int x = 0; x < slot.length; x++) {
            for (int y = 0; y < slot[0].length; y++) {
                slot[x][y] = randomSlot();
            }
        }
        rolling = false;
        stop = new boolean[3];
        super.open();
    }

    public int randomSlot() {
        if (fever > 0) {
            int score = slotTable[randomInt(0, slotTable.length)];
            if (score == slotTable[0]) score = slotTable[slotTable.length-1];
            return score;
        }
        return slotTable[randomInt(0, slotTable.length)];
    }

    private final CustomItemStack[] flames = new CustomItemStack[]{
        new CustomItemStack(Material.BLUE_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.RED_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.YELLOW_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.PINK_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE).setNonDecoDisplay(" "),
        new CustomItemStack(Material.ORANGE_STAINED_GLASS_PANE).setNonDecoDisplay(" ")
    };
    @Override
    public void update() {
        updateTick();
        normalFlame();
    }

    public void normalFlame() {
        CustomItemStack flame = Config.FlameItem(playerData);
        for (int i = 0; i < 54; i++) {
            setItem(i, flame);
        }
        updateView();
        button();
    }

    public void feverFlame() {
        for (int i = 0; i < 54; i++) {
            setItem(i, flames[randomInt(0, flames.length)]);
        }
        setItem(16, new CustomItemStack(Material.EMERALD).setNonDecoDisplay("§bフィーバータイム！").addLore("§7石炭§aが§bダイヤモンド§aになります").setAmountReturn(fever));
        updateView();
        button();
    }

    public void button() {
        for (int i = 0; i < 3; i++) {
            setItem(37 + i*2, new CustomItemStack(Material.BIRCH_BUTTON).setNonDecoDisplay("§aストップ").setCustomData("Stop", i).setCustomData("Rolling", true));
        }
        if (rolling) {
            setItem(43, new CustomItemStack(Material.REDSTONE_TORCH).setNonDecoDisplay("§e左から止める").setCustomData("LeftStop", true));
        } else {
            setItem(43, new CustomItemStack(Material.LEVER).setNonDecoDisplay("§eスロットを回す").setCustomData("Rolling", true));
        }
    }

    public void updateTick() {
        for (int i = 0; i < slot.length; i++) {
            if (!stop[i]) {
                slot[i][2] = slot[i][1];
                slot[i][1] = slot[i][0];
                slot[i][0] = randomSlot();
            }
        }
        updateView();
    }

    public void updateView() {
        for (int x = 0; x < slot.length; x++) {
            for (int y = 0; y < slot[0].length; y++) {
                setItem(1+x*2, 1+y, viewSlot(slot[x][y]));
            }
        }
    }

    public CustomItemStack viewSlot(int slot) {
        CustomItemStack item = new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§ex" + slot);
        switch (slot) {
            case 5 -> item.setType(Material.COAL);
            case 7 -> item.setType(Material.COPPER_INGOT);
            case 9 -> item.setType(Material.IRON_INGOT);
            case 12 -> item.setType(Material.GOLD_INGOT);
            case 15 -> item.setType(Material.DIAMOND);
            case 50 -> item.setType(Material.EMERALD);
        }
        return item;
    }
}
