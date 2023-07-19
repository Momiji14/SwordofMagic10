package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Component.*;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomRune;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestEnemyKill;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import com.destroystokyo.paper.entity.Pathfinder;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Slime;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Dungeon.Instance.DungeonInstance.Radius;
import static SwordofMagic10.SomCore.Log;

public class EnemyData implements SomEntity {
    private static final List<EnemyData> enemyList = new ArrayList<>();
    private static final List<EnemyData> globalEnemyList = new ArrayList<>();
    private static final Random random = new Random();

    public static List<EnemyData> getEnemyList() {
        return enemyList;
    }

    public static List<EnemyData> getGlobalEnemyList() {
        globalEnemyList.removeIf(EnemyData::isInvalid);
        return globalEnemyList;
    }

    private final LivingEntity entity;
    private final MobData mobData;
    private boolean isDeath = false;
    private boolean isLoad = false;
    private final int level;
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private final HashMap<DamageEffect, Double> damageEffect = new HashMap<>();
    private final HashMap<String, SomEffect> effect = new HashMap<>();
    private SomEntity target;
    private SomEntity targetOverride;
    private final CustomLocation spawnLocation;
    private final Collection<PlayerData> interactAblePlayers;
    private BukkitTask aiTask = null;
    private BossBar healthBar;
    private final DungeonDifficulty difficulty;
    private final HashMap<SomEntity, Double> hateMap = new HashMap<>();
    private final HashMap<SomEntity, Double> damageMap = new HashMap<>();
    private final List<DropData> dropDataList;

    public static EnemyData spawn(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        if (mobData.getCustomClass() != null) {
            try {
                Class<?> bossClass = Class.forName("SwordofMagic10.Entity.Enemy.Boss." + mobData.getCustomClass());
                Constructor<?> constructor = bossClass.getConstructor(MobData.class, int.class, DungeonDifficulty.class, Location.class, Collection.class);
                return  (EnemyData) constructor.newInstance(mobData, level, difficulty, location, viewers);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new EnemyData(mobData, level, difficulty, location, viewers);
        }
    }
    protected EnemyData(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        interactAblePlayers = viewers;
        spawnLocation = new CustomLocation(location);
        this.level = level;
        this.mobData = mobData;
        this.difficulty = difficulty;
        dropDataList = new ArrayList<>(mobData.getDropDataList());
        World world = location.getWorld();
        switch (mobData.getEntityType()) {
            case SLIME -> {
                Slime slime = (Slime) world.spawnEntity(location, mobData.getEntityType(), false);
                slime.setSize(mobData.getSize());
                entity = slime;
            }
            case MAGMA_CUBE -> {
                MagmaCube magmaCube = (MagmaCube) world.spawnEntity(location, mobData.getEntityType(), false);
                magmaCube.setSize(mobData.getSize());
                entity = magmaCube;
            }
            default -> {
                entity = (LivingEntity) world.spawnEntity(location, mobData.getEntityType(), false);
            }
        }

        entity.setMetadata(Config.EnemyMetaAddress, new FixedMetadataValue(SomCore.plugin(), this));
        entity.setCustomName("§c《" + mobData.getDisplay() + " Lv" + level + "》");
        entity.setCustomNameVisible(true);
        entity.setSilent(true);
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            if (!viewers.contains(playerData)) {
                playerData.getPlayer().hideEntity(SomCore.plugin(), entity);
            }
        }
        if (mobData.getDisguise() != null) {
            Disguise disguise = mobData.getDisguise().clone();
            disguise.setEntity(entity);
            disguise.setDisguiseName(mobData.getDisplay());
            disguise.setDynamicName(true);
            disguise.setCustomDisguiseName(true);
            disguise.startDisguise();
        }

        updateStatus();
        setHealth(getMaxHealth());

        Mob mob = (Mob) entity;
        Pathfinder pathfinder = mob.getPathfinder();
        if (isBoss()) {
            healthBar = Bukkit.createBossBar("§c§l" + mobData.getDisplay(), BarColor.RED, BarStyle.SOLID);
        }
        aiTask = SomTask.syncTimer(() -> {
            if (!isInvalid()) {
                isLoad = true;
                if (isBoss()) {
                    healthBar.setProgress(getHealthPercent());
                    healthBar.removeAll();
                    for (PlayerData viewer : getViewers()) {
                        healthBar.addPlayer(viewer.getPlayer());
                    }
                }
                hateMap.keySet().removeIf(entity -> entity instanceof PlayerData playerData && playerData.getPlayer().getGameMode() != GameMode.ADVENTURE);
                hateMap.keySet().removeIf(SomEntity::isDeath);
                hateMap.keySet().removeIf(somEntity -> somEntity.getLocation().distance(getLocation()) > 128);
                if (hateMap.size() == 0) target = null;

                if (target == null) {
                    for (PlayerData viewer : getViewers(Radius)) {
                        addHate(viewer, randomDouble(0, 1));
                    }
                }

                double hate = 0;
                for (Map.Entry<SomEntity, Double> entry : hateMap.entrySet()) {
                    SomEntity entity = entry.getKey();
                    if (hate < entry.getValue() && !(entity.hasEffect("Fade") || entity.hasEffect("MistMidnight"))) {
                        target = entity;
                        hate = entry.getValue();
                    }
                }
                if (target != null) {
                    pathfinder.moveTo(target.getLocation());
                }
                if (!isSilence("Other")) {
                    for (PlayerData playerData : interactAblePlayers) {
                        if (mob.getBoundingBox().expand(1.5 + mobData.getCollisionSize()).contains(playerData.getPlayer().getBoundingBox())) {
                            attack(playerData);
                        }
                    }
                }
                tickEffect();
                tick();
            } else {
                delete();
            }
        }, 20);
    }

