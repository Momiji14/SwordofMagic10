package SwordofMagic10.Component;

import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static SwordofMagic10.Component.Config.SomParticleMetaAddress;
import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomDoubleSign;

public abstract class SomDisplayParticle extends CustomTransformation implements Cloneable {

    private final List<AnimationFlame> animation = new ArrayList<>();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<Display> displayEntity = new ArrayList<>();
    private Display.Billboard billboard = Display.Billboard.CENTER;

    public void addAnimation(AnimationFlame animationFlame) {
        animation.add(animationFlame.clone());
    }

    public SomDisplayParticle setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public SomDisplayParticle setTime(int time) {
        super.setTime(time);
        return this;
    }

    public Display create(Collection<PlayerData> viewers, Location location) {
        Display display = summon(location);
        displayEntity.add(display);
        display.setTransformation(this);
        display.setBillboard(billboard);
        display.setViewRange(256);
        display.setMetadata(SomParticleMetaAddress, new FixedMetadataValue(SomCore.plugin(), true));
        if (animation.size() == 0) {
            taskList.add(SomTask.syncDelay(display::remove, time()));
        } else {
            SomTask.delay(() -> {
                int delay = 0;
                for (AnimationFlame animationFlame : animation) {
                    if (animationFlame instanceof CustomTransformation flame) {
                        taskList.add(SomTask.delay(() -> {
                            display.setInterpolationDuration(flame.time());
                            display.setInterpolationDelay(-1);
                            display.setTransformation(flame);
                        }, delay));
                    }
                    delay += animationFlame.time();
                }
                taskList.add(SomTask.syncDelay(display::remove, delay));
            }, 2);
        }
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            if (!viewers.contains(playerData)) {
                Player player = playerData.getPlayer();
                player.hideEntity(SomCore.plugin(), display);
            }
        }
        return display;
    }

    public void stop() {
        for (BukkitTask bukkitTask : taskList) {
            bukkitTask.cancel();
        }
        SomTask.sync(() -> {
            for (Display display : displayEntity) {
                display.remove();
            }
        });
    }

    public void spawn(Collection<PlayerData> viewers, Location location) {
        SomTask.sync(() -> create(viewers, location));
    }

    public void line(Collection<PlayerData> viewers, CustomLocation location, double length, double width) {
        setBillboard(Display.Billboard.FIXED);
        setScale(width, width, length);
        setOffset(location.getDirection().multiply(length));
        setLeftRotation(location.getYaw(), location.getPitch(), 0);
        spawn(viewers, location);
    }

    public void pop(Collection<PlayerData> viewers, CustomLocation location) {
        setScale(0, 0, 0);
        CustomTransformation transformation = new CustomTransformation().setOffset(randomDoubleSign(-0.5, 1.5), randomDouble(0.5, 1), randomDoubleSign(-0.5, 1.5)).setScale(1.5,1.5,1.5).setTime(5);
        addAnimation(transformation);
        addAnimation(new AnimationDelay(10));
        addAnimation(transformation.setScale(0,0,0).setTime(20));
        spawn(viewers,location);
    }

    public boolean circle(Collection<PlayerData> viewers, Location location, double radius, double density) {
        density = 1/density;
        double pi = 2*Math.PI;
        double wait = time() / (pi/density);
        double offset = randomDouble(0, pi);
        for (double i = 0; i < pi; i += density) {
            double x = Math.cos(i+offset) * radius;
            double z = Math.sin(i+offset) * radius;
            spawn(viewers, location.clone().add(x, 0 ,z));
            if (time() > 0) SomTask.wait(wait);
        }
        return true;
    }

    public boolean circleFill(Collection<PlayerData> viewers, Location location, double radius, double density) {
        density = 1/density;
        double pi = 2*Math.PI;
        double wait = time() / (pi/density);
        for (double i = 0; i < pi; i += density) {
            double lastRadius = randomDouble(0, radius);
            double x = Math.cos(randomDouble(0, pi)) * lastRadius;
            double z = Math.sin(randomDouble(0, pi)) * lastRadius;
            spawn(viewers, location.clone().add(x, 0 ,z));
            if (time() > 0) SomTask.wait(wait);
        }
        return true;
    }


    public abstract Display summon(Location location);
}
