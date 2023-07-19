package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.ProduceDataLoader;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Player.Classes.Classes.MaxLevel;
import static SwordofMagic10.SomCore.Log;

public class GatheringMenu extends GUIManager {

    private final static int[] ReqExp = new int[MaxLevel];
    private final static int GatheringTime = 30;

    static {
        ReqExp[0] = 100;
        for (int i = 1; i < ReqExp.length; i++) {
            ReqExp[i] = (int) Math.ceil(ReqExp[i-1] * 1.2);
        }
    }

    public static int getReqExp(int level) {
        if (MaxLevel <= level) {
            return Integer.MAX_VALUE;
        }
        return ReqExp[level - 1];
    }

    private final HashMap<SomTool.Type, SomTool> tool = new HashMap<>();
    private final HashMap<Type, Integer> level = new HashMap<>();
    private final HashMap<Type, Integer> exp = new HashMap<>();
    private final HashMap<String, Integer> gatheringItem = new HashMap<>();

    private static final int[] WorkerSlotCost = {0, 100000, 200000, 300000, 400000, 500000};
    private int workerSlot = 1;
    private final List<SomWorker> workerList = new ArrayList<>();

    public void setTool(SomTool.Type type, SomTool tool) {
        this.tool.put(type, tool);
    }

    public void equip(SomTool tool) {
        unEquip(tool.getType());
        if (playerData.getItemInventory().has(tool, 1)) {
            setTool(tool.getType(), tool);
            playerData.getItemInventory().remove(tool, 1);
        }
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
    }

    public int getExp(Type type) {
        if (!exp.containsKey(type)) exp.put(type, 0);
        return exp.get(type);
    }

    public void setExp(Type type, int exp) {
        this.exp.put(type, exp);
    }

    public void addExp(Type type, int addExp) {
        int reqExp = getReqExp(getLevel(type));
        int addLevel = 0;
        int currentExp = getExp(type);
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            reqExp = getReqExp(getLevel(type) + addLevel);
        }
        if (addLevel > 0) {
            addLevel(type, addLevel);
        }
        setExp(type, currentExp);