    public void setGlobal(boolean global) {
        if (global) {
            globalEnemyList.add(this);
        } else {
            globalEnemyList.remove(this);
        }
    }

    public boolean isBoss() {
        switch (mobData.getTier()) {
            case Boss, RaidBoss -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    protected EnemyData getEnemyData() {
        return this;
    }

    public void tick() {

    }

    public void sendViewerMessage(String message, SomSound sound) {
        for (PlayerData viewer : getViewers()) {
            viewer.sendMessage(message, sound);
        }
    }

    public CustomLocation getSpawnLocation() {
        return spawnLocation;
    }

    public DungeonDifficulty getDifficulty() {
        return difficulty;
    }

    public void attack(SomEntity entity) {
        Damage.makeDamage(this, entity, DamageEffect.None, DamageOrigin.ATK, 1);
    }

    public void addInteractAblePlayers(PlayerData playerData) {
        interactAblePlayers.add(playerData);
    }

    public void addHate(SomEntity entity, double hate) {
        hateMap.put(entity, hateMap.getOrDefault(entity, 0.0) + hate);
    }

    public void addDamage(SomEntity entity, double damage) {
        damageMap.merge(entity, damage, Double::sum);
    }

    @Override
    public Collection<PlayerData> interactAblePlayers() {
        return interactAblePlayers;
    }

    @Override
    public String getDisplayName() {
        return mobData.getDisplay();
    }

    @Override
    public LivingEntity getLivingEntity() {
        return entity;
    }

    @Override
    public HashMap<StatusType, Double> getBaseStatus() {
        return new HashMap<>() {{
            put(StatusType.MaxHealth, 100.0);
            put(StatusType.MaxMana, 100.0);
            put(StatusType.HealthRegen, 1.0);
            put(StatusType.ManaRegen, 1.0);
            put(StatusType.ATK, 10.0);
            put(StatusType.MAT, 10.0);
            put(StatusType.DEF, 10.0);
            put(StatusType.MDF, 10.0);
            put(StatusType.SPT, 10.0);
            put(StatusType.Critical, 10.0);
            put(StatusType.CriticalDamage, 10.0);
            put(StatusType.CriticalResist, 10.0);
            put(StatusType.DamageResist, 1.0);
            put(StatusType.Movement, 200.0);
        }};
    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    @Override
    public void hurt(Collection<PlayerData> viewers) {
        String type = mobData.getDisguise() != null ? mobData.getDisguise().getType().toString() : mobData.getEntityType().toString();
        try {
            Sound sound;
            switch (type) {
                case "CAVE_SPIDER" -> sound = Sound.ENTITY_SPIDER_HURT;
                case "ENDER_CRYSTAL" -> sound = Sound.BLOCK_GRASS_BREAK;
                case "MUSHROOM_COW" -> sound = Sound.ENTITY_COW_HURT;
                default -> sound = Sound.valueOf("ENTITY_" + type + "_HURT");
            }
            for (PlayerData viewer : viewers) {
                viewer.getPlayer().playSound(getLocation(), sound, SoundCategory.HOSTILE, 1f, 1f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log("§cEntityHurtSoundが存在しません -> " + type);
        }
    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    @Override
    public HashMap<DamageEffect, Double> getDamageEffect() {
        return damageEffect;
    }

    @Override
    public HashMap<String, SomEffect> getEffect() {
        return effect;
    }

    @Override
    public boolean isDeath() {
        return isDeath;
    }

    @Override
    public boolean isInvalid() {
        return isDeath() || !entity.isValid();
    }

    public SomEntity getTarget() {
        return target;
    }

    public SomEntity getTargetOverride() {
        return targetOverride;
    }

    public void setTargetOverride(SomEntity targetOverride) {
        this.targetOverride = targetOverride;
    }

    public boolean isTargeting() {
        return target != null;
    }

    public void delete() {
        enemyList.remove(this);
        aiTask.cancel();
        if (isBoss()) {
            healthBar.setProgress(0);
            healthBar.removeAll();
            healthBar.setVisible(false);
        }
        SomTask.sync(() -> {
            entity.setAI(false);
            entity.remove();
        });
    }

    public synchronized void death() {
        death(1);
    }

    public MobData getMobData() {
        return mobData;
    }

    @Override
    public int getLevel() {
        return level;
    }

    private CustomLocation deathLocation;
    public synchronized void death(double multiply) {
        if (!isDeath()) {
            isDeath = true;
            deathLocation = getLocation();
            delete();
            SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK).setRandomVector().setRandomSpeed(0.1f).setAmount(10);
            particle.spawn(getViewers(), entity.getBoundingBox().getCenter().toLocation(entity.getWorld()));
            List<String> damageRanking = new ArrayList<>();
            if (isBoss()) {
                damageRanking.add(decoText("ダメージランキング"));
                int i = 1;
                for (Map.Entry<SomEntity, Double> entry : damageMap.entrySet()) {
                    damageRanking.add(decoLore("[" + i + "]" + entry.getKey().getDisplayName()) + scale(entry.getValue()));
                    i++;
                }
            }
            int exp = (int) Math.ceil(mobData.getExp() * (1 + level/10f) * multiply);
            for (PlayerData playerData : new HashSet<>(interactAblePlayers)) {
                if (isBoss()) playerData.sendMessage(damageRanking, null);
                Classes classes = playerData.getClasses();
                ClassType mainClass = classes.getMainClass();
                classes.addExp(mainClass, exp);
                playerData.addEquipmentExp(exp, getLevel());
                if (playerData.getSetting().isExpLog()) {
                    playerData.sendMessage("§a[EXP+]§e装備EXP §e+" + exp);
                }
                playerData.getStatistics().add(Statistics.Enum.EnemyKill, 1);

                for (DropData dropData : dropDataList) {
                    double percent = dropData.getPercent();
                    if (random.nextDouble() < percent) {
                        int minAmount = dropData.getMinAmount();
                        int maxAmount = dropData.getMaxAmount();
                        int amount = minAmount == maxAmount ? maxAmount : random.nextInt(minAmount, maxAmount+1);
                        if (percent > 1) {
                            amount = (int) Math.floor(amount*percent);
                        }
                        SomItem item = dropData.getItem();
                        playerData.getItemInventory().add(item, amount);
                        String percentText = " §7(" + scale(percent*100, -1) + "%)";
                        if (playerData.getSetting().isItemLog()) {
                            playerData.sendSomText(SomText.create("§b[+]§r").addText(item.toSomText(amount)).addText(percentText));
                        }
                    }
                }

                double runePercent = 0.025;
                if (randomDouble(0, 1) < runePercent) {
                    SomRune rune = SomRune.getRandomRune();
                    rune.setQuality(randomDouble(0, 1));
                    rune.setLevel(getLevel());
                    rune.setTier(difficulty.ordinal()+1);
                    playerData.getItemInventory().add(rune, 1);
                }

                for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                    if (somQuest.getNowPhase() instanceof QuestEnemyKill questEnemyKill) {
                        questEnemyKill.kill(this);
                    }
                }
            }
        }
    }
}
