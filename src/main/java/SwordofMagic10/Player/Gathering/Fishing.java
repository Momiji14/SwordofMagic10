package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.entity.FishHook;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Player.Gathering.Fishing.Combo.Centre;
import static SwordofMagic10.Player.Gathering.Fishing.Combo.Rim;

public class Fishing {
    private static final List<GatheringTable> baseTable = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.025));
        add(new GatheringTable(ItemDataLoader.getItemData("品質変更石"), 0.0025));
        add(new GatheringTable(ItemDataLoader.getItemData("思い出の断片"), 0.01));
    }};

    public static final List<GatheringTable> Table = new ArrayList<>(baseTable) {{
        add(new GatheringTable(ItemDataLoader.getItemData("フグ"), 0.2));
        add(new GatheringTable(ItemDataLoader.getItemData("熱帯魚"), 0.2));
        add(new GatheringTable(ItemDataLoader.getItemData("生鮭"), 0.2));
        add(new GatheringTable(ItemDataLoader.getItemData("生鱈"), 0.2));
        add(new GatheringTable(ItemDataLoader.getItemData("昆布"), 0.2));
    }};

    private final PlayerData playerData;
    private boolean fishing = false;
    private final List<Combo> combo = new CopyOnWriteArrayList<>();
    private long startFishing = 0;
    private static final int max = 7;

    public Fishing(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void waitFishing() {
        combo.clear();
        for (int i = 0; i < max; i++) {
            Combo addCombo = randomDouble(0, 1) > 0.5 ? Centre : Rim;
            combo.add(addCombo);
        }
        int equal = 0;
        for (int i = 1; i < max; i++) {
            if (combo.get(i-1) == combo.get(i)) {
                equal++;
            }
            if (equal > 3) {
                combo.set(i, combo.get(i).next());
                equal = 0;
            }
        }
        StringBuilder builder = new StringBuilder();
        StringBuilder space = new StringBuilder();
        for (Combo combo : combo) {
            space.append(" ").append("§7").append(combo.getDisplay());
            builder.append("  ");
        }
        playerData.sendTitle(builder.append(space).toString(), "", 0, 100, 0);
    }

    public void startFishing(FishHook hook) {
        fishing = true;
        startFishing = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.isOnline() && fishing) {
                    sendTitle();
                } else {
                    this.cancel();
                    hook.remove();
                }
            }
        }.runTaskTimer(SomCore.plugin(), 1, 1);
    }

    public void sendTitle() {
        StringBuilder builder = new StringBuilder();
        StringBuilder space = new StringBuilder();
        for (Combo combo : combo) {
            space.append(" ").append(combo.getColor()).append(combo.getDisplay());
            builder.append("  ");
        }
        playerData.sendTitle(builder.append(space).toString(), "§e" + scale(cps(), 3) + "CPS", 0, 3, 0);
    }

    public double cps() {
        return max/((System.currentTimeMillis()-startFishing)/1000.0);
    }

    public void comboFishing(Combo clickCombo) {
        if (fishing && !combo.isEmpty()) {
            if (combo.get(0) == clickCombo) {
                combo.remove(0);
                SomSound.Tick.play(playerData);
                sendTitle();
                if (combo.isEmpty()) {
                    SomTool tool = playerData.getGatheringMenu().getTool(SomTool.Type.Fishing);
                    playerData.getStatistics().add(Statistics.Type.FishingCount, 1);
                    GatheringTable.gathering(playerData, GatheringMenu.Type.Fishing, Table, tool);
                    fishing = false;
                    double cps = cps();
                    playerData.sendMessage("§a[Fishing] §e" + scale(cps, 3) + "CPS");
                    double currentCPS = playerData.getStatistics().get(Statistics.Type.FishingCPS);
                    if (cps > currentCPS) {
                        playerData.getStatistics().set(Statistics.Type.FishingCPS, cps);
                        playerData.sendMessage("§e" + Statistics.Type.FishingCPS.getDisplay() + "§aを§b更新§aしました §e[" + scale(currentCPS, 3) + " -> " + scale(cps, 3) + "]", SomSound.Level);
                    }
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    combo.add(Combo.values()[randomInt(0, Combo.values().length)]);
                }
                SomSound.Nope.play(playerData);
            }
        }
    }

    public boolean isFishing() {
        return fishing;
    }

    public void setFishing(boolean fishing) {
        this.fishing = fishing;
    }

    public enum Combo {
        Centre("L", "§b"),
        Rim("R", "§c"),
        ;

        private final String display;
        private final String color;

        Combo(String display, String color) {
            this.display = display;
            this.color = color;
        }

        public String getDisplay() {
            return display;
        }

        public String getColor() {
            return color;
        }

        public Combo next() {
            if (this == Centre) {
                return Rim;
            } else {
                return Centre;
            }
        }
    }
}
