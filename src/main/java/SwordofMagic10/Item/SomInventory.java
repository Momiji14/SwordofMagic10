package SwordofMagic10.Item;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.PlayerData;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static SwordofMagic10.Component.Function.scale;

public class SomInventory {
    private final List<SomItemStack> list = new CopyOnWriteArrayList<>();
    private final PlayerData playerData;

    public SomInventory(PlayerData playerData) {
        this.playerData = playerData;
    }

    public List<SomItemStack> getInventory() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public Optional<SomItemStack> get(SomItem item) {
        Optional<SomItemStack> optional;
        if (item instanceof SomPlus plus) {
            optional = get(item.getId(), item.getTier(), plus.getLevel(), plus.getPlus());
        } else if (item instanceof SomQuality quality) {
            optional = get(item.getId(), item.getTier(), quality.getLevel());
        } else {
            optional = get(item.getId(), item.getTier());
        }
        return optional;
    }

    public Optional<SomItemStack> get(String id, int tier) {
        return get(id, tier, 0);
    }

    public Optional<SomItemStack> get(String id, int tier, int level) {
        return get(id, tier, level, 0);
    }

    public Optional<SomItemStack> get(String id, int tier, int level, int plus) {
        List<SomItemStack> list = new ArrayList<>();
        this.list.removeIf(stack -> stack.getAmount() <= 0);
        for (SomItemStack stack : this.list) {
            SomItem item = stack.getItem();
            if (item.getId().equals(id) && (item.getTier() == tier || tier == -1)) {
                boolean bool = true;
                if (level > 0 && item instanceof SomQuality quality) {
                    if (quality.getLevel() < level) {
                        bool = false;
                    }
                }
                if (plus > 0 && item instanceof SomPlus somPlus) {
                    if (somPlus.getPlus() < plus) {
                        bool = false;
                    }
                }
                if (bool) {
                    list.add(stack);
                }
            }
        }
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            list.sort((stack1, stack2) -> {
                SomItem item1 = stack1.getItem();
                SomItem item2 = stack2.getItem();
                if (item1.getTier() == item2.getTier()) {
                    if (item1 instanceof SomQuality quality1 && item2 instanceof SomQuality quality2) {
                        if (quality1.getLevel() == quality2.getLevel()) {
                            if (item1 instanceof SomPlus plus1 && item2 instanceof SomPlus plus2) {
                                return plus1.getPlus() - plus2.getPlus();
                            }
                        } else return quality1.getLevel() - quality2.getLevel();
                    }
                }
                return item1.getTier() - item2.getTier();
            });
            return Optional.of(list.get(0));
        }
    }

    public Optional<SomItemStack> getStack(SomItem item) {
        list.removeIf(stack -> stack.getAmount() <= 0);
        for (SomItemStack stack : list) {
            if (item.equal(stack.getItem())) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }

    public void add(SomItemStack stack) {
        add(stack.getItem(), stack.getAmount());
    }

    public void add(SomItem item, int amount, boolean log) {
        add(item, amount);
        if (log) {
            playerData.sendSomText(SomText.create("§b[+]§r").add(item.toSomText(amount)));
        }
    }

    public void add(SomItem item, int amount, double logPercent) {
        add(item, amount);
        if (playerData.getSetting().isItemLog()) {
            playerData.sendSomText(SomText.create("§b[+]§r").add(item.toSomText(amount)).add(" §7(" + scale(logPercent*100, 2) + "%)"));
        }
    }

    public SomItem add(SomItem item, int amount) {
        SomItem returnItem = item;
        if (amount <= 0) return returnItem;
        if (item instanceof SomPet pet) pet.setOwner(playerData);
        switch (item.getItemCategory()) {
            case Equipment, Rune, Tool, Worker, Pet -> {
                if (playerData.getItemInventory() == this) {
                    if (size() >= playerData.getRank().getInventorySlot()) {
                        playerData.getInventoryViewer().setNextUpdateText(SomText.create("§eインベントリ§aが§c満杯§aです"), SomSound.Tick);
//                        playerData.getItemStorage().add(item, amount);
//                        playerData.getInventoryViewer().setNextUpdateText(SomText.create("§eインベントリ§aが§c満杯§aのため").add(item.toSomText(amount).add("§aは§eストレージ§aに送られました")), SomSound.Tick);
//                        return item;
                    }
                } else if (playerData.getItemStorage() == this) {
                    if (size() >= playerData.getRank().getStorageSlot()) {
                        playerData.getInventoryViewer().setNextUpdateText(SomText.create("§eストレージ§aが§c満杯§aのため").add(item.toSomText(amount).add("§aを§c一時保存§aします。§c放置§aしたままだと§4§n消滅§aします")), SomSound.Nope);
                    }
                }
                for (int i = 0; i < amount; i++) {
                    returnItem = item.clone();
                    list.add(new SomItemStack(returnItem, 1));
                }
            }
            default -> getStack(item).ifPresentOrElse(stack -> stack.addAmount(amount), () -> list.add(new SomItemStack(item, amount)));
        }
        playerData.getInventoryViewer().sort(list);
        playerData.getInventoryViewer().nextUpdate();
        return returnItem;
    }

    public void removeReq(SomItemStack stack) {
        removeReq(stack.getItem(), stack.getAmount());
    }

    public void removeReq(SomItemStack stack, int amount) {
        removeReq(stack.getItem(), stack.getAmount() * amount);
    }

    public void removeReq(SomItem item, int amount) {
        if (item instanceof SomPlus plus) {
            remove(item.getId(), amount, item.getTier(), plus.getLevel(), plus.getPlus());
        } else if (item instanceof SomQuality quality) {
            remove(item.getId(), amount, item.getTier(), quality.getLevel());
        } else {
            remove(item.getId(), amount, item.getTier());
        }
    }

    public void remove(String id, int amount, int tier) {
        remove(id, amount, tier, 0);
    }

    public void remove(String id, int amount, int tier, int level) {
        remove(id, amount, tier, level, 0);
    }

    public void remove(String id, int amount, int tier, int level, int plus) {
        get(id, tier, level, plus).ifPresent(stack -> remove(stack.getItem(), amount));
    }

    public void remove(SomItemStack stack, int amount) {
        remove(stack.getItem(), stack.getAmount() * amount);
    }

    public void remove(SomItemStack stack) {
        remove(stack.getItem(), stack.getAmount());
    }

    public void remove(SomItem item, int amount) {
        if (amount <= 0) return;
        getStack(item).ifPresent(stack -> {
            stack.removeAmount(amount);
            if (stack.getAmount() <= 0) {
                list.remove(stack);
            }
        });
        playerData.getInventoryViewer().nextUpdate();
    }

    public boolean has(SomItemStack stack, int amount) {
        return has(stack.getItem(), stack.getAmount() * amount);
    }

    public boolean has(SomItemStack stack) {
        return has(stack.getItem(), stack.getAmount());
    }

    public boolean has(SomItem item, int amount) {
        Optional<SomItemStack> stack = getStack(item);
        return stack.isPresent() && stack.get().getAmount() >= amount;
    }

    public boolean req(SomItemStack stack) {
        return req(stack.getItem(), stack.getAmount());
    }


    public boolean req(SomItemStack stack, int amount) {
        return req(stack.getItem(), stack.getAmount() * amount);
    }

    public boolean req(SomItem item, int amount) {
        Optional<SomItemStack> optional;
        if (item instanceof SomPlus plus) {
            optional = get(item.getId(), item.getTier(), plus.getLevel(), plus.getPlus());
        } else if (item instanceof SomQuality quality) {
            optional = get(item.getId(), item.getTier(), quality.getLevel());
        } else {
            optional = get(item.getId(), item.getTier());
        }
        return optional.isPresent() && optional.get().getAmount() >= amount;
    }
}
