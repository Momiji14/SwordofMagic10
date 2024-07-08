package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dummy implements SomEntity {
    public static final List<Dummy> DummyList = new CopyOnWriteArrayList<>();
    public static void run() {
        new BukkitRunnable() {

            final Location[] location = new Location[]{
                    new Location(SomCore.World, 168, -27, 219, 180, 0),
                    new Location(SomCore.World, 173, -27, 219, 180, 0),
                    new Location(SomCore.World, 178, -27, 219, 180, 0),
            };
            @Override
            public void run() {
                DummyList.removeIf(Dummy::isDeath);
                if (SomEntity.nearPlayer(PlayerData.getPlayerList(), location[1], 32).isEmpty()) {
                    for (Dummy dummy : DummyList) {
                        dummy.death();
                    }
                    DummyList.clear();
                } else if (DummyList.size() < location.length) {
                    for (Dummy dummy : DummyList) {
                        dummy.death();
                    }
                    DummyList.clear();
                    double value = 10;
                    for (Location location : location) {
                        Dummy dummy = new Dummy(location);
                        dummy.setBaseStatus(StatusType.DEF, value);
                        dummy.setBaseStatus(StatusType.MDF, value);
                        dummy.setBaseStatus(StatusType.CriticalResist, value);
                        dummy.setBaseStatus(StatusType.DamageResist, 1);
                        dummy.updateStatus();
                        value *= 10;
                        DummyList.add(dummy);
                    }
                }
            }
        }.runTaskTimer(SomCore.plugin(), 20, 100);
    }

    private final LivingEntity entity;
    private final ConcurrentHashMap<DamageEffect, Double> damageEffect = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SomEffect> effect = new ConcurrentHashMap<>();
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private final HashMap<StatusType, Double> basicStatus = new HashMap<>();
    public Dummy(Location location) {
        entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.CREEPER);
        entity.setAI(false);
        PlayerDisguise disguise = new PlayerDisguise("MomiNeko");
        disguise.setName("§c《Dummy》");
        disguise.setNameVisible(true);
        disguise.setDeadmau5Ears(true);
        disguise.setUpsideDown(true);
        disguise.setEntity(entity);
        disguise.startDisguise();
    }

    @Override
    public String getDisplayName() {
        return "ダミー";
    }

    @Override
    public LivingEntity getLivingEntity() {
        return entity;
    }

    @Override
    public HashMap<StatusType, Double> getBaseStatus() {
        return baseStatus;
    }
    private final HashMap<StatusType, Double> baseStatus = new HashMap<>();

    public void setBaseStatus(StatusType status, double value) {
        baseStatus.put(status, value);
    }

    @Override
    public ConcurrentHashMap<DamageEffect, Double> getDamageEffect() {
        return damageEffect;
    }

    @Override
    public ConcurrentHashMap<String, SomEffect> getEffect() {
        return effect;
    }

    @Override
    public Collection<PlayerData> interactAblePlayers() {
        return PlayerData.getPlayerList();
    }

    @Override
    public void hurt(Collection<PlayerData> viewers) {

    }

    @Override
    public boolean isDeath() {
        return false;
    }

    @Override
    public boolean isInvalid() {
        return !entity.isValid();
    }

    @Override
    public void death() {
        entity.remove();
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public void removeHealth(double value) {

    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    @Override
    public HashMap<StatusType, Double> getBasicStatus() {
        return basicStatus;
    }
}
