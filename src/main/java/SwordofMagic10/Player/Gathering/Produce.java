package SwordofMagic10.Player.Gathering;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ProduceDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Item.SomWorker;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.RecipeData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;
import static SwordofMagic10.SomCore.Log;

public class Produce extends GUIManager.Bar {
    public static final int CraftTick = 50;
    private static final int MaxReserve = 20;
    private int page = 1;
    private final List<Queue> queues = new ArrayList<>();
    public Produce(PlayerData playerData) {
        super(playerData, "制作加工", 6);
        SomTask.timer(this::ProduceTick, 50, CraftTick);
    }

    public void ProduceTick() {
        for (Queue queue : queues) {
            ProduceData produceData = queue.produceData;
            for (SomWorker worker : playerData.getGatheringMenu().getWorkerList()) {
                if (worker.getType() == GatheringMenu.Type.Produce) {
                    double power = 2.5 * (1+worker.getLevel(GatheringMenu.Type.Produce)*0.05);
                    double percent = power/produceData.getCost();
                    queue.process += power;
                    worker.addExp(GatheringMenu.Type.Produce, (int) (produceData.getExp()*percent));
                }
            }
            int amount = 0;
            while (queue.process > produceData.getCost()) {
                queue.amount--;
                queue.process -= produceData.getCost();
                amount++;
            }
            if (amount > 0) {
                playerData.getGatheringMenu().getGatheringItem().merge(produceData.getId(), produceData.getAmount() * amount, Integer::sum);
                playerData.getGatheringMenu().addExp(GatheringMenu.Type.Produce, produceData.getExp() * amount);
            }
            if (queue.amount <= 0) {
                queues.remove(0);
                if (queues.size() == 0) {
                    playerData.sendMessage("§aすべての§b制作予約§aが§e完了§aしました\n§e労働者管理§aから§eアイテム§aを§b回収§aしてください", SomSound.Level);
                }
            }
            playerData.getGatheringMenu().updateWorkerTick();
            playerData.getGatheringMenu().updateCraftTick();
            playerData.getGatheringMenu().updateChestTick();
            break;
        }
    }

    public int offset() {
        return (page-1) * 45;
    }

    public void addPage() {
        page = Math.min(maxPage(), page + 1);
    }

    public int maxPage() {
        return ProduceDataLoader.getProduceDataList().size()/45;
    }

    public void removePage() {
        page = Math.max(1, page - 1);
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
                    int buyAmount = amount;
                    int amount = produceData.getAmount() * buyAmount;
                    boolean fall = false;
                    List<SomText> message = new ArrayList<>();
                    message.add(SomText.create(decoText("必要リスト")));
                    RecipeData recipeData = produceData.getRecipe();
                    for (SomItemStack stack : recipeData.getRecipeSlot()) {
                        SomText itemText = SomText.create("§7・").addText(stack.getItem().toSomText(stack.getAmount()));
                        if (playerData.getItemInventory().has(stack, buyAmount)) {
                            message.add(itemText.addText("§a✔"));
                        } else {
                            message.add(itemText.addText("§c✖"));
                            fall = true;
                        }
                    }
                    if (fall) {
                        //String command = "/recipeInfo " + recipeData.getId();
                        //message.add(SomText.create().addRunCommand("§e[" + command + "]", "§e" + command, command));
                        playerData.sendSomText(message, SomSound.Nope);
                    } else {
                        for (SomWorker worker : playerData.getGatheringMenu().getWorkerList()) {
                            if (worker.getType() == GatheringMenu.Type.Produce) {
                                if (getQueues().size() < MaxReserve) {
                                    addQueue(new Queue(produceData, buyAmount));
                                    for (SomItemStack stack : produceData.getRecipe().getRecipeSlot()) {
                                        playerData.getItemInventory().remove(stack, amount);
                                    }
                                    playerData.sendMessage(item.getColorDisplay() + (amount > 1 ? "§ex" + amount : "") + "§aを§b制作予約§aに§e登録§aしました", SomSound.Tick);
                                } else {
                                    playerData.sendMessage("§e制作予約§aは§c最大" + MaxReserve + "個§aまでです", SomSound.Nope);
                                }
                                return;
                            }
                        }
                        playerData.sendMessage("§e制作業務§aを担当している§e労働者§aがいません", SomSound.Nope);
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
            if (getProduceDataList().size() > i) {
                ProduceData produceData = getProduceData(i);
                contents[slot] = produceData.viewItem().setCustomData("ProduceDataID", produceData.getId());
                slot++;
            } else break;
        }
        setContents(contents);
        if (page > 1) setItem(45, Config.DownScrollIcon);
        if (maxPage() > page) setItem(53, Config.UpScrollIcon);
        updateBar();
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
