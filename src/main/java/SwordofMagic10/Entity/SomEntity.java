package SwordofMagic10.Entity;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Item.SomEquipment;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static SwordofMagic10.Component.Function.angle;
import static SwordofMagic10.SomCore.Log;

public interface SomEntity extends SomStatus {

    static List<PlayerData> nearPlayer(Collection<PlayerData> targets, Location location, double distance) {
        List<PlayerData> list = new ArrayList<>();
        for (PlayerData playerData : targets) {
            if (playerData.getLocation().distance(location) <= distance) {
                list.add(playerData);
            }
        }
        return list;
    }

    static List<SomEntity> nearSomEntity(Collection<SomEntity> targets, CustomLocation location, double radius) {
        List<SomEntity> list = new ArrayList<>();
        for (SomEntity entity : targets) {
            LivingEntity livingEntity = entity.getLivingEntity();
            if (livingEntity != null && location.getWorld() == livingEntity.getWorld()) {
                Location target = livingEntity.getLocation();
                if (location.distanceXZ(target) <= radius && Math.abs(location.getY()-target.getY()) < radius+2) {
                    list.add(entity);
                }
            }
        }
        return list;
    }

    static List<LivingEntity> nearLivingEntity(Location location, double radius, Predicate<SomEntity> predicate) {
        List<LivingEntity> list = new ArrayList<>();
        for (SomEntity entity : getSomEntity(predicate)) {
            LivingEntity livingEntity = entity.getLivingEntity();
            if (livingEntity != null && location.getWorld() == livingEntity.getWorld() && livingEntity.getLocation().distance(location) <= radius) {
                list.add(livingEntity);
            }
        }
        return list;
    }

    static List<SomEntity> fanShapedSomEntity(Collection<SomEntity> targets, CustomLocation location, double length, double angle) {
        List<SomEntity> list = new ArrayList<>();
        for (SomEntity entity : targets) {
            if (location.distanceXZ(entity.getLocation()) < length) {
                Location location2 = entity.getLocation();
                int Angle = angle(location.getDirection());
                int Angle2 = angle(location.toVector(), location2.toVector());
                if (Math.abs(Angle % 360 - Angle2 % 360) < angle / 2) {
                    list.add(entity);
                }
            }
        }
        return list;
    }

    static List<SomEntity> rectangleSomEntity(Collection<SomEntity> targets, CustomLocation location, double length, double width) {
        final double posX0 = -width/2;
        final double posX1 = width/2;
        final double posZ1 = 0;
        double angle = angle(location.getDirection());
        List<SomEntity> list = new ArrayList<>();
        for (SomEntity entity : targets) {
            double distance = location.distance(entity.getLocation());
            double posAngle = angle(location.toVector(), entity.getLocation().toVector()) - angle;
            double posX = distance * Math.sin(posAngle * (Math.PI / 180));
            double posZ = distance * Math.cos(posAngle * (Math.PI / 180));
            if (posX0 <= posX && posX <= posX1 && posZ1 <= posZ && posZ <= length) {
                list.add(entity);
            }
        }
        return list;
    }

