package SwordofMagic10.Component;

import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomDoubleSign;

public class SomParticle {

    public static final Vector VectorUp = new Vector(0, 1, 0);
    public static final Vector VectorDown = new Vector(0, -1, 0);
    private Particle particle;
    private Particle.DustOptions options;
    private final SomEntity owner;
    private Vector vector = new Vector();
    private float speed = 0f;
    private double time = 0d;
    private int amount = 1;
    private boolean stop = false;
    private final Random random = new Random();

    public SomParticle(Particle particle, SomEntity owner) {
        this.particle = particle;
        this.owner = owner;
    }

    public SomParticle(Color color, SomEntity owner) {
        this.particle = Particle.REDSTONE;
        options = new Particle.DustOptions(color, 1);
        this.owner = owner;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Particle getParticle() {
        return particle;
    }

    public Particle.DustOptions getOptions() {
        return options;
    }

    public void setOptions(Particle.DustOptions options) {
        this.options = options;
    }

    public void setColor(Color color) {
        options = new Particle.DustOptions(color, 0);
    }

    public float getSpeed() {
        return speed;
    }

    public SomParticle setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public SomParticle setRandomSpeed(float maxSpeed) {
        this.speed = maxSpeed + 100;
        return this;
    }

    public Vector getVector() {
        return vector;
    }

    public SomParticle setVector(Vector vector) {
        this.vector = vector;
        return this;
    }

    public SomParticle setVector(Vector vector, double multiply) {
        this.vector = vector.multiply(multiply);
        return this;
    }

    public SomParticle setRandomVector() {
        this.vector = new Vector(0, 999, 0);
        return this;
    }

    public SomParticle setVectorUp() {
        this.vector = VectorUp;
        return this;
    }

    public SomParticle setVectorDown() {
        this.vector = VectorDown;
        return this;
    }

    public double getTime() {
        return time;
    }

    public SomParticle setTime(double time) {
        this.time = time;
        return this;
    }

    public SomParticle setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public void stop() {
        stop = true;
    }

    public void spawn(Collection<PlayerData> viewers, List<Location> locations, int wait) {
        if (wait == 0) {
            spawn(viewers, locations);
            return;
        }
        SomTask.run(() -> {
            for (Location location : locations) {
                spawn(viewers, location);
                SomTask.wait(wait);
            }
        });
    }

    public void spawn(Collection<PlayerData> viewers, List<Location> locations) {
        SomTask.run(() -> {
            for (Location location : locations) {
                spawn(viewers, location);
            }
        });
    }

    private static final HashMap<PlayerData, Integer> particleDensityCache = new HashMap<>();
    public void spawn(Collection<PlayerData> viewers, Location location) {
        try {
            for (PlayerData playerData : viewers) {
                if (playerData.getLocation().distance(location) < Math.min(96, playerData.getPlayer().getViewDistance()*16)) {
                    int particleDensity;
                    if (owner == playerData) {
                        particleDensity = playerData.getSetting().getParticleDensity();
                    } else if (owner instanceof PlayerData) {
                        particleDensity = playerData.getSetting().getParticleDensityOther();
                    } else if (owner instanceof EnemyData) {
                        particleDensity = playerData.getSetting().getParticleDensityEnemy();
                    } else {
                        particleDensity = 100;
                    }
                    if (!playerData.isAFK() && particleDensity != 0) {
                        Particle particle = this.particle;
                        Particle.DustOptions options = this.options;
                        if (playerData.isBE()) {
                            switch (particle) {
                                case FIREWORKS_SPARK -> {
                                    particle = Particle.REDSTONE;
                                    options = new Particle.DustOptions(Color.WHITE, 1);
                                }
                            }
                        }
                        boolean viewParticle = true;
                        if (particleDensity != 100) {
                            int density = particleDensityCache.getOrDefault(playerData, 0) + particleDensity;
                            if (density < 100) {
                                viewParticle = false;
                            } else {
                                density -= 100;
                            }
                            particleDensityCache.put(playerData, density);
                        }
                        if (viewParticle) {
                            Player player = playerData.getPlayer();
                            if (player.getWorld() == location.getWorld() && player.getLocation().distance(location) < 128) {
                                for (int i = 0; i < amount; i++) {
                                    double speed = this.speed < 100 ? this.speed : random.nextDouble(100, this.speed) - 100;
                                    Vector vector = this.vector.getY() < 100 ? this.vector : new Vector(randomDouble(-1, 1), randomDouble(-1, 1), randomDouble(-1, 1));
                                    if (options == null) {
                                        player.spawnParticle(particle, location, 0, vector.getX(), vector.getY(), vector.getZ(), speed);
                                    } else {
                                        player.spawnParticle(particle, location, 0, vector.getX(), vector.getY(), vector.getZ(), speed, options);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public boolean sphere(Collection<PlayerData> viewers, Location location, double radius) {
        return sphere(viewers, location, radius, radius*24);
    }

    public boolean sphere(Collection<PlayerData> viewers, Location location, double radius, double density) {
        List<Vector> locations = sphereData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, location.clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean sphere(Collection<PlayerData> viewers, LivingEntity entity, double radius) {
        return sphere(viewers, entity, radius, 12 + radius*6);
    }

    public boolean sphere(Collection<PlayerData> viewers, LivingEntity entity, double radius, double density) {
        List<Vector> locations = sphereData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, entity.getLocation().clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public List<Vector> sphereData(double radius, double density) {
        List<Vector> locations = new ArrayList<>();
        density = 1/density;
        double pi = 2*Math.PI;
        for (double i = 0; i < pi; i += density) {
            locations.add(Function.VectorFromYawPitch(random.nextFloat()*360-180, random.nextFloat()*180-90).multiply(radius));
        }
        return locations;
    }

    public boolean sphereHalf(Collection<PlayerData> viewers, Location location, double radius) {
        return sphereHalf(viewers, location, radius, 12 + radius*6);
    }

    public boolean sphereHalf(Collection<PlayerData> viewers, Location location, double radius, double density) {
        List<Vector> locations = sphereData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, location.clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean sphereHalf(Collection<PlayerData> viewers, LivingEntity entity, double radius) {
        return sphereHalf(viewers, entity, radius, 12 + radius*6);
    }

    public boolean sphereHalf(Collection<PlayerData> viewers, LivingEntity entity, double radius, double density) {
        List<Vector> locations = sphereHalfData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, entity.getLocation().clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public List<Vector> sphereHalfData(double radius, double density) {
        List<Vector> locations = new ArrayList<>();
        density = 1/density;
        double pi = Math.PI;
        for (double i = 0; i < pi; i += density) {
            locations.add(Function.VectorFromYawPitch(random.nextFloat()*360-180, random.nextFloat()*-90).multiply(radius));
        }
        return locations;
    }

    public boolean circle(Collection<PlayerData> viewers, Location location, double radius) {
        return circle(viewers, location, radius, 12 + radius*6);
    }

    public boolean circle(Collection<PlayerData> viewers, Location location, double radius, double density) {
        List<Vector> locations = circleData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, location.clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean circle(Collection<PlayerData> viewers, LivingEntity entity, double radius) {
        return circle(viewers, entity, radius, 12 + radius*6);
    }

    public boolean circle(Collection<PlayerData> viewers, LivingEntity entity, double radius, double density) {
        List<Vector> locations = circleData(radius, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, entity.getLocation().clone().add(vector).add(0, 0.1, 0));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public List<Vector> circleData(double radius, double density) {
        List<Vector> locations = new ArrayList<>();
        density = 1/density;
        double pi = 2*Math.PI;
        double wait = time / (pi/density);
        double offset = random.nextDouble(pi);
        for (double i = 0; i < pi; i += density) {
            double x = Math.cos(i+offset) * radius;
            double z = Math.sin(i+offset) * radius;
            locations.add(new Vector(x, 0, z));
        }
        return locations;
    }


    public boolean circleFill(Collection<PlayerData> viewers, Location location, double radius) {
        return circleFill(viewers, location, radius, 12 + radius*6);
    }

    public boolean circleFill(Collection<PlayerData> viewers, Location location, double radius, double density) {
        density = 1/density;
        double pi = 2*Math.PI;
        double wait = time / (pi/density);
        double offset = random.nextDouble(pi);
        density = density/(1+radius/3);
        for (double i = 0; i < pi; i += density) {
            double lastRadius = random.nextDouble(radius);
            double x = Math.cos(i+offset) * lastRadius;
            double z = Math.sin(i+offset) * lastRadius;
            spawn(viewers, location.clone().add(x, 0 ,z));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean circleSmoothRadius(Collection<PlayerData> viewers, Location location, double radiusStart, double radiusEnd, int lap) {
        return circleSmoothRadius(viewers, location, radiusStart, radiusEnd, lap, 12 + radiusEnd*6);
    }

    public boolean circleSmoothRadius(Collection<PlayerData> viewers, Location location, double radiusStart, double radiusEnd, int lap, double density) {
        density = (1/density)/lap;
        double pi = 2*Math.PI;
        double wait = time / (pi/density);
        double offset = random.nextDouble(pi);
        double radius = radiusStart;
        double radiusIncrease = (radiusEnd - radiusStart) / density;
        for (double i = 0; i < pi; i += density) {
            double x = Math.cos(i+offset) * radius;
            double z = Math.sin(i+offset) * radius;
            radius += radiusIncrease;
            spawn(viewers, location.clone().add(x, 0.1 ,z));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean circleHeightTwin(Collection<PlayerData> viewers, Location location, double radius, double height) {
        return circleHeightTwin(viewers, location, radius, height, 3, 10);
    }

    public boolean circleHeightTwin(Collection<PlayerData> viewers, Location location, double radius, double height, double lap) {
        return circleHeightTwin(viewers, location, radius, height, lap, 10*lap);
    }

    public boolean circleHeightTwin(Collection<PlayerData> viewers, Location location, double radius, double height, double lap, double density) {
        List<Vector> locations = circleHeightTwinData(radius, height, lap, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, location.clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean circleHeightTwin(Collection<PlayerData> viewers, LivingEntity entity, double radius, double height, double lap) {
        return circleHeightTwin(viewers, entity, radius, height, lap, 10*lap);
    }

    public boolean circleHeightTwin(Collection<PlayerData> viewers, LivingEntity entity, double radius, double height, double lap, double density) {
        List<Vector> locations = circleHeightTwinData(radius, height, lap, density);
        double wait = time / locations.size();
        for (Vector vector : locations) {
            spawn(viewers, entity.getLocation().clone().add(vector));
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public List<Vector> circleHeightTwinData(double radius, double height, double lap, double density) {
        List<Vector> locations = new ArrayList<>();
        density = 1/density;
        double pi = 2 * Math.PI;
        double densityY = height / (pi/density);
        double y = 0;
        double offset = random.nextDouble() * pi;
        for (double i = 0; i < pi; i += density) {
            double x = Math.cos(i*lap+offset) * radius;
            double z = Math.sin(i*lap+offset) * radius;
            locations.add(new Vector(x, y, z));
            locations.add(new Vector(-x, y, -z));
            y += densityY;
        }
        return locations;
    }

    public boolean line(Collection<PlayerData> viewers, Location location, double length) {
        return line(viewers, location, new CustomLocation(location).front(length), 0);
    }

    public boolean line(Collection<PlayerData> viewers, Location location, double length, double width, double density) {
        return line(viewers, location, new CustomLocation(location).front(length), width, density);
    }

    public boolean line(Collection<PlayerData> viewers, Location location, Location point) {
        return line(viewers, location, point, 0);
    }

    public boolean line(Collection<PlayerData> viewers, Location location, Location point, double width) {
        return line(viewers, location, point, width, 10 + width*10);
    }

    public boolean line(Collection<PlayerData> viewers, Location location, Location point, double width, double density) {
        density = 1/density;
        Vector vector = point.toVector().subtract(location.toVector()).normalize().multiply(density);
        double distance = location.distance(point);
        double wait = time / (distance/density);
        Location loc = location.clone();
        for (double i = 0; i < distance; i += density) {
            Location locWidth = loc.clone();
            if (width > 0){
                Vector randomVector =  new Vector(width*(random.nextDouble()-0.5), width*(random.nextDouble()-0.5), width*(random.nextDouble()-0.5));
                locWidth.add(Vector.getRandom().multiply(randomVector));
            }
            spawn(viewers, locWidth);
            loc.add(vector);
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean widthLine(Collection<PlayerData> viewers, Location location, double length, double rightWidth) {
        return widthLine(viewers, location, length, rightWidth, 0);
    }

    public boolean widthLine(Collection<PlayerData> viewers, Location location, double length, double rightWidth, double width) {
        return widthLine(viewers, location, length, rightWidth, width, 10 + width*8);
    }

    public boolean widthLine(Collection<PlayerData> viewers, Location location, double length, double rightWidth, double width, double density) {
        CustomLocation center = new CustomLocation(location).front(length);
        double offsetY = randomDoubleSign(0.35, 0.7);
        Location right = center.clone().right(rightWidth).addY(offsetY);
        Location left = center.clone().right(-rightWidth).addY(-offsetY);
        line(viewers, right, left, width, density);
        return true;
    }

    public boolean arc(Collection<PlayerData> viewers, Location location, Location point, double angle) {
        return arc(viewers, location, point, angle, 0);
    }

    public boolean arc(Collection<PlayerData> viewers, Location location, Location point, double angle, double width) {
        return arc(viewers, location, point, angle, width, 10);
    }

    public boolean arc(Collection<PlayerData> viewers, Location location, Location point, double angle, double width, double density) {
        density = 1/density;
        Vector vector = point.toVector().subtract(location.toVector()).normalize().multiply(density);
        double distance = location.distance(point);
        double wait = time / (distance/density);
        double angleSubtract = (angle * density)/distance * 2;
        CustomLocation loc = (CustomLocation) location.clone();
        for (double i = 0; i < distance; i += density) {
            Location locWidth = loc.clone();
            if (width > 0) locWidth.add(Vector.getRandom().multiply(width*(random.nextDouble()-0.5)));
            spawn(viewers, locWidth);
            loc.add(vector);
            loc.addY(angle);
            angle -= angleSubtract;
            if (time > 0) SomTask.wait(wait);
            if (stop) return false;
        }
        return true;
    }

    public boolean circlePointLine(Collection<PlayerData> viewers, Location location, double radius, int point, double width, double offset) {
        return circlePointLine(viewers, location, radius, point, width, offset, 6);
    }

    public boolean circlePointLine(Collection<PlayerData> viewers, Location location, double radius, int point, double width, double offset, double lineDensity) {
        Location lastLocation = null;
        double density = 2 * Math.PI/point;
        offset *= Math.PI;
        for (double i = offset; i <= 2 * Math.PI + offset ; i += density) {
            double x = Math.cos(i) * radius;
            double z = Math.sin(i) * radius;
            Location loc = location.clone().add(x, 0 ,z);
            if (lastLocation != null) {
                Location finalLastLocation = lastLocation;
                SomTask.run(() -> line(viewers, finalLastLocation, loc, width, lineDensity));
            }
            lastLocation = loc.clone();
        }
        for (double i = 0; i < time; i += 50) {
            SomTask.wait(50);
            if (stop) return false;
        }
        return true;
    }

    public void randomVectorHalf(Collection<PlayerData> viewers, Location location, double radius) {
        randomVectorHalf(viewers, location, radius, 50 + radius * 10);
    }

    public void randomVectorHalf(Collection<PlayerData> viewers, Location location, double radius, double density) {
        if (getSpeed() == 0) setSpeed(0.1f);
        double wait = time / density;
        for (double i = 0; i < density; i++) {
            setVector(new Vector(randomDoubleSign(0, radius), randomDouble(0, radius), randomDoubleSign(0, radius)));
            spawn(viewers, location);
            if (time > 0) SomTask.wait(wait);
        }
    }

    public void randomLocation(Collection<PlayerData> viewers, Location location, double radius, double density) {
        for (int i = 0; i < density; i++) {
            double x = (random.nextDouble(2)-1) * radius;
            double y = random.nextDouble(2) * radius;
            double z = (random.nextDouble(2)-1) * radius;
            spawn(viewers, location.clone().add(x, y, z));
        }
    }

    public void fanShaped(Collection<PlayerData> viewers, Location location, double length, double angle) {
        fanShaped(viewers, location, length, angle, 5);
    }

    public void fanShaped(Collection<PlayerData> viewers, Location location, double length, double angle, double density) {
        double multiply = 36 / density;
        CustomLocation cloneLocation = new CustomLocation(location.clone());
        cloneLocation.setPitch(0);
        cloneLocation.setYaw((float) (cloneLocation.getYaw() - angle/2));
        for (double i = 0; i < angle; i += multiply) {
            cloneLocation.setYaw((float) (cloneLocation.getYaw() + multiply));
            line(viewers, cloneLocation, length, 0, density);
        }
    }

    public void rectangle(Collection<PlayerData> viewers, CustomLocation location, double length, double width) {
        rectangle(viewers, location, length, width, 5);
    }

    public void rectangle(Collection<PlayerData> viewers, CustomLocation location, double length, double width, double density) {
        location.setPitch(0);
        CustomLocation locR = location.clone().right(width/2);
        CustomLocation locL = location.clone().left(width/2);
        CustomLocation locR2 = locR.clone().front(length);
        CustomLocation locL2 = locL.clone().front(length);
        line(viewers, locL, locR, density);
        line(viewers, locL, locL2, density);
        line(viewers, locR, locR2, density);
        line(viewers, locL2, locR2, density);
    }

    public boolean circleBlockWare(Location location, double radius) {
        List<Block> blocks = new ArrayList<>();
        for (double x = -radius; x < radius; x++) {
            for (double z = -radius; z < radius; z++) {
                Location loc = location.clone().add(x, -0.5, z);
                if (loc.distance(location) < radius) {
                    Block block = loc.getBlock();
                    blocks.add(block);
                }
            }
        }
        blocks.sort(Comparator.comparingDouble(block -> block.getLocation().distance(location)));
        Vector vector = new Vector(0, 0.3, 0);
        double time = this.time/blocks.size();
        for (Block block : blocks) {
            SomTask.sync(() -> {
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0.25, 0.5), block.getBlockData());
                fallingBlock.setVelocity(vector);
                fallingBlock.setDropItem(false);
                fallingBlock.shouldAutoExpire(true);
                fallingBlock.setHurtEntities(false);
            });
            if (time > 0) SomTask.wait(time);
            if (stop) return false;
        }
        return true;
    }
}