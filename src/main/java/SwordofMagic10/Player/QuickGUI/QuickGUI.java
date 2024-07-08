package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.Function;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public abstract class QuickGUI {

    private static final HashMap<Type, QuickGUI> list = new HashMap<>();

    public static Collection<QuickGUI> getList() {
        return list.values();
    }

    public static void load() {
        new WeaponSupply();
        new RaidEnter();
        new DefensiveBattleMenu();
        new GateMenu();
        new EquipmentSmith();
        new AmuletTrade();
        new RuneCrusher();
    }

    public enum Type {
        WeaponSupply("武器支給", 1),
        RaidEnter("レイド入場", 1),
        DefensiveBattle("防衛戦", 1),
        Gate("転移門", 1),
        EquipmentSmith("鍛冶屋", 1),
        AmuletTrade("アミュレット交換", 1),
        RuneCrusher("ルーン粉砕", 1),
        ;

        private final String display;
        private final int size;

        Type(String display, int size) {
            this.display = display;
            this.size = size;
        }

        public String getDisplay() {
            return display;
        }

        public int getSize() {
            return size;
        }
    }

    private final Inventory inv;
    private final Type type;
    public QuickGUI(Type type) {
        this.type = type;
        inv = Function.decoInv(type.display, type.size);
        list.put(type, this);
    }

    public void setItem(int slot, ItemStack item) {
        inv.setItem(slot, item);
    }

    public void openIns(PlayerData playerData) {
        if (!playerData.isInvalid()) {
            SomTask.sync(() -> playerData.getPlayer().openInventory(inv));
        }
    }

    public abstract void topClick(PlayerData playerData, InventoryClickEvent event);
    public void bottomClick(PlayerData playerData, InventoryClickEvent event) {}

    public String getTitle() {
        return type.display;
    }
}