    static List<SomEntity> getSomEntity(Predicate<SomEntity> predicate) {
        List<SomEntity> list = new ArrayList<>();
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            if (predicate.test(playerData)) {
                list.add(playerData);
            }
        }
        List<EnemyData> enemyList = EnemyData.getEnemyList();
        for (EnemyData enemyData : enemyList) {
            if (predicate.test(enemyData)) {
                list.add(enemyData);
            }
        }
        return list;
    }

    static SomEntity getSomEntity(Entity entity) {
        if (entity instanceof Player player && player.isOnline()) {
            return PlayerData.get(player);
        } else  if (entity != null && entity.hasMetadata(Config.EnemyMetaAddress)) {
            for (MetadataValue metadata : entity.getMetadata(Config.EnemyMetaAddress)) {
                if (metadata.value() instanceof EnemyData enemyData) {
                    return enemyData;
                }
            }
        }
        entity.remove();
        throw new RuntimeException("Not Found somEntity");
    }

    static boolean isSomEntity(Entity entity) {
        if (entity instanceof Player player && player.isOnline()) {
            return true;
        } else {
            return entity.hasMetadata(Config.EnemyMetaAddress);
        }
    }

    String getDisplayName();
    LivingEntity getLivingEntity();
    HashMap<StatusType, Double> getBaseStatus();
    HashMap<DamageEffect, Double> getDamageEffect();
    HashMap<String, SomEffect> getEffect();

    Collection<PlayerData> interactAblePlayers();
    void hurt(Collection<PlayerData> viewers);
    boolean isDeath();

    boolean isInvalid();

    void death();

    default void addEffect(SomEffect effect) {
        if (!effect.isBuff() && hasEffect("Forgiveness")) {
            SomEffect forgiveness = getEffect("Forgiveness");
            forgiveness.addStack(-1);
            sendMessage(forgiveness.getDisplay() + "§aにより§c" + effect.getDisplay() + "§aが§c無力化§aされました");
            return;
        }
        if (effect.isStun() && hasEffect("PainBarrier")) {
            SomEffect forgiveness = getEffect("PainBarrier");
            sendMessage(forgiveness.getDisplay() + "§aにより§c" + effect.getDisplay() + "§aが§c無力化§aされました");
            return;
        }
        if (hasEffect(effect)) {
            SomEffect baseEffect = getEffect(effect.getId());
            baseEffect.setTime(Math.max(baseEffect.getTime(), effect.getTime()));
            baseEffect.setStack(Math.max(baseEffect.getStack(), effect.getStack()));
        } else {
            getEffect().put(effect.getId(), effect.clone());
        }
        if (effect.isStatusUpdate() || effect.getStatus().size() > 0) {
            updateStatus();
        }
    }

    default void removeEffect(String id) {
        if (id.equals("ArcaneEnergy")) {
            setMana(Math.max(getMana(), getEffect(id).getDoubleData()[0]));
        }
        getEffect().remove(id);
    }

    default void removeEffect(SomEffect effect) {
        removeEffect(effect.getId());
        if (effect.getStatus().size() > 0 || effect.isStatusUpdate()) {
            updateStatus();
        }
    }

    default void addEffectStack(SomEffect effect, int stack) {
        addEffectStack(effect.getId(), stack);
    }

    default void addEffectStack(String id, int stack) {
        if (hasEffect(id)) {
            getEffect(id).addStack(stack);
        }
    }

    default void removeEffectStack(SomEffect effect, int stack) {
        getEffect(effect.getId()).addStack(-stack);
    }

    default boolean hasEffect(String id) {
        return getEffect().containsKey(id);
    }

    default boolean hasEffect(SomEffect effect) {
        return getEffect().containsKey(effect.getId());
    }

    default SomEffect getEffect(String id) {
        return getEffect().get(id);
    }

    default boolean isInvincible() {
        for (SomEffect effect : getEffect().values()) {
            if (effect.isInvincible()) return true;
        }
        return false;
    }

    default boolean isStun() {
        for (SomEffect effect : getEffect().values()) {
            if (effect.isStun()) return true;
        }
        return false;
    }

    default boolean isSilence(String other) {
        for (SomEffect effect : getEffect().values()) {
            if (effect.isSilence() != null) {
                if (!effect.isSilence().equals(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    default void tickEffect() {
        List<SomEffect> removeEffect = new ArrayList<>();
        getEffect().forEach((id, effect) -> {
            if (!effect.isToggle()) {
                effect.setTime(effect.getTime()-1);
                if (effect.getStack() <= 0 || effect.getTime() <= 0) {
                    removeEffect.add(effect);
                }
            }
        });
        for (SomEffect effect : removeEffect) {
            removeEffect(effect);
        }
    }

    int getLevel();

    HashMap<StatusType, Double> levelPerPlayer = new HashMap<>() {{
       put(StatusType.MaxHealth, 5.0);
       put(StatusType.MaxMana, 5.0);
       put(StatusType.ATK, 1.0);
       put(StatusType.MAT, 1.0);
       put(StatusType.DEF, 1.0);
       put(StatusType.MDF, 1.0);
       put(StatusType.SPT, 1.0);
       put(StatusType.CriticalResist, 1.0);
    }};

    HashMap<MobData.Tier, HashMap<StatusType, Double>> levelPerEnemy = new HashMap<>() {{
        for (MobData.Tier tier : MobData.Tier.values()) {
            switch (tier) {
                case Normal -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 25.0);
                    put(StatusType.ATK, 5.0);
                    put(StatusType.MAT, 5.0);
                    put(StatusType.DEF, 5.0);
                    put(StatusType.MDF, 5.0);
                    put(StatusType.SPT, 5.0);
                    put(StatusType.Critical, 2.5);
                    put(StatusType.CriticalDamage, 1.5);
                    put(StatusType.CriticalResist, 2.5);
                }});
                case MiddleBoss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 200.0);
                    put(StatusType.ATK, 5.5);
                    put(StatusType.MAT, 5.5);
                    put(StatusType.DEF, 5.5);
                    put(StatusType.MDF, 5.5);
                    put(StatusType.SPT, 5.5);
                    put(StatusType.Critical, 3.0);
                    put(StatusType.CriticalDamage, 2.0);
                    put(StatusType.CriticalResist, 3.0);
                }});
                case Boss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 400.0);
                    put(StatusType.ATK, 6.5);
                    put(StatusType.MAT, 6.5);
                    put(StatusType.DEF, 6.5);
                    put(StatusType.MDF, 6.5);
                    put(StatusType.SPT, 6.5);
                    put(StatusType.Critical, 4.5);
                    put(StatusType.CriticalDamage, 3.0);
                    put(StatusType.CriticalResist, 4.5);
                }});
                case RaidBoss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 10000.0);
                    put(StatusType.ATK, 7.5);
                    put(StatusType.MAT, 7.5);
                    put(StatusType.DEF, 7.5);
                    put(StatusType.MDF, 7.5);
                    put(StatusType.SPT, 7.5);
                    put(StatusType.Critical, 5.5);
                    put(StatusType.CriticalDamage, 4.0);
                    put(StatusType.CriticalResist, 5.5);
                }});
            }
        }
    }};
    HashMap<DungeonDifficulty, HashMap<StatusType, Double>> levelPerEnemyDifficulty = new HashMap<>() {{
        for (DungeonDifficulty difficulty : DungeonDifficulty.values()) {
            switch (difficulty) {
                case Easy -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 1.0);
                    put(StatusType.ATK, 1.0);
                    put(StatusType.MAT, 1.0);
                    put(StatusType.DEF, 1.0);
                    put(StatusType.MDF, 1.0);
                    put(StatusType.SPT, 1.0);
                    put(StatusType.Critical, 1.0);
                    put(StatusType.CriticalDamage, 1.0);
                    put(StatusType.CriticalResist, 1.0);
                }});
                case Normal -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 3.0);
                    put(StatusType.ATK, 3.0);
                    put(StatusType.MAT, 3.0);
                    put(StatusType.DEF, 3.0);
                    put(StatusType.MDF, 3.0);
                    put(StatusType.SPT, 3.0);
                    put(StatusType.Critical, 1.5);
                    put(StatusType.CriticalDamage, 1.5);
                    put(StatusType.CriticalResist, 1.5);
                }});
                case Hard -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 6.0);
                    put(StatusType.ATK, 4.0);
                    put(StatusType.MAT, 4.0);
                    put(StatusType.DEF, 4.0);
                    put(StatusType.MDF, 4.0);
                    put(StatusType.SPT, 4.0);
                    put(StatusType.Critical, 2.0);
                    put(StatusType.CriticalDamage, 2.0);
                    put(StatusType.CriticalResist, 2.0);
                }});
                case Expert -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 10.0);
                    put(StatusType.ATK, 5.0);
                    put(StatusType.MAT, 5.0);
                    put(StatusType.DEF, 5.0);
                    put(StatusType.MDF, 5.0);
                    put(StatusType.SPT, 5.0);
                    put(StatusType.Critical, 3.0);
                    put(StatusType.CriticalDamage, 3.0);
                    put(StatusType.CriticalResist, 3.0);
                }});
            }
        }
    }};

    default void updateStatus() {
        HashMap<StatusType, Double> status = new HashMap<>();
        for (StatusType statusType : StatusType.BaseStatus()) {
            status.put(statusType, getBaseStatus().getOrDefault(statusType, 0.0));
        }

        if (this instanceof PlayerData playerData) {
            levelPerPlayer.forEach(((statusType, value) -> status.merge(statusType, value*(Math.min(getLevel(), playerData.getLevelSync())-1), Double::sum)));
            for (SomEquipment equipment : playerData.getEquipment().values()) {
                equipment.getStatusLevelSync(Math.min(playerData.getLevel(), playerData.getLevelSync()), playerData.getTierSync()).forEach(((statusType, value) -> status.merge(statusType, value, Double::sum)));
            }
        } else if (this instanceof EnemyData enemyData) {
            levelPerEnemy.get(enemyData.getMobData().getTier()).forEach(((statusType, value) -> status.merge(statusType, value * (getLevel() - 1), Double::sum)));
            levelPerEnemyDifficulty.get(enemyData.getDifficulty()).forEach(((statusType, value) -> status.put(statusType, status.get(statusType) * value)));
        }
        for (SomEffect effect : getEffect().values()) {
            effect.getStatus().forEach((type, value) -> {
                if (type != StatusType.DamageResist) {
                    status.merge(type, status.getOrDefault(type, 0.0) * value, Double::sum);
                } else {
                    status.merge(type, (1/value)-1, Double::sum);
                }
            });
        }
        status.forEach(this::setStatus);
        getLivingEntity().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(getStatus(StatusType.Movement)/1000f);
    }

    default void sendMessage(String message) {
        if (this instanceof PlayerData playerData) {
            playerData.sendMessage(message);
        }
    }

    default void sendMessage(String message, SomSound somSound) {
        if (this instanceof PlayerData playerData) {
            playerData.sendMessage(message, somSound);
        }
    }

    default CustomLocation getLocation() {
        return new CustomLocation(getLivingEntity().getLocation());
    }
    default CustomLocation getEyeLocation() {
        return new CustomLocation(getLivingEntity().getEyeLocation());
    }
    default CustomLocation getSoundLocation() {
        return new CustomLocation(getLivingEntity().getEyeLocation()).front(1);
    }
    default Vector getDirection() {
        return getLivingEntity().getEyeLocation().getDirection();
    }

    default CustomLocation getHipsLocation() {
        return new CustomLocation(getLivingEntity().getBoundingBox().getCenter().toLocation(getLivingEntity().getWorld()));
    }

    default CustomLocation getHandLocation() {
        CustomLocation location = getLocation();
        return location.addY(0.9).right(0.35).frontHorizon(0.5);
    }

    default void setVelocity(Vector velocity) {
        getLivingEntity().setVelocity(velocity);
    }

    default void removeHealth(double value) {
        double health = getHealth()-value;
        if (health > 0) {
            setHealth(health);
        } else {
            setHealth(0);
            death();
        }
    }

    default void removeMana(double value) {
        double mana = getMana()-value;
        if (mana > 0) {
            setMana(mana);
        } else {
            if (hasEffect("ArcaneEnergy")) {
                removeEffect("ArcaneEnergy");
            }
            removeHealth(Math.abs(mana));
        }
    }

    default Collection<PlayerData> getViewers() {
        if (this instanceof PlayerData playerData) {
            if (playerData.getDungeonMenu().isInDungeon()) {
                return playerData.getDungeonMenu().getDungeon().getMember();
            } else {
                return PlayerData.getPlayerList();
            }
        } else if (this instanceof EnemyData enemyData) {
            return enemyData.interactAblePlayers();
        }
        throw new RuntimeException("SomEntity.getViewers() is null. SomEntity must be 'PlayerData' or 'EnemyData'");
    }

    default Collection<PlayerData> getViewers(double radius) {
        return SomEntity.nearPlayer(getViewers(), getLocation(), radius);
    }

    default Collection<PlayerData> getViewers(Location location, double radius) {
        return SomEntity.nearPlayer(getViewers(), location, radius);
    }

    default Collection<PlayerData> getAllies() {
        List<PlayerData> alies = new ArrayList<>();
        if (this instanceof PlayerData playerData) {
            if (playerData.getDungeonMenu().isInDungeon()) {
                alies.addAll(playerData.getDungeonMenu().getDungeon().getMember());
            } else {
                alies.addAll(PlayerData.getPlayerList());
            }
            alies.removeIf(SomEntity::isDeath);
        }
        return alies;
    }

    default Collection<PlayerData> getAllies(double radius) {
        return SomEntity.nearPlayer(getAllies(), getLocation(), radius);
    }

    default Collection<PlayerData> getAlliesNoMe() {
        Collection<PlayerData> allies = getAllies();
        if (this instanceof PlayerData playerData) {
            allies.remove(playerData);
        }
        return allies;
    }

    default Collection<SomEntity> getTargets() {
        if (this instanceof PlayerData playerData) {
            if (playerData.getDungeonMenu().isInDungeon()) {
                return new ArrayList<>(playerData.getDungeonMenu().getDungeon().getEnemies());
            } else {
                Collection<SomEntity> list = new ArrayList<>(EnemyData.getGlobalEnemyList());
                if (playerData.getSetting().isPvPMode()) {
                    for (PlayerData loopPlayer : PlayerData.getPlayerList()) {
                        if (loopPlayer != playerData && !loopPlayer.getDungeonMenu().isInDungeon() && loopPlayer.getSetting().isPvPMode()) {
                            list.add(loopPlayer);
                        }
                    }
                }
                list.removeIf(SomEntity::isDeath);
                return list;
            }
        } else if (this instanceof EnemyData enemyData) {
            return new ArrayList<>(enemyData.interactAblePlayers());
        }
        throw new RuntimeException("SomEntity.getTargets() is null. SomEntity must be 'PlayerData' or 'EnemyData'");
    }

    default Collection<SomEntity> getTargets(double radius) {
        return SomEntity.nearSomEntity(getTargets(), getLocation(), radius);
    }

    default Collection<SomEntity> getTargets(CustomLocation location, double radius) {
        return SomEntity.nearSomEntity(getTargets(), location, radius);
    }

    default void setDamageEffect(DamageEffect damageEffect, double value) {
        synchronized (getDamageEffect()) {
            this.getDamageEffect().put(damageEffect, value);
        }
    }

    default void addDamageEffect(DamageEffect damageEffect, double value) {
        synchronized (getDamageEffect()) {
            this.getDamageEffect().merge(damageEffect, value, Double::sum);
        }
    }
}
