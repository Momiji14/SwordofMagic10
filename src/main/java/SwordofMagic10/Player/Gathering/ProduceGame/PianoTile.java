package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static SwordofMagic10.Component.Function.*;

public class PianoTile {

    private final ProduceGame.Game PianoTile = ProduceGame.Game.PianoTile;
    private final PlayerData playerData;

    private BukkitTask task;
    private final List<Integer> notes = new CopyOnWriteArrayList<>();
    private boolean start = false;
    private long startTime = 0;
    private int count = 0;
    private int combo = 0;
    private int maxCombo = 0;
    private int miss = 0;
    private double topKps = 0;
    private double kps = 0;
    private TextDisplay display;

    public PianoTile(PlayerData playerData) {
        this.playerData = playerData;
    }

    public boolean isStart() {
        return start;
    }

    public void start() {
        playerData.closeInventory();
        playerData.getPlayer().getInventory().setHeldItemSlot(5);
        start = true;
        startTime = 0;
        combo = 0;
        count = 0;
        miss = 0;
        notes.clear();
        notes.add(randomInt(0, 4));
        topKps = 0;
        kps = 0;
        for (int i = 0; i < 6; i++) {
            notes.add(random());
        }
        if (task != null) task.cancel();
        SomTask.sync(() -> {
            if (display != null) display.remove();
            display = (TextDisplay) SomCore.World.spawnEntity(playerData.getLocation().frontHorizon(3), EntityType.TEXT_DISPLAY);
            display.setBackgroundColor(Color.fromARGB(255, 0, 0, 0));
            display.setAlignment(TextDisplay.TextAlignment.LEFT);
            display.setBillboard(Display.Billboard.FIXED);
            display.setRotation(playerData.getLocation().getYaw()-180, 0);
            display.setSeeThrough(true);
            update();

            task = new BukkitRunnable() {
                final CustomLocation location = playerData.getLocation();
                @Override
                public void run() {
                    if (playerData.isOnline()) {
                        if (!playerData.getProduceGame().check(ProduceGame.Game.PianoTile) || playerData.getLocation().distance(location) > 5) {
                            this.cancel();
                            end();
                        }
                    } else {
                        this.cancel();
                        end();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 50, 50);
        });
    }

    public TextDisplay getDisplay() {
        return display;
    }

    public int random() {
        int last = notes.get(notes.size()-1);
        if (last == 0) {
            return randomInt(last+1, 4);
        } else if (last == 3) {
            return randomInt(0, last);
        } else {
            return randomBool() ? randomInt(0, last) : randomInt(last+1, 4);
        }
    }

    private final String space = "     ";
    private final String spaceLine = space.repeat(4) + "\n";
    public void update() {
        StringBuilder builder = new StringBuilder(playerData.getDisplayName());
        builder.append("\n§e計測時間: ").append(startTime > 0 ? (System.currentTimeMillis()-startTime)/1000 : 0).append("秒");
        builder.append("\n§e最高KPS: ").append(topKps > 0 ? scale(topKps, 3) : "計測中");
        builder.append("\n§e現在KPS: ").append(kps > 0 ? scale(kps, 3) : "計測中");
        builder.append("\n§e最高コンボ: ").append(maxCombo);
        builder.append("\n§e現在コンボ: ").append(combo);
        builder.append("\n§eカウント: ").append(count);
        builder.append("\n§eミス: ").append(miss);
        builder.append("\n--------------\n");
        for (int i = notes.size(); i > 0; i--) {
            int note = notes.get(i-1);
            builder.append(spaceLine).append(space.repeat(note)).append(noteText(note)).append(space.repeat(3-note)).append(" \n");
        }
        builder.append("§e--------------");
        display.setText(builder.toString());
    }

    public String noteText(int i) {
        switch (i) {
            case 0,3 -> {
                return " §f■■■";
            }
            case 1,2 -> {
                return " §b■■■";
            }
        }
        return "";
    }

    public void tap(int key) {
        if (startTime == 0) startTime = System.currentTimeMillis();
        if (key == notes.get(0)) {
            notes.remove(0);
            notes.add(random());
            double time = (System.currentTimeMillis()-startTime)/1000.0;
            if (time > 5) {
                kps = count/time;
                topKps = Math.max(kps, topKps);
            }
            combo++;
            count++;
            maxCombo = Math.max(maxCombo, combo);
            playerData.getStatistics().add(Statistics.Type.PianoTileCount, 1);
            playerData.getProduce().ProducePlayerTick(0.75);
            SomSound.Tick.play(playerData);
        } else {
            combo = 0;
            miss++;
            playerData.getPlayer().getInventory().setHeldItemSlot(5);
            SomSound.Nope.play(playerData);
        }
        if (miss > 100 && (double) miss/count > 0.5) {
            end();
            playerData.sendMessage("§e[" + PianoTile.getDisplay() + "] " + "§cミス§aし過ぎです！(ミス率50%) §eもう少し慎重になりましょう", SomSound.Nope);
        }
        update();
    }

    public void end() {
        if (task != null) task.cancel();
        if (isStart()) {
            double currentKps = playerData.getStatistics().get(Statistics.Type.PianoTileKPS);
            if (topKps > currentKps) {
                playerData.sendMessage("§e" + Statistics.Type.PianoTileKPS.getDisplay() + "§aを§b更新§aしました §e[" + scale(currentKps, 3) + " -> " + scale(topKps, 3) + "]", SomSound.Level);
                playerData.getStatistics().set(Statistics.Type.PianoTileKPS, topKps);
            }
            int currentCombo = (int) playerData.getStatistics().get(Statistics.Type.PianoTileCombo);
            if (maxCombo > currentCombo) {
                playerData.sendMessage("§e" + Statistics.Type.PianoTileCombo.getDisplay() + "§aを§b更新§aしました §e[" + currentCombo + " -> " + maxCombo + "]", SomSound.Level);
                playerData.getStatistics().set(Statistics.Type.PianoTileCombo, maxCombo);
            }
            List<String> message = new ArrayList<>();
            message.add("§e[" + PianoTile.getDisplay() + "] " + scale(topKps, 3) + "KPS");
            message.add("§e[" + PianoTile.getDisplay() + "] " + maxCombo + "Combo");
            playerData.sendMessage(message, SomSound.Tick);
            playerData.getProduceGame().leaveGame();
            start = false;
            SomTask.sync(() -> display.remove());
        }
    }
}
