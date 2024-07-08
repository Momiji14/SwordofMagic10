package SwordofMagic10.Component;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

public class SomTextParticle extends SomDisplayParticle {
    private final String text;
    private Color color = Color.fromARGB(0, 0, 0, 0);
    private boolean seeThrough = true;
    public SomTextParticle(String text) {
        this.text = text;
    }

    public void setColor(int a, int r, int g, int b) {
        this.color = Color.fromARGB(a, r, g, b);
    }

    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
    }

    @Override
    public Display summon(Location location) {
        TextDisplay display = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        display.setText(text);
        display.setBackgroundColor(color);
        display.setSeeThrough(seeThrough);
        return display;
    }
}
