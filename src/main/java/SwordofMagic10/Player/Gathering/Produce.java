package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ProduceDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomQuality;
import SwordofMagic10.Item.SomWorker;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.RecipeData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static SwordofMagic10.Component.Function.decoText;
import static SwordofMagic10.Component.Function.randomDouble;


public class Produce extends GUIManager.Bar {
    public static CustomItemStack ProduceGameIcon = new CustomItemStack(Material.CRAFTING_TABLE).setDisplay("手動制作").addLore("§a自身でアイテムを製作します").addLore("§a※ミニゲームが開始されます").setCustomData("ProduceGame", true);
    public static final int CraftTick = 20;
    private static final int MaxReserve = 18;
    private int page = 0;
    private final List<Queue> queues = new ArrayList<>();
    private final HashMap<SomItem.ItemCategory, List<ProduceData>> dataList = new HashMap<>();
    private SomItem.ItemCategory category;
    private final ProduceSelect select;
    public Produce(PlayerData playerData) {
        super(playerData, "制作加工", 6);
        select = new ProduceSelect(playerData, this);
        SomTask.timerPlayer(playerData, () -> {
            if (playerData.isPlayMode()) {
                ProduceWorkerTick();
                if (!playerData.getGatheringMenu().isReserve()) {
                    playerData.getGatheringMenu().updateWorkerTick();
                    playerData.getGatheringMenu().updateCraftTick();
                    playerData.getGatheringMenu().updateChestTick();
                }
            }
        }, 50, CraftTick);
    }

    public ProduceSelect select() {
        return select;
    }
    private double playerPowerCount = 0;
    public void ProduceWorkerTick() {
        int size = 0;
        for (Queue queue : queues) {
            size++;
            ProduceData produceData = queue.produceData;
            double reqProcess =  queue.amount * produceData.getCost();
            if (playerPowerCount >= 1) {
                int count = 0;
                while (playerPowerCount >= 1) {
                    playerPowerCount -= 1.0;
                    if (queue.process <= reqProcess) {
                        count++;
                    } else break;
                    queue.process += getPower() * CraftTick/20.0;
                }
                if (count > 0) {
                    if (queue.getProduceData().getItem().getItemCategory() == SomItem.ItemCategory.Potion) {
                        count *= 2;
                    }
                    playerData.getGatheringMenu().addExp(GatheringMenu.Type.Produce, count);
                }
            }
            if (queue.process <= reqProcess) {
                for (SomWorker worker : playerData.getGatheringMenu().getWorkerList()) {
                    if (worker.getType() == GatheringMenu.Type.Produce) {
                        double power = worker.getPower(GatheringMenu.Type.Produce) * CraftTick/20.0;
                        queue.process += power;
                        int level = worker.getLevel(GatheringMenu.Type.Produce);
                        worker.addExp(GatheringMenu.Type.Produce, GatheringMenu.getExp(level)*0.2);
                        if (queue.process > reqProcess) break;
                    }
                }
            }
            int amount = 0;
            while (queue.process >= produceData.getCost()) {
                queue.amount--;
                queue.process -= produceData.getCost();
                amount++;
                if (randomDouble(0, 1) < playerData.getGatheringMenu().overBonus(GatheringMenu.Type.Produce)) amount++;
                if (queue.amount <= 0) {
                    playerData.sendSomText(queue.produceData.getItem().toSomText().add("§aの§b制作予約§aが§e完了§aしました"), SomSound.Level);
                    break;
                }
            }
            if (amount > 0) {
                SomItem item = produceData.getItem().clone();
                if (item instanceof SomQuality quality) {
                    int level = playerData.getGatheringMenu().getLevel(GatheringMenu.Type.Produce);
                    for (SomWorker worker : playerData.getGatheringMenu().getWorkerList()) {
                        if (worker.getType() == GatheringMenu.Type.Produce) {
                            level = Math.max(level, worker.getLevel(GatheringMenu.Type.Produce));
                        }
                    }
                    quality.setLevel(level);
                }
                playerData.getGatheringMenu().addGatheringItem(item, produceData.getAmount() * amount);
            }
            if (queues.size() >= size && playerPowerCount >= 1) continue;
            break;
        }
        if (!queues.isEmpty()) {
            queues.removeIf(queueData -> queueData.amount <= 0);
            if (queues.isEmpty()) {
                playerData.sendMessage("§aすべての§b制作予約§aが§e完了§aしました\n§e労働者管理§aから§eアイテム§aを§b回収§aしてください", SomSound.Level);
            }
        }
    }
    public void ProducePlayerTick(double multiply) {
        if (!queues.isEmpty()) {
            playerPowerCount += multiply;
        }
    }

    public double getPower() {
        return 3 + playerData.getGatheringMenu().getLevel(GatheringMenu.Type.Produce) * 0.15;
    }


    public int offset() {
        return page * 45;
    }

    public void addPage() {
        page = Math.min(maxPage(), page + 1);
    }

    public int maxPage() {
        return (int) Math.floor(dataList.get(category).size()/45.0);
    }

    public void removePage() {
        page = Math.max(0, page - 1);
    }


    public ProduceData getProduceData(int slot) {
        return ProduceDataLoader.getProduceDataList().get(slot);
    }

