package SwordofMagic10.Component;

import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import javax.xml.stream.events.StartDocument;
import java.util.Collection;

import static SwordofMagic10.Component.Function.randomDouble;

public class SomBlockParticle extends SomDisplayParticle {

    private final BlockData blockData;
    public SomBlockParticle(Material block) {
        this.blockData = block.createBlockData();
    }

    public SomBlockParticle(BlockData blockData) {
        this.blockData = blockData;
    }
    @Override
    public Display summon(Location location) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        display.setBlock(blockData);
        return display;
    }

    public void bell(Collection<PlayerData> viewers, Location location, double size) {
        SomDisplayParticle particle = addChild(new SomBlockParticle(blockData).setBillboard(Display.Billboard.FIXED));
        SomDisplayParticle particle2 = addChild(new SomBlockParticle(blockData).setBillboard(Display.Billboard.FIXED));
        particle.setScale(0, 0, 0);
        particle2.setScale(0, 0, 0);
        particle.addAnimation(new CustomTransformation().setScale(size*2, size*2.5, size*2).setOffset(size*-1, size*0.5, size*-1).setTime(10));
        particle.addAnimation(new AnimationDelay(10));
        particle.addAnimation(new CustomTransformation().setScale(0, 0, 0).setTime(5));
        particle2.addAnimation(new CustomTransformation().setScale(size*3, size*0.5, size*3).setOffset(size*-1.5, 0, size*-1.5).setTime(10));
        particle2.addAnimation(new AnimationDelay(10));
        particle2.addAnimation(new CustomTransformation().setScale(0, 0, 0).setTime(5));
        particle.spawn(viewers, location);
        particle2.spawn(viewers, location);
    }

    public void pillar(Collection<PlayerData> viewers, Location location, double size, int step, int duration) {
        double y = 0;
        for (int i = 0; i < step; i++) {
            double current = size/(1 + i * (2.0/step));
            SomDisplayParticle particle = addChild(new SomBlockParticle(blockData)).setBillboard(Display.Billboard.FIXED);
            particle.setScale(0, 0, 0);
            particle.addAnimation(new CustomTransformation().setScale(current, current, current).setOffset(current/-2, y, current/-2).setTime(10));
            particle.addAnimation(new AnimationDelay(duration));
            particle.addAnimation(new CustomTransformation().setScale(0, 0, 0).setTime(5));
            particle.spawn(viewers, location);
            y += current;
        }
    }

    //突き刺す
    public void stick(Collection<PlayerData> viewers, CustomLocation location, int duration){

        //Location制定
        double randomX = randomDouble(-2, 2);
        double randomZ = randomDouble(-2, 2);

        CustomLocation startLocation = location.clone().addXZ(randomX, randomZ).addY(10);
        startLocation.lookLocation(location.addY(-0.25));

        SomDisplayParticle display = addChild(new SomBlockParticle(blockData));
        //初期回転
        display.setBillboard(Display.Billboard.FIXED);
        display.setLeftRotation(startLocation);
        display.setRightRotation(0, 90, 0);

        //アニメーション
        display.setScale(0, 0, 0);
        CustomTransformation flame = display.clone().setScale(1.5, 1.5, 2.0).setTime(2);
        display.addAnimation(flame);
        display.addAnimation(flame.setOffset(startLocation.toLocationVector(location)));

        display.addAnimation(new AnimationDelay(duration));
        display.addAnimation(flame.setScale(0, 0, 0));

        display.spawn(viewers, startLocation);
    }

    public void rotationCircleAtEntity(Collection<PlayerData> viewers, SomEntity entity, double size, double offsetY, double radius, int point, int rotationTick, int duration, int acc) {
        rotationCircleAtEntity(viewers, entity, new Vector(size, size, size), offsetY, radius, point, rotationTick, duration, acc);
    }

    public void rotationCircleAtEntity(Collection<PlayerData> viewers, SomEntity entity, Vector size, double offsetY, double radius, int point, int rotationTick, int duration, int acc) {
        double pi = 2*Math.PI;
        int accPoint = acc * point;
        Vector[] points = new Vector[accPoint];
        for (int i = 0; i < accPoint; i++) {
            double x = Math.cos(pi/accPoint * i) * radius;
            double z = Math.sin(pi/accPoint * i) * radius;
            points[i] = new Vector(x - size.getX()/2, offsetY, z - size.getZ()/2);
        }
        int animFlame = duration / rotationTick;
        for (int i = 0; i < point; i++) {
            SomDisplayParticle particle = addChild(new SomBlockParticle(blockData)).setBillboard(Display.Billboard.FIXED);
            particle.setScale(0, 0, 0);
            CustomTransformation transformation = particle.clone().setOffset(points[i*acc]).setScale(size).setTime(rotationTick/acc);
            for (int i2 = 1; i2 < animFlame * acc; i2++) {
                particle.addAnimation(transformation.clone().setOffset(points[(i*acc+i2) % accPoint]));
            }
            particle.addAnimation(transformation.clone().setScale(0, 0, 0));
            SomTask.sync(() -> {
                Display display = particle.create(viewers, entity.getEyeLocation());
                entity.getLivingEntity().addPassenger(display);
            });
        }
    }
}
