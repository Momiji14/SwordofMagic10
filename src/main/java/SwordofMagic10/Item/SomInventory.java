package SwordofMagic10.Item;

import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.RecipeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SomInventory {

    private final ReentrantReadWriteLock invRWL = new ReentrantReadWriteLock();
    private final Lock invRead = invRWL.readLock();
    private final Lock invWrite = invRWL.writeLock();
    private final List<SomItemStack> list = new ArrayList<>();
    private final PlayerData playerData;

    public SomInventory(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void readLock() {
        invRead.lock();
    }

    public void readUnlock() {
        invRead.unlock();
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

    public Optional<SomItemStack> getStack(SomItem item) {
        invRead.lock();
        try {
            list.removeIf(stack -> stack.getAmount() <= 0);
            for (SomItemStack stack : list) {
                if (item.equal(stack.getItem())) {
                    return Optional.of(stack);
                }
            }
            return Optional.empty();
        } finally {
            invRead.unlock();
        }
    }

    public void add(SomItemStack stack) {
        add(stack.getItem(), stack.getAmount());
    }

    public void add(SomItem item, int amount) {
        if (amount <= 0) return;
        invWrite.lock();
        try {
            switch (item.getItemCategory()) {
                case Equipment, Rune, Worker -> {
                    for (int i = 0; i < amount; i++) {
                        list.add(new SomItemStack(item, 1));
                    }
                }
                default -> getStack(item).ifPresentOrElse(stack -> stack.addAmount(amount), () -> list.add(new SomItemStack(item, amount)));
            }
            playerData.getInventoryViewer().nextUpdate();
        } finally {
            invWrite.unlock();
        }
    }

    public void remove(SomItemStack stack, int amount) {
        remove(stack.getItem(), stack.getAmount() * amount);
    }

    public void remove(SomItemStack stack) {
        remove(stack.getItem(), stack.getAmount());
    }

    public void remove(SomItem item, int amount) {
        if (amount <= 0) return;
        invWrite.lock();
        try {
            getStack(item).ifPresent(stack -> {
                stack.removeAmount(amount);
                if (stack.getAmount() <= 0) {
                    list.remove(stack);
                }
            });
            playerData.getInventoryViewer().nextUpdate();
        } finally {
            invWrite.unlock();
        }
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
}
