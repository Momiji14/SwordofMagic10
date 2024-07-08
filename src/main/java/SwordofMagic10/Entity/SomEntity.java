package SwordofMagic10.Entity;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.*;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.Dummy;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static SwordofMagic10.Component.Function.angle;
import static SwordofMagic10.Entity.Enemy.Dummy.DummyList;
import static SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle.DefensiveEnemy;
import static SwordofMagic10.SomCore.Log;

public interface SomEntity extends SomStatus.Basic {

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

    static List<SomEntity> nearestSomEntity(Collection<SomEntity> targets, CustomLocation location, double radius) {
        List<SomEntity> entityList = nearSomEntity(targets, location, radius);
        entityList.sort(Comparator.comparingDouble(enemy -> enemy.getLocation().distance(location)));
        return entityList;
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
    ConcurrentHashMap<DamageEffect, Double> getDamageEffect();
    ConcurrentHashMap<String, SomEffect> getEffect();

    Collection<PlayerData> interactAblePlayers();
    void hurt(Collection<PlayerData> viewers);
    boolean isDeath();

    boolean isInvalid();

    void death();

    default void addEffect(SomEffect effect, SomEntity owner) {
        if (effect.getTime() == -1 && hasEffect(effect.getId())) return;
        if (hasEffect(effect.getId())) {
            SomEffect currentEffect = getEffect(effect.getId());
            if (currentEffect.getUUID() == effect.getUUID()) {
                currentEffect.setTime(effect.getTime());
                currentEffect.setStack(effect.getStack());
                return;
            }
        }
        if (!effect.isBuff() && (effect.getRank() == SomEffect.Rank.Normal || effect.getRank() == SomEffect.Rank.High) && hasEffect("Stop")) {
            SomEffect forgiveness = getEffect("Stop");
            sendMessage(forgiveness.getDisplay() + "§aにより§c" + effect.getDisplay() + "§aが§c無力化§aされました");
            return;
        }
        if (!effect.isBuff() && effect.getRank() == SomEffect.Rank.Normal && hasEffect("Forgiveness")) {
            SomEffect forgiveness = getEffect("Forgiveness");
            forgiveness.addStack(-1);
            sendMessage(forgiveness.getDisplay() + "§aにより§c" + effect.getDisplay() + "§aが§c無力化§aされました");
            return;
        }
        if (!effect.isBuff() && effect.getRank() == SomEffect.Rank.Normal && hasEffect("Prophecy")) {
            SomEffect forgiveness = getEffect("Prophecy");
            sendMessage(forgiveness.getDisplay() + "§aにより§c" + effect.getDisplay() + "§aが§c無力化§aされました");
            return;
        }
        getEffect().put(effect.getId(), effect.clone());
        if (effect.isStatusUpdate()) {
            updateStatus();
        }
        if (owner instanceof PlayerData playerData && playerData.getSetting().isBuffLog()) {
            playerData.sendMessage("§b≫" + effect.getColorDisplay() + " §e[" + this.getDisplayName() + "§e]");
        }
        if (this != owner && this instanceof PlayerData playerData && playerData.getSetting().isBuffLog()) {
            playerData.sendMessage("§a≪" + effect.getColorDisplay() + " §e[" + owner.getDisplayName() + "§e]");
        }
    }

    default void removeEffect(String id) {
        if (id.equals("ArcaneEnergy")) {
            setMana(Math.max(getMana(), getEffect(id).getDoubleData()[0]));
        }
        if (this instanceof PlayerData playerData && playerData.getSetting().isBuffLog()) {
            playerData.sendMessage("§c≪" + getEffect(id).getColorDisplay());
        }
        getEffect().remove(id);
    }

    default void removeEffect(SomEffect effect) {
        removeEffect(effect.getId());
        if (effect.isStatusUpdate()) {
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

    default void clearEffectNonPotion() {
        getEffect().keySet().removeIf(key -> !key.equals(SomItem.ItemCategory.Cook.toString()) && !key.equals(SomItem.ItemCategory.Potion.toString()));
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

    default void tickEffect(int tick) {
        List<SomEffect> removeEffect = new ArrayList<>();
        getEffect().forEach((id, effect) -> {
            if (effect.isDelete()) {
                removeEffect.add(effect);
            }
            if (effect instanceof SomEffect.Area area) {
                if (area.getLocation().distance(getLocation()) > area.getRadius()) {
                    removeEffect.add(effect);
                }
            } else if (!(effect instanceof SomEffect.Toggle)) {
                effect.setTime(effect.getTime() - tick/20.0);
                if (effect.getStack() <= 0 || effect.getTime() <= 0) {
                    removeEffect.add(effect);
                }
            }
        });
        for (SomEffect effect : removeEffect) {
            removeEffect(effect);
        }
        if (this instanceof PlayerData playerData) {
            if (playerData.getSetting().isAutoBuff()) {
                for (SomEffect effect : removeEffect) {
                    if (effect.hasFromItem()) {
                        if (playerData.getItemInventory().has(effect.getFormItem(), 1)) {
                            effect.getFormItem().use(playerData);
                        }
                    }
                }
            }
        }
    }

    int getLevel();

    HashMap<StatusType, Double> levelPerPlayer = new HashMap<>() {{
        put(StatusType.MaxHealth, 7.5);
        put(StatusType.MaxMana, 7.5);
        put(StatusType.HealthRegen, 0.07);
        put(StatusType.ManaRegen, 0.07);
        put(StatusType.ATK, 1.0);
        put(StatusType.MAT, 1.0);
        put(StatusType.DEF, 1.0);
        put(StatusType.MDF, 1.0);
        put(StatusType.SPT, 1.0);
        put(StatusType.Critical, 1.0);
        put(StatusType.CriticalDamage, 1.0);
        put(StatusType.CriticalResist, 1.0);
    }};

    HashMap<MobData.Tier, HashMap<StatusType, Double>> levelPerEnemy = new HashMap<>() {{
        for (MobData.Tier tier : MobData.Tier.values()) {
            switch (tier) {
                case Normal -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 15.0);
                    put(StatusType.ATK, 3.5);
                    put(StatusType.MAT, 3.5);
                    put(StatusType.DEF, 3.5);
                    put(StatusType.MDF, 3.5);
                    put(StatusType.SPT, 3.5);
                    put(StatusType.Critical, 3.5);
                    put(StatusType.CriticalDamage, 1.5);
                    put(StatusType.CriticalResist, 3.5);
                }});
                case MiddleBoss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 200.0);
                    put(StatusType.ATK, 4.0);
                    put(StatusType.MAT, 4.0);
                    put(StatusType.DEF, 4.0);
                    put(StatusType.MDF, 4.0);
                    put(StatusType.SPT, 4.0);
                    put(StatusType.Critical, 4.0);
                    put(StatusType.CriticalDamage, 2.0);
                    put(StatusType.CriticalResist, 4.0);
                }});
                case Boss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 500.0);
                    put(StatusType.ATK, 4.5);
                    put(StatusType.MAT, 4.5);
                    put(StatusType.DEF, 4.5);
                    put(StatusType.MDF, 4.5);
                    put(StatusType.SPT, 4.5);
                    put(StatusType.Critical, 4.5);
                    put(StatusType.CriticalDamage, 2.5);
                    put(StatusType.CriticalResist, 4.5);
                }});
                case WorldRaidBoss, LegendRaidBoss -> put(tier, new HashMap<>() {{
                    put(StatusType.MaxHealth, 4500.0);
                    put(StatusType.ATK, 5.0);
                    put(StatusType.MAT, 5.0);
                    put(StatusType.DEF, 5.0);
                    put(StatusType.MDF, 5.0);
                    put(StatusType.SPT, 5.0);
                    put(StatusType.Critical, 5.0);
                    put(StatusType.CriticalDamage, 3.0);
                    put(StatusType.CriticalResist, 6.5);
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
                    put(StatusType.MaxHealth, 2.0);
                    put(StatusType.ATK, 2.0);
                    put(StatusType.MAT, 2.0);
                    put(StatusType.DEF, 2.0);
                    put(StatusType.MDF, 2.0);
                    put(StatusType.SPT, 2.0);
                    put(StatusType.Critical, 1.5);
                    put(StatusType.CriticalDamage, 1.5);
                    put(StatusType.CriticalResist, 2.5);
                }});
                case Hard -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 12.0);
                    put(StatusType.ATK, 4.5);
                    put(StatusType.MAT, 4.5);
                    put(StatusType.DEF, 4.5);
                    put(StatusType.MDF, 4.5);
                    put(StatusType.SPT, 4.5);
                    put(StatusType.Critical, 2.0);
                    put(StatusType.CriticalDamage, 2.0);
                    put(StatusType.CriticalResist, 3.5);
                }});
                case Expert -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 40.0);
                    put(StatusType.ATK, 6.0);
                    put(StatusType.MAT, 6.0);
                    put(StatusType.DEF, 10.0);
                    put(StatusType.MDF, 10.0);
                    put(StatusType.SPT, 6.0);
                    put(StatusType.Critical, 3.0);
                    put(StatusType.CriticalDamage, 3.0);
                    put(StatusType.CriticalResist, 5.0);
                }});

                case Extreme -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 150.0);
                    put(StatusType.ATK, 7.0);
                    put(StatusType.MAT, 7.0);
                    put(StatusType.DEF, 15.0);
                    put(StatusType.MDF, 15.0);
                    put(StatusType.SPT, 7.0);
                    put(StatusType.Critical, 4.0);
                    put(StatusType.CriticalDamage, 4.0);
                    put(StatusType.CriticalResist, 6.5);
                }});

                case Ultimate -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 500.0);
                    put(StatusType.ATK, 8.0);
                    put(StatusType.MAT, 8.0);
                    put(StatusType.DEF, 20.0);
                    put(StatusType.MDF, 20.0);
                    put(StatusType.SPT, 8.0);
                    put(StatusType.Critical, 5.0);
                    put(StatusType.CriticalDamage, 5.0);
                    put(StatusType.CriticalResist, 8.0);
                }});

                case NightMare -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 1700.0);
                    put(StatusType.ATK, 9.0);
                    put(StatusType.MAT, 9.0);
                    put(StatusType.DEF, 25.0);
                    put(StatusType.MDF, 25.0);
                    put(StatusType.SPT, 9.0);
                    put(StatusType.Critical, 6.0);
                    put(StatusType.CriticalDamage, 6.0);
                    put(StatusType.CriticalResist, 9.5);
                }});

                case Lunatic -> put(difficulty, new HashMap<>() {{
                    put(StatusType.MaxHealth, 2500.0);
                    put(StatusType.ATK, 10.0);
                    put(StatusType.MAT, 10.0);
                    put(StatusType.DEF, 30.0);
                    put(StatusType.MDF, 30.0);
                    put(StatusType.SPT, 10.0);
                    put(StatusType.Critical, 7.0);
                    put(StatusType.CriticalDamage, 7.0);
                    put(StatusType.CriticalResist, 11.0);
                }});
            }
        }
    }};

    default void updateStatus() {
        HashMap<StatusType, Double> basicStatus = new HashMap<>();
        HashMap<StatusType, Double> multiplyStatus = new HashMap<>();
        for (StatusType statusType : StatusType.BaseStatus()) {
            basicStatus.put(statusType, getBaseStatus().getOrDefault(statusType, 0.0));
            multiplyStatus.put(statusType, 1.0);
        }

        if (this instanceof PlayerData playerData) {
            levelPerPlayer.forEach(((statusType, value) -> basicStatus.merge(statusType, value*(Math.min(getLevel(), playerData.getLevelSync())-1), Double::sum)));
            for (SomEquip item : playerData.getEquipment().values()) {
                if (item instanceof SomEquipment equipItem) {
                    equipItem.getStatusLevelSync(Math.min(playerData.getLevel(), playerData.getLevelSync()), playerData.getTierSync()).forEach(((statusType, value) -> basicStatus.merge(statusType, value, Double::sum)));
                } else if (item instanceof SomAlchemyStone stone) {
                    if (stone.getExp() > 0) stone.getStatusLevelSync(Math.min(playerData.getLevel(), playerData.getLevelSync()), playerData.getTierSync()).forEach(((statusType, value) -> basicStatus.merge(statusType, value, Double::sum)));
                } else if (item instanceof SomAmulet amulet) {
                    amulet.getStatus().keySet().forEach(statusType -> multiplyStatus.merge(statusType, amulet.getAmuletStatus(statusType), Double::sum));
                } else {
                    Log(item.getId() + "のステータス計算が行われていません");
                }
            }
        } else if (this instanceof EnemyData enemyData) {
            levelPerEnemy.get(enemyData.getMobData().getTier()).forEach(((statusType, value) -> basicStatus.merge(statusType, value * (getLevel() - 1), Double::sum)));
            levelPerEnemyDifficulty.get(enemyData.getDifficulty()).forEach(((statusType, value) -> basicStatus.put(statusType, basicStatus.get(statusType) * value)));
            basicStatus.put(StatusType.MaxHealth, basicStatus.get(StatusType.MaxHealth) * enemyData.getHealthMultiply());
            if (enemyData.isInDungeon()) {
                switch (enemyData.getDungeon().getMatchType()) {
                    case Solo -> basicStatus.put(StatusType.MaxHealth, basicStatus.get(StatusType.MaxHealth)/3);
                    case Duo, FreeSearch -> basicStatus.put(StatusType.MaxHealth, basicStatus.get(StatusType.MaxHealth)/2);
                }
            }
            if (hasEffect(DefensiveEnemy)) {
                basicStatus.put(StatusType.Movement, 250.0);
            }
        } else if (this instanceof SomPet pet) {
            pet.getPetStatus().forEach((statusType, value) -> multiplyStatus.merge(statusType, value * pet.getLevel() * (0.75 + pet.getTier()*0.25), Double::sum));
        }
        HashMap<StatusType, Double> effectStatus = new HashMap<>();
        basicStatus.forEach(((statusType, value) -> basicStatus.put(statusType, value * multiplyStatus.get(statusType))));
        final double[] minMovementValue = {1};
        for (SomEffect effect : getEffect().values()) {
            effect.getMultiply().forEach((type, value) -> {
                if (effect.getRank() == SomEffect.Rank.Normal && type == StatusType.Movement && value < 0) {
                    if (this instanceof EnemyData enemyData && enemyData.getMobData().getTier() == MobData.Tier.LegendRaidBoss) return;
                    if (hasEffect("PainBarrier")) return;
                }
                if (hasEffect(DefensiveEnemy) && type == StatusType.Movement && value < 0) {
                    switch (effect.getRank()) {
                        case Normal -> value = effect.isStun() ? -0.4 : value/4;
                        case High -> value = effect.isStun() ? -0.7 : value/2;
                        case Impossible -> value = effect.isStun() ? -0.9 : value/2;
                    }
                    if (value < minMovementValue[0]) minMovementValue[0] = value;
                }
                switch (type) {
                    case DamageResist, CastTime, CoolTime, RigidTime -> effectStatus.merge(type, (effectStatus.getOrDefault(type, 1.0)/value)-1, Double::sum);
                    case DamageMultiply -> effectStatus.merge(type, (1 + effectStatus.getOrDefault(type, 0.0)) * value, Double::sum);
                    default -> effectStatus.merge(type, basicStatus.getOrDefault(type, 0.0) * value, Double::sum);
                }
            });
            effect.getFixed().forEach((type, value) -> effectStatus.merge(type, value, Double::sum));
        }
        HashMap<StatusType, Double> status = new HashMap<>();
        basicStatus.forEach(((statusType, value) -> status.merge(statusType, value, Double::sum)));
        effectStatus.forEach(((statusType, value) -> status.merge(statusType, value, Double::sum)));
        basicStatus.forEach(this::setBasicStatus);
        status.forEach(this::setStatus);
        //一番移動速度の下がるものだけ適応
        if (hasEffect(DefensiveEnemy) && getStatus(StatusType.Movement) < basicStatus.get(StatusType.Movement)) {
            setStatus(StatusType.Movement, basicStatus.get(StatusType.Movement) + basicStatus.get(StatusType.Movement) * minMovementValue[0]);
        }
        if (this instanceof PlayerData playerData) {
            playerData.getPlayer().setWalkSpeed((float) (getStatus(StatusType.Movement)/450f));
        } else if (getLivingEntity() != null) {
            getLivingEntity().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(getStatus(StatusType.Movement)/1000f);
        }
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
        CustomLocation location = new CustomLocation(getLivingEntity().getBoundingBox().getCenter().toLocation(getLivingEntity().getWorld()));
        location.setDirection(getDirection());
        return location;
    }

    default CustomLocation getHandLocation() {
        CustomLocation location = getLocation();
        return location.addY(0.9).right(0.35).frontHorizon(0.5);
    }

    default void setVelocity(Vector velocity) {
        if (hasEffect(DefensiveEnemy)) return;
        getLivingEntity().setVelocity(velocity);
    }

    default void removeHealth(double value) {
        double health = getHealth()-value;
        if (health > 0) {
            setHealth(health);
        } else {
            if (hasEffect("Revive")) {
                setHealth(1);
                removeEffect("Revive");
                addEffect(new SomEffect("Invincible", "無敵", true, 5).setInvincible(true), this);
                sendMessage("§cリバイブが死を防ぎ、砕けました", SomSound.Nope);
            } else {
                setHealth(0);
                death();
            }
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
            if (playerData.isInCity()) {
                return PlayerData.getPlayerListNonAFK();
            } else {
                return playerData.getMember();
            }
        } else if (this instanceof EnemyData enemyData) {
            return enemyData.interactAblePlayers();
        } else if (this instanceof Dummy dummy) {
            return dummy.interactAblePlayers();
        } else if (this instanceof SomPet pet) {
            return pet.getOwner().getViewers();
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
        List<PlayerData> alies = new ArrayList<>(getViewers());
        alies.removeIf(SomEntity::isDeath);
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
            if (playerData.isInCity()) {
                Collection<SomEntity> list = new ArrayList<>(EnemyData.getGlobalEnemyList());
                if (playerData.getSetting().isPvPMode()) {
                    for (PlayerData loopPlayer : PlayerData.getPlayerList()) {
                        if (loopPlayer != playerData && loopPlayer.isInCity() && loopPlayer.getSetting().isPvPMode()) {
                            list.add(loopPlayer);
                        }
                    }
                }
                list.removeIf(SomEntity::isDeath);
                list.addAll(DummyList);
                return list;
            } else if (playerData.getDungeonMenu().isInDungeon()) {
                return new ArrayList<>(playerData.getDungeonMenu().getDungeon().getEnemies());
            } else if (playerData.isInDefensiveBattle()) {
                return new ArrayList<>(playerData.getDefensiveBattle().getEnemies());
            } else {
                return new ArrayList<>(playerData.getMapData().getEnemies());
            }
        } else if (this instanceof EnemyData enemyData) {
            return new ArrayList<>(enemyData.interactAblePlayers());
        } else if (this instanceof Dummy dummy) {
            return new ArrayList<>(dummy.interactAblePlayers());
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
        this.getDamageEffect().put(damageEffect, value);
    }

    default void addDamageEffect(DamageEffect damageEffect, double value) {
        this.getDamageEffect().merge(damageEffect, value, Double::sum);
    }

    default boolean isBoss() {
        return  (this instanceof EnemyData enemyData && enemyData.isBoss());
    }
}