    public List<ProduceData> getProduceDataList() {
        return ProduceDataLoader.getProduceDataList();
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void addQueue(Queue queue) {
        queues.add(queue);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();
        if (clickedItem != null) {
            if (Config.UpScrollIcon.proximate(clickedItem)) {
                addPage();
                update();
            } else if (Config.DownScrollIcon.proximate(clickedItem)) {
                removePage();
                update();
            } else if (slot < 45) {
                ProduceData produceData = ProduceDataLoader.getProduceData(CustomItemStack.getCustomData(clickedItem,"ProduceDataID"));
                if (produceData != null) {
                    SomItem item = produceData.getItem();
                    RecipeData recipeData = produceData.getRecipe();
                    int buyAmount;
                    if (event.isShiftClick()) {
                        buyAmount = Integer.MAX_VALUE;
                        for (SomItemStack stack : recipeData.getRecipeSlot()) {
                            Optional<SomItemStack> optional = playerData.getItemInventory().getStack(stack.getItem());
                            if (optional.isPresent()) {
                                buyAmount = Math.min(optional.get().getAmount() / stack.getAmount(), buyAmount);
                            }
                        }
                        if (buyAmount < 1) buyAmount = 1;
                    } else {
                        buyAmount = amount;
                    }
                    boolean fall = false;
                    List<SomText> message = new ArrayList<>();
                    message.add(SomText.create(decoText("必要リスト")));
                    for (SomItemStack stack : recipeData.getRecipeSlot()) {
                        SomItem itemData = stack.getItem();
                        SomText itemText = SomText.create("§7・").add(itemData.toSomText(stack.getAmount() * buyAmount));
                        if (playerData.getItemInventory().req(stack, buyAmount)) {
                            message.add(itemText.add("§a✔"));
                        } else {
                            message.add(itemText.add("§c✖"));
                            fall = true;
                        }
                    }
                    if (fall) {
                        //String command = "/recipeInfo " + recipeData.getId();
                        //message.add(SomText.create().addRunCommand("§e[" + command + "]", "§e" + command, command));
                        playerData.sendSomText(message, SomSound.Nope);
                    } else {
                        if (getQueues().size() < MaxReserve) {
                            addQueue(new Queue(produceData, buyAmount));
                            for (SomItemStack stack : produceData.getRecipe().getRecipeSlot()) {
                                playerData.getItemInventory().removeReq(stack, buyAmount);
                            }
                            playerData.sendMessage(item.getColorDisplay() + (buyAmount > 1 ? "§ex" + buyAmount : "") + "§aを§b制作予約§aに§e登録§aしました", SomSound.Tick);
                            for (SomWorker worker : playerData.getGatheringMenu().getWorkerList()) {
                                if (worker.getType() == GatheringMenu.Type.Produce) return;
                            }
                            playerData.sendMessage("§e制作業務§aを担当している§e労働者§aがいないため§e手動制作§aを行ってください", SomSound.Tick);
                        } else {
                            playerData.sendMessage("§e制作予約§aは§c最大" + MaxReserve + "個§aまでです", SomSound.Nope);
                        }
                    }
                }
            } else {
                barClick(event);
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        ItemStack[] contents = new ItemStack[54];
        int offset = offset();
        int slot = 0;
        for (int i = offset; i < offset + 45; i++) {
            if (dataList.get(category).size() > i) {
                ProduceData produceData = dataList.get(category).get(i);
                contents[slot] = produceData.viewItem().setAmountReturn(produceData.getAmount()).setCustomData("ProduceDataID", produceData.getId());
                slot++;
            } else break;
        }
        setContents(contents);
        updateBar();
        if (page > 0) setItem(45, Config.DownScrollIcon);
        if (maxPage() > page) setItem(53, Config.UpScrollIcon);
    }

    public static class ProduceSelect extends GUIManager {

        private final Produce produce;
        public ProduceSelect(PlayerData playerData, Produce produce) {
            super(playerData, "制作カテゴリ", 3);
            this.produce = produce;
            List<SomItem.ItemCategory> categories = new ArrayList<>();
            for (ProduceData produceData : ProduceDataLoader.getProduceDataList()) {
                SomItem.ItemCategory category = produceData.getItem().getItemCategory();
                if (!produce.dataList.containsKey(category)) produce.dataList.put(category, new ArrayList<>());
                produce.dataList.get(category).add(produceData);
                if (!categories.contains(category)) {
                    categories.add(category);
                }
            }
            categories.sort(Comparator.comparing(category -> category));
            int slot = 0;
            for (SomItem.ItemCategory category : categories) {
                setItem(slot, category.viewItem());
                slot++;
            }
            setItem(26, ProduceGameIcon);
        }

        @Override
        public void topClick(InventoryClickEvent event) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                if (CustomItemStack.hasCustomData(clickedItem, "Category")) {
                    produce.category = SomItem.ItemCategory.valueOf(CustomItemStack.getCustomData(clickedItem, "Category"));
                    produce.open();
                } else if (CustomItemStack.hasCustomData(clickedItem, "ProduceGame")) {
                    playerData.getProduceGame().open();
                }
            }
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

    public static class Queue {
        private final ProduceData produceData;
        private int amount;
        private double process = 0;

        public Queue(ProduceData produceData, int amount) {
            this.produceData = produceData;
            this.amount = amount;
        }

        public ProduceData getProduceData() {
            return produceData;
        }

        public int getAmount() {
            return amount;
        }

        public double getProcess() {
            return process;
        }

        public SomJson toJson() {
            SomJson json = new SomJson();
            json.set("Id", produceData.getId());
            json.set("Amount", amount);
            json.set("Process", process);
            return json;
        }

        public static Queue fromJson(SomJson json) {
            Queue queue = new Queue(ProduceDataLoader.getProduceData(json.getString("Id")), json.getInt("Amount", 0));
            queue.process = json.getDouble("Process", 0.0);
            return queue;
        }
    }
}
