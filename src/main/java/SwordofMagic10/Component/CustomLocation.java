package SwordofMagic10.Component;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static SwordofMagic10.Component.Function.VectorFromYawPitch;

public class CustomLocation extends Location {
    public CustomLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public CustomLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public CustomLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public double distance(@NotNull Location location) {
        return getWorld().equals(location.getWorld()) ? super.distance(location) : Double.MAX_VALUE;
    }

    public double distanceXZ(Location location) {
        Location currentLocation = clone();
        Location targetLocation = location.clone();
        currentLocation.setY(64);
        targetLocation.setY(64);
        return currentLocation.distance(targetLocation);
    }

    public CustomLocation addXZ(double x, double z) {
        add(x, 0, z);
        return this;
    }

    public CustomLocation addY(double y) {
        add(0, y, 0);
        return this;
    }

    public CustomLocation removeY(double y) {
        add(0, -y, 0);
        return this;
    }

    public CustomLocation left(double value) {
        right(-value);
        return this;
    }

    public CustomLocation right(double value) {
        add(getLookVector().clone().rotateAroundY(-90).multiply(value));
        return this;
    }

    public CustomLocation front(double value) {
        add(getLookVector().clone().multiply(value));
        return this;
    }

    public CustomLocation frontHorizon(double value) {
        add(getLookVector().clone().setY(0).normalize().multiply(value));
        return this;
    }

    public CustomLocation pitch(float pitch) {
        super.setPitch(pitch);
        return this;
    }

    public CustomLocation yaw(float yaw) {
        super.setYaw(yaw);
        return this;
    }

    public CustomLocation addYaw(float yaw) {
        setYaw(getYaw() + yaw);
        return this;
    }

    public CustomLocation lower() {
        CustomLocation loc = this.clone().addY(1);
        loc.setPitch(90);
        SomRay somRay = SomRay.rayLocationBlock(loc, 256, true);
        setLocation(somRay.getHitPosition().addY(0.1));
        return this;
    }

    public CustomLocation setLocation(Location location) {
        set(location.x(), location.y(), location.z());
        return this;
    }

    public CustomLocation lookLocation(Location to) {
        setDirection(to.toVector().subtract(this.toVector()));
        return this;
    }

    public Vector toLocationVector(Location to) {
        return to.toVector().subtract(this.toVector());
    }

    public Vector getLookVector() {
        return VectorFromYawPitch(getYaw(), getPitch());
    }


    public boolean equals(Location location) {
        return location.x() == x() && location.y() == y() && location.z() == z() && location.getYaw() == getYaw() && location.getPitch() == getPitch();
    }

    @Override
    public CustomLocation clone() {
        return new CustomLocation(new Location(getWorld(), x(), y(), z(), getYaw(), getPitch()));
    }

    public @NotNull CustomLocation toHighestLocation() {
        setLocation(super.toHighestLocation());
        return this;
    }
}