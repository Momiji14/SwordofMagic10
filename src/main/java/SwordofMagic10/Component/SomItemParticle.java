package SwordofMagic10.Component;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

public class SomItemParticle extends SomDisplayParticle {

    private final ItemStack item;
    public SomItemParticle(ItemStack item) {
        this.item = item;
    }

    @Override
    public Display summon(Location location) {
        ItemDisplay display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        display.setItemStack(item);
        return display;
    }
}