        if (playerData.getSetting().isGatheringLog()) {
            playerData.sendMessage("§a[GatheringExp] §e" + type.getDisplay() + "+" + addExp);
        }
    }

    public List<SomWorker> getWorkerList() {
        return workerList;
    }

    public int getWorkerSlot() {
        return workerSlot;
    }

    public HashMap<String, Integer> getGatheringItem() {
        return gatheringItem;
    }

    public CustomItemStack viewEquipment(SomTool.Type toolType) {
        SomTool tool = getTool(toolType);
        return tool != null ? tool.viewItem() : null;
    }

    public GatheringMenu(PlayerData playerData) {
        super(playerData, "労働者管理", 2);
        SomTask.timer(() -> {
            if (playerData.isPlayMode()) {
                if (playerData.getLocation().getX() < 100 && playerData.getLocation().getZ() > 175) {
                    if (!isJoin) join();
                } else if (isJoin) leave();
            }
        }, 50, 50);
        SomTask.timer(this::GatheringTick, 50, GatheringTime*20);
    }

    public void GatheringTick() {
        long lastSystemTime = System.currentTimeMillis();
        for (SomWorker worker : getWorkerList()) {
            List<List<GatheringTable>> list = new ArrayList<>();
            switch (worker.getType()) {
                case Mining -> list.addAll(Mining.Table.values());
                case Lumber -> list.addAll(Lumber.Table.values());
                case Collect -> list.addAll(Collect.Table.values());
            }
            if (list.size() > 0) {
                GatheringTable.gathering(playerData, worker.getType(), list.get(randomInt(0, list.size())), 1, worker);
                updateWorkerTick();
                updateChestTick();
            }
        }
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "BuySlot")) {
                int mel = CustomItemStack.getCustomDataInt(clickedItem, "BuySlot");
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
                worker.setType(worker.getType().next());
                playerData.sendMessage("§e業務§aを§e変更§aしました", SomSound.Tick);
            } else if (CustomItemStack.hasCustomData(clickedItem, "GatheringItem")) {
                if (getGatheringItem().values().size() > 0) {
                    for (Map.Entry<String, Integer> entry : getGatheringItem().entrySet()) {
                        playerData.getItemInventory().add(ItemDataLoader.getItemData(entry.getKey()), entry.getValue());
                    }
                    getGatheringItem().clear();
                    playerData.sendMessage("§eアイテム§aを§b回収§aしました", SomSound.Tick);
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

    }

    @Override
    public void update() {
        clear();
        int slot = updateWorkerTick();
        for (int i = slot; i < getWorkerSlot(); i++) {
            setItem(slot, new CustomItemStack(Material.IRON_BARS).setDisplay("§7空スロット"));
            slot++;
        }
        for (int i = slot; i < WorkerSlotCost.length; i++) {
            setItem(slot, new CustomItemStack(Material.BARRIER).setDisplay("§c購入スロット [" + WorkerSlotCost[i] + "メル]").setCustomData("BuySlot", WorkerSlotCost[i]));
            slot++;
        }
        updateCraftTick();
        updateChestTick();
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
        CustomItemStack craft = new CustomItemStack(Material.CRAFTING_TABLE).setDisplay("制作予約");
        for (Produce.Queue queue : playerData.getProduce().getQueues()) {
            craft.addLore("§7・§e" + queue.getProduceData().getDisplay() + "§ax" + queue.getAmount() + " §b-> §a" + queue.getProcess() + "/" + queue.getProduceData().getCost());
        }
        setItem(17, craft);
    }

    public void updateChestTick() {
        CustomItemStack gatheringItem = new CustomItemStack(Material.CHEST).setDisplay("獲得アイテム").setCustomData("GatheringItem", true);
        for (Map.Entry<String, Integer> entry : getGatheringItem().entrySet()) {
            gatheringItem.addLore("§7・§e" + entry.getKey() + "§ax" + entry.getValue());
        }
        setItem(8, gatheringItem);
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
        }
    }

    public SomJson save() {
        SomJson json = new SomJson();
        json.set("WorkerSlot", getWorkerSlot());
        for (SomWorker worker : getWorkerList()) {
            json.addArray("WorkerList", worker.toJson());
        }
        for (Map.Entry<String, Integer> entry : getGatheringItem().entrySet()) {
            json.addArray("GatheringItem", entry.getKey() + ":" + entry.getValue());
        }
        for (Type type : Type.values()) {
            json.set(type + ".Level", getLevel(type));
            json.set(type + ".Exp", getExp(type));
        }
        for (Produce.Queue queue : playerData.getProduce().getQueues()) {
            json.addArray("Produce.Queue", queue.toJson());
        }
        return json;
    }

    public void load(SomJson json) {
        workerSlot = json.getInt("WorkerSlot", getWorkerSlot());
        getWorkerList().clear();
        for (String data : json.getList("WorkerList")) {
            try {
                getWorkerList().add((SomWorker) SomItem.fromJson(new SomJson(data)));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }
        for (String data : json.getStringList("GatheringItem")) {
            try {
                String[] split = data.split(":");
                getGatheringItem().put(split[0], Integer.valueOf(split[1]));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }
        for (Type type : Type.values()) {
            setLevel(type, json.getInt(type + ".Level", 1));
            setExp(type, json.getInt(type + ".Exp", 0));
        }
        for (String data : json.getList("Produce.Queue")) {
            try {
                playerData.getProduce().getQueues().add(Produce.Queue.fromJson(new SomJson(data)));
            } catch (Exception e) {
                e.printStackTrace();
                Log("§c" + e.getMessage());
            }
        }

        if (getWorkerList().size() > 0) {
            Duration difference = Duration.between(playerData.getStatistics().getLastLogout(), LocalDateTime.now());
            if (difference.toMinutes() > 0) {
                playerData.sendMessage("§bオフライン時間分(" + difference.toMinutes() + "分)§aの§e労働§aを§e再生§aしています\n§c※完了前にログアウトすると失います", SomSound.Tick);
                new BukkitRunnable() {
                    int count = (int) Math.floor((double) difference.toSeconds() / GatheringTime);
                    int craft = (int) Math.floor((difference.toSeconds()*20.0) / Produce.CraftTick);

                    @Override
                    public void run() {
                        if (count > 0) {
                            GatheringTick();
                            count--;
                        }
                        if (craft > 0) {
                            playerData.getProduce().ProduceTick();
                            craft--;
                        }
                        if (count == 0 && craft == 0) {
                            this.cancel();
                            playerData.sendMessage("§e労働再生§aが§b完了§aしました", SomSound.Tick);
                        }
                    }
                }.runTaskTimerAsynchronously(SomCore.plugin(), 100, 2);
            }
        }
    }

    public enum Type {
        Mining("採掘", Material.IRON_PICKAXE),
        Lumber("伐採", Material.IRON_AXE),
        Collect("採集", Material.IRON_HOE),
        Produce("制作", Material.CRAFTING_TABLE),
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
    }
}
