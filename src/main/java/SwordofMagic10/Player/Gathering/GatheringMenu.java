package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.*;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static SwordofMagic10.Component.Config.DateFormat;
import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Player.Classes.Classes.MaxLevel;
import static SwordofMagic10.SomCore.Log;

public class GatheringMenu extends GUIManager {

    public final static int GatheringTime = 60;
    public final static int AdjustExp = 100;
    private final static double[] ReqExp = new double[MaxLevel];
    private final static double[] Exp = new double[MaxLevel];

    static {
        ReqExp[0] = 1000;
        for (int i = 1; i < ReqExp.length; i++) {
            double multiply = 1.15;
            if (i >= 50) multiply = 1.05;
            if (i == 98) multiply = 10;
            if (i > 98) multiply = 2;
            ReqExp[i] = ReqExp[i-1] * multiply;
        }

        Exp[0] = 1000;
        for (int i = 1; i < Exp.length; i++) {
            double multiply = 1.15;
            if (i >= 50) multiply = 1.05;
            Exp[i] = Exp[i-1] * multiply;
        }
    }

    public static double getReqExp(int level) {
        if (level < 1) level = 1;
        if (MaxLevel <= level) {
            return 0.0;
        }
        return ReqExp[level - 1];
    }

    public static double getExp(int level) {
        if (level < 1) level = 1;
        if (MaxLevel <= level) return 0;
        return Exp[level-1]/AdjustExp/Math.pow(level, 1.2);
    }

    private final HashMap<SomTool.Type, SomTool> tool = new HashMap<>();
    private final HashMap<Type, Integer> level = new HashMap<>();
    private final HashMap<Type, Double> exp = new HashMap<>();
    private final CopyOnWriteArrayList<SomItemStack> gatheringItem = new CopyOnWriteArrayList<>();

    private static final int[] WorkerSlotCost = {0, 50000, 100000, 500000, 700000, 1000000};
    private int workerSlot = 1;
    private boolean reserve = false;
    private final List<SomWorker> workerList = new ArrayList<>();

    private LocalDateTime lastChestTime = LocalDateTime.now();

    public GatheringMenu(PlayerData playerData) {
        super(playerData, "労働者管理", 2);
        SomTask.timerPlayer(playerData, () -> {
            if (playerData.isPlayMode()) {
                if (playerData.getLocation().getX() < 96 && playerData.getLocation().getZ() > 175) {
                    if (!isJoin) join();
                } else if (isJoin) leave();
            }
        }, 50, 50);
        SomTask.timerPlayer(playerData, () -> {
            if (playerData.isPlayMode()) {
                if (!isReserve()) {
                    GatheringTick();
                    updateWorkerTick();
                    updateChestTick();
                }
            }
        }, 50, GatheringTime*20);
    }

    public void setTool(SomTool.Type type, SomTool tool) {
        this.tool.put(type, tool);
    }

    public void equip(SomTool tool) {
        if (hasTool(tool.getType())) {
            playerData.getItemInventory().add(getTool(tool.getType()), 1);
        }
        playerData.getItemInventory().remove(tool, 1);
        setTool(tool.getType(), tool);
    }

    public void unEquip(SomTool.Type type) {
        if (hasTool(type)) {
            playerData.getItemInventory().add(getTool(type), 1);
        }
        tool.remove(type);
    }

    public SomTool getTool(SomTool.Type type) {
        return tool.get(type);
    }

    public CustomItemStack getToolViewItem(SomTool.Type type) {
        return tool.containsKey(type) ? tool.get(type).viewItem() : new CustomItemStack(Material.BARRIER).setNonDecoDisplay("§c" + type.getDisplay());
    }

    public boolean hasTool(SomTool.Type type) {
        return tool.containsKey(type);
    }

    public int getLevel(Type type) {
        if (!level.containsKey(type)) level.put(type, 1);
        return level.get(type);
    }

    public void setLevel(Type type, int level) {
        this.level.put(type, level);
    }

    public void addLevel(Type type, int level) {
        this.level.merge(type, level, Integer::sum);
        playerData.sendMessage("§e" + type.getDisplay() + "§aが§eLv" + getLevel(type) + "§aに上がりました", SomSound.Level);
    }

    public double getExp(Type type) {
        if (!exp.containsKey(type)) exp.put(type, 0.0);
        return exp.get(type);
    }

    public float getExpPercent(Type type) {
        if (getLevel(type) >= MaxLevel) return 0f;
        return (float) (getExp(type) / GatheringMenu.getReqExp(getLevel(type)));
    }

    public void setExp(Type type, double exp) {
        this.exp.put(type, exp);
    }

