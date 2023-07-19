package SwordofMagic10.Component;

import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.SomEntity;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class SomRay {
    private final List<SomEntity> HitEntity = new ArrayList<>();
    private final HashMap<SomEntity, Boolean> HeadShot = new HashMap<>();
    private Location HitPosition = null;
    private Block HitBlock = null;

    public boolean isHitEntity() {
        return HitEntity.size() != 0;
    }

    public boolean isHitBlock() {
        return HitBlock != null;
    }

    public boolean isHeadShot() {
        return HeadShot.get(HitEntity.get(0));
    }

    public boolean isHeadShot(SomEntity entity) {
        return HeadShot.get(entity);
    }

    public Block getHitBlock() {
        return HitBlock;
    }

    public SomEntity getHitEntity() {
        return HitEntity.size() > 0 ? HitEntity.get(0) : null;
    }

    public List<SomEntity> getHitEntities() {
        return HitEntity;
    }

    public CustomLocation getHitPosition() {
        if (isHitEntity()) return new CustomLocation(HitEntity.get(HitEntity.size()-1).getEyeLocation());
        return getOriginPosition();
    }

    public CustomLocation getOriginPosition() {
        return new CustomLocation(HitPosition);
    }

    public static SomRay rayLocationBlock(CustomLocation loc, double distance, boolean ignore) {
        loc = loc.clone();
        World world = loc.getWorld();
        RayTraceResult rayData = world.rayTraceBlocks(loc, loc.getLookVector(), distance, FluidCollisionMode.NEVER, ignore);
        SomRay ray = new SomRay();
        if (rayData == null) {
            ray.HitPosition = loc.add(loc.getLookVector().multiply(distance));
        } else {
            ray.HitPosition = rayData.getHitPosition().toLocation(world);
            if (rayData.getHitBlock() != null) {
                ray.HitBlock = rayData.getHitBlock();
            }
        }
        return ray;
    }

    public static SomRay rayLocationBlock(SomEntity entity, double distance, boolean ignore) {
        if (entity instanceof EnemyData enemyData) {
            if (enemyData.isTargeting()) {
                CustomLocation location = enemyData.getTarget().getLocation();
                CustomLocation eyeLocation = enemyData.getEyeLocation().clone();
                eyeLocation.setDirection(location.toVector().subtract(eyeLocation.toVector()));
                return rayLocationBlock(eyeLocation, distance, ignore);
            }
        }
        return rayLocationBlock(entity.getEyeLocation(), distance, ignore);
    }

    public static SomRay rayLocationEntity(Collection<SomEntity> targets, CustomLocation loc, double distance, double size, boolean penetrate) {
        World world = loc.getWorld();
        SomRay ray = new SomRay();
        Location lastLocation = rayLocationBlock(loc, distance, true).getOriginPosition();
        distance = loc.distance(lastLocation);
        List<SomEntity> enemyList = new ArrayList<>(targets);
        enemyList.sort(Comparator.comparingDouble(enemy -> enemy.getLocation().distance(loc)));
        for (SomEntity entity : enemyList) {
            BoundingBox box = entity.getLivingEntity().getBoundingBox().expand(size);
            if (entity instanceof EnemyData enemyData) {
                box.expand(enemyData.getMobData().getCollisionSize());
                box.expand(0, enemyData.getMobData().getCollisionSizeY(), 0);
            }
            RayTraceResult rayData = box.rayTrace(loc.toVector(), loc.getLookVector(), distance);
            if (rayData != null) {
                ray.HitPosition = rayData.getHitPosition().toLocation(world);
                ray.HitEntity.add(entity);
                ray.HeadShot.put(entity, entity.getEyeLocation().getY() <= ray.HitPosition.getY());
                if (!penetrate) {
                    return ray;
                }
            }
        }
        ray.HitPosition = lastLocation;
        return ray;
    }

    public static SomRay rayLocationEntity(SomEntity entity, double distance, double size, Collection<SomEntity> targets, boolean penetrate) {
        if (entity instanceof EnemyData enemyData) {
            if (enemyData.isTargeting()) {
                CustomLocation location = enemyData.getTarget().getLocation();
                CustomLocation eyeLocation = enemyData.getEyeLocation().clone();
                eyeLocation.setDirection(location.toVector().subtract(eyeLocation.toVector()));
                return rayLocationEntity(targets, eyeLocation, distance, size, penetrate);
            }
        }
        return rayLocationEntity(targets, entity.getEyeLocation(), distance, size, penetrate);
    }
}