    public void addExp(Type type, double multiply) {
        addExp(type, GatheringMenu.getExp(getLevel(type)) * multiply, GatheringMenu.getExp(Math.min(getLevel(type), playerData.getRawLevel())/2) * multiply);
    }

    public void addExp(Type type, double addExp, double classExp) {
        if (type == Type.Produce) {
            classExp *= 1 + playerData.getGatheringMenu().overBonus(GatheringMenu.Type.Produce);
        }
        if (getLevel(type) >= MaxLevel) {
            setExp(type, 0);
            return;
        }
        double reqExp = getReqExp(getLevel(type));
        int addLevel = 0;
        double currentExp = getExp(type);
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            int nextLevel = getLevel(type) + addLevel;
            if (MaxLevel > nextLevel) {
                reqExp = getReqExp(nextLevel);
            } else {
                reqExp = Double.MAX_VALUE;
            }
        }
        if (addLevel > 0) {
            addLevel(type, addLevel);
        }
        setExp(type, currentExp);

        if (playerData.getSetting().isExpLog()) {
            playerData.sendMessage("§a[ExpLog]§r" + type.getDisplay() + " §e+" + scale(addExp, 2));
        }

        playerData.getClasses().addExp(classExp);
    }

    public List<SomWorker> getWorkerList() {
        return workerList;
    }

    public int getWorkerSlot() {
        return workerSlot;
    }

    public CopyOnWriteArrayList<SomItemStack> getGatheringItem() {
        return gatheringItem;
    }

    public void addGatheringItem(SomItem item, int amount) {
        for (SomItemStack stack : gatheringItem) {
            if (stack.getItem().equal(item)) {
                switch (item.getItemCategory()) {
                    case Equipment, Rune, Tool, Worker -> {
                        for (int i = 0; i < amount; i++) {
                            gatheringItem.add(new SomItemStack(item, 1));
                        }
                    }
                    default -> stack.addAmount(amount);
                }
                return;
            }
        }
        gatheringItem.add(new SomItemStack(item, amount));
    }

    public void GatheringTick() {
        for (SomWorker worker : getWorkerList()) {
            List<List<GatheringTable>> list = new ArrayList<>();
            switch (worker.getType()) {
                case Mining -> list.addAll(Mining.Table.values());
                case Lumber -> list.addAll(Lumber.Table.values());
                case Collect -> list.addAll(Collect.Table.values());
                case Fishing -> list.add(Fishing.Table);
                case Hunting -> list.addAll(Hunting.Table.values());
            }
            if (!list.isEmpty()) {
                GatheringTable.gathering(playerData, worker.getType(), list.get(randomInt(0, list.size())), worker);
            }
        }
    }

    public boolean isReserve() {
        return reserve;
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (isReserve()) {
                int index = event.getSlot();
                Produce.Queue queue = playerData.getProduce().getQueues().get(index);
                playerData.getProduce().getQueues().remove(index);
                playerData.getProduce().getQueues().add(0, queue);
                playerData.sendMessage(queue.getProduceData().getDisplay() + "§aを§b優先予約§aにしました", SomSound.Tick);
            } else {
                if (CustomItemStack.hasCustomData(clickedItem, "BuySlot")) {
                    int mel = WorkerSlotCost[workerSlot];
                    if (playerData.getMel() >= mel) {
                        workerSlot++;
                        playerData.removeMel(mel);
                        playerData.sendMessage("§e労働者スロット§aを§b購入§aしました", SomSound.Level);
                    } else {
                        playerData.sendMessageNonMel();
                    }
                } else if (CustomItemStack.hasCustomData(clickedItem, "Worker")) {
                    int index = CustomItemStack.getCustomDataInt(clickedItem, "Worker");
                    SomWorker worker = getWorkerList().get(index);
                    getWorkerList().remove(worker);
                    playerData.getItemInventory().add(worker, 1);
                    playerData.sendMessage("§e労働者§aに§e休暇§aを与えました", SomSound.Tick);
                } else if (CustomItemStack.hasCustomData(clickedItem, "ChangeType")) {
                    int index = CustomItemStack.getCustomDataInt(clickedItem, "ChangeType");
                    SomWorker worker = getWorkerList().get(index);
                    if (event.getClick().isRightClick()) {
                        worker.setType(worker.getType().back());
                    } else {
                        worker.setType(worker.getType().next());
                    }
                    playerData.sendMessage("§e業務§aを§e変更§aしました", SomSound.Tick);
                } else if (CustomItemStack.hasCustomData(clickedItem, "GatheringItem")) {
                    if (!getGatheringItem().isEmpty()) {
                        for (SomItemStack stack : getGatheringItem()) {
                            SomItem item = stack.getItem().clone();
                            if (item instanceof SomQuality quality) {
                                quality.randomQuality(0, 1);
                            }
                            playerData.getItemInventory().add(item, stack.getAmount());
                        }
                        getGatheringItem().clear();
                        lastChestTime = LocalDateTime.now();
                        playerData.sendMessage("§eアイテム§aを§b回収§aしました", SomSound.Tick);
                    }
                } else if (CustomItemStack.hasCustomData(clickedItem, "Reserve")) {
                    if (playerData.getProduce().getQueues().size() >= 2) {
                        reserve = true;
                    } else {
                        playerData.sendMessage("§e優先予約§aの§e対象§aがありません", SomSound.Nope);
                    }
                }
            }
            update();
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
            SomItemStack stack = playerData.getInventoryViewer().getSomItemStack(clickedItem);
            SomItem item = stack.getItem();
            if (item instanceof SomWorker worker) {
                if (getWorkerList().size() < getWorkerSlot()) {
                    getWorkerList().add(worker);
                    playerData.getItemInventory().remove(worker, 1);
                    playerData.sendMessage("§e労働者§aを§e勤務§aさせました", SomSound.Tick);
                } else {
                    playerData.sendMessage("§e労働者スロット§aが足りません", SomSound.Nope);
                }
            } else {
                playerData.sendMessage("§e労働者権利書§aを§b選択§aしてください", SomSound.Nope);
            }
            update();
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        reserve = false;
    }

    @Override
    public void update() {
        clear();
        if (isReserve()) {
            int slot = 0;
            for (Produce.Queue queue : playerData.getProduce().getQueues()) {
                setItem(slot, queue.getProduceData().viewItem().setAmountReturn(queue.getAmount()));
                slot++;
            }
        } else {
            int slot = updateWorkerTick();
            for (int i = slot; i < getWorkerSlot(); i++) {
                setItem(slot, new CustomItemStack(Material.IRON_BARS).setDisplay("§7空スロット"));
                slot++;
            }
            for (int i = slot; i < WorkerSlotCost.length; i++) {
                setItem(slot, new CustomItemStack(Material.BARRIER).setDisplay("§c購入スロット [" + WorkerSlotCost[i] + "メル]").setCustomData("BuySlot", WorkerSlotCost[i]));
                slot++;
            }
            SomTask.delay(() -> {
                updateCraftTick();
                updateChestTick();
            }, 1);
        }
    }

    public int updateWorkerTick() {
        int slot = 0;
        for (SomWorker worker : getWorkerList()) {
            setItem(slot, worker.viewItem().setCustomData("Worker", slot));
            setItem(slot+9, worker.getType().viewItem().setCustomData("ChangeType", slot));
            slot++;
        }
        return slot;
    }

    public void updateCraftTick() {
        if (playerData.getPlayer().getOpenInventory().getTopInventory() == getInventory()) {
            CustomItemStack craft = new CustomItemStack(Material.CRAFTING_TABLE).setDisplay("制作管理");
            double power = 0;
            for (SomWorker worker : workerList) {
                if (worker.getType() == Type.Produce) power += worker.getPower(Type.Produce);
            }
            craft.addLore(decoLore("総合制作力") + scale(power*60, 2) + "/分");
            craft.addSeparator("制作予約");
            for (Produce.Queue queue : playerData.getProduce().getQueues()) {
                craft.addLore("§7・§e" + queue.getProduceData().getDisplay() + "§ex" + queue.getAmount() + " §b-> §a" + scale(queue.getProcess(), 2) + "/" + queue.getProduceData().getCost());
            }
            setItem(17, craft.setCustomData("Reserve", true));
        }
    }

    public void updateChestTick() {
        if (playerData.getPlayer().getOpenInventory().getTopInventory() == getInventory()) {
            Duration difference = Duration.between(lastChestTime, LocalDateTime.now());
            CustomItemStack gatheringItem = new CustomItemStack(Material.CHEST).setDisplay("獲得アイテム").setCustomData("GatheringItem", true);
            for (SomItemStack stack : getGatheringItem()) {
                gatheringItem.addLore("§7・§r" + stack.getItem().getColorDisplay() + "§ex" + stack.getAmount());
            }
            gatheringItem.addLore("§8経過時間: " + difference.toMinutes() + "分");
            setItem(8, gatheringItem);
        }
    }

    public double overBonus(Type main) {
        int overLevel = Math.max(0, getLevel(main)-99) * 4;
        for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
            overLevel += Math.max(0, getLevel(type)-99);
        }
        return overLevel * 0.01;
    }

    private boolean isJoin = false;

    public boolean isJoin() {
        return isJoin;
    }

    public void join() {
        if (playerData.isPlayMode()) {
            isJoin = true;
            playerData.getInventoryViewer().updateBar(true);
            SomTask.sync(() -> player.setGameMode(GameMode.SURVIVAL));
            playerData.sendMessage("§eギャザリングエリア§aに入りました");
        }
    }

    public void leave() {
        if (playerData.isPlayMode()) {
            isJoin = false;
            playerData.getInventoryViewer().updateBar(true);
            SomTask.sync(() -> player.setGameMode(GameMode.ADVENTURE));
            playerData.sendMessage("§eギャザリングエリア§aから出ました");
            playerData.closeInventory();
        }
    }

    public static final String Table = "PlayerGathering";
    private static final String[] priKey = new String[]{"UUID", "Type"};
    public SomJson save() {
        SomJson json = new SomJson();
        json.set("WorkerSlot", getWorkerSlot());
        json.set("LastChestTime", lastChestTime.format(DateFormat));
        for (SomWorker worker : getWorkerList()) {
            json.addArray("WorkerList", worker.toJson());
        }
        for (SomItemStack stack : getGatheringItem()) {
            json.addArray("GatheringItem", stack.toJson());
        }
        for (Produce.Queue queue : playerData.getProduce().getQueues()) {
            json.addArray("Produce.Queue", queue.toJson());
        }
        for (Type type : Type.values()) {
            String[] priValue = new String[]{playerData.getUUIDAsString(), type.toString()};
            SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Username", playerData.getUsername());
            SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Level", getLevel(type));
            SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Exp", getExp(type));
        }
        return json;
    }

    public void load(SomJson json) {
        workerSlot = json.getInt("WorkerSlot", getWorkerSlot());
        try {
            lastChestTime = LocalDateTime.parse(json.getString("LastChestTime"), DateFormat);
        } catch (Exception ignored) {}
        getWorkerList().clear();
        for (String data : json.getList("WorkerList")) {
            try {
                getWorkerList().add((SomWorker) SomItem.fromJson(new SomJson(data)));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }
        getGatheringItem().clear();
        for (String data : json.getList("GatheringItem")) {
            try {
                getGatheringItem().add(SomItemStack.fromJson(data));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }
        playerData.getProduce().getQueues().clear();
        for (String data : json.getList("Produce.Queue")) {
            try {
                playerData.getProduce().getQueues().add(Produce.Queue.fromJson(new SomJson(data)));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }
        for (Type type : Type.values()) {
            String[] priValue = new String[]{playerData.getUUIDAsString(), type.toString()};
            if (SomSQL.existSql(Table, priKey, priValue)) {
                setLevel(type, SomSQL.getInt(GatheringMenu.Table, priKey, priValue, "Level"));
                setExp(type, SomSQL.getDouble(GatheringMenu.Table, priKey, priValue, "Exp"));
            }
        }

        if (!getWorkerList().isEmpty()) {
            Duration difference = Duration.between(playerData.getStatistics().getLastLogout(), LocalDateTime.now());
            if (difference.toMinutes() > 0) {
                long second = Math.min(difference.toSeconds(), (long) playerData.getRank().getWorkerLimit() * 3600);
                playerData.sendMessage("§bオフライン時間分(" + (second/60) + "分)§aの§e労働§aを§e再生§aしています\n§c※完了前にログアウトすると失います", SomSound.Tick);
                SomTask.delay(() -> {
                    int count = (int) Math.floor((double) second / GatheringTime);
                    int craft = (int) Math.floor((second*20.0) / Produce.CraftTick);
                    for (int i = 0; i < count; i++) {
                        GatheringTick();
                    }
                    for (int i = 0; i < craft; i++) {
                        playerData.getProduce().ProduceWorkerTick();
                    }
                    playerData.sendMessage("§e労働再生§aが§b完了§aしました", SomSound.Tick);
                }, 200);
            }
        }
    }

    public enum Type {
        Produce("制作", Material.CRAFTING_TABLE),
        Mining("採掘", Material.IRON_PICKAXE),
        Lumber("伐採", Material.IRON_AXE),
        Collect("採集", Material.IRON_HOE),
        Fishing("漁獲", Material.FISHING_ROD),
        Hunting("狩猟", Material.BOW),
        ;

        private final String display;
        private final Material icon;

        Type(String display, Material icon) {
            this.display = display;
            this.icon = icon;
        }

        public String getDisplay() {
            return display;
        }

        public CustomItemStack viewItem() {
            return new CustomItemStack(icon).setNonDecoDisplay("§e" + getDisplay());
        }

        public Type next() {
            if (this.ordinal()+1 < Type.values().length) {
                return Type.values()[this.ordinal()+1];
            } else return Type.values()[0];
        }

        public Type back() {
            if (this.ordinal()-1 >= 0) {
                return Type.values()[this.ordinal()-1];
            } else return Type.values()[Type.values().length-1];
        }
    }
}
