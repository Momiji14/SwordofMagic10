package SwordofMagic10.Entity.Enemy;

import SwordofMagic10.Component.*;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomRecord;
import SwordofMagic10.Item.SomRune;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestEnemyKill;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.QuickGUI.RuneCrusher;
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
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static SwordofMagic10.Component.Config.AdjustExp;
import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle.DefensiveEnemy;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;
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
    private boolean isDelete = false;
    private boolean isLoad = false;
    private final int level;
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private final HashMap<StatusType, Double> basicStatus = new HashMap<>();
    private final ConcurrentHashMap<DamageEffect, Double> damageEffect = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SomEffect> effect = new ConcurrentHashMap<>();
    private final DungeonInstance dungeon;
    private final MapData mapData;
    private SomEntity target;
    private CustomLocation targetOverride;
    private SomEntity targetOverrideEntity;
    private int index = 0;
    private final CustomLocation spawnLocation;
    private final Collection<PlayerData> interactAblePlayers;
    private final BukkitTask aiTask;
    private final BukkitTask effectTask;
    private BossBar healthBar;
    private final DungeonDifficulty difficulty;
    private final ConcurrentHashMap<SomEntity, Double> hateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SomEntity, Double> damageMap = new ConcurrentHashMap<>();
    private final List<DropData> dropDataList;
    private double healthMultiply = 1;

    public static EnemyData spawn(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData) {
        return spawn(mobData, level, difficulty, location,  viewers, mapData, null);
    }

    public static EnemyData spawn(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        if (mobData.getCustomClass() != null) {
            try {
                Class<?> bossClass = Class.forName("SwordofMagic10.Entity.Enemy.Boss." + mobData.getCustomClass());
                Constructor<?> constructor = bossClass.getConstructor(MobData.class, int.class, DungeonDifficulty.class, Location.class, Collection.class, MapData.class, DungeonInstance.class);
                return  (EnemyData) constructor.newInstance(mobData, level, difficulty, location, viewers, mapData, dungeon);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new EnemyData(mobData, level, difficulty, location, viewers, mapData, dungeon);
        }
    }

    public static LivingEntity summonEntity(MobData mobData, Location location, Collection<PlayerData> viewers) {
        World world = location.getWorld();
        LivingEntity entity;
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
        return entity;
    }
    protected EnemyData(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        interactAblePlayers = viewers;
        this.dungeon = dungeon;
        this.mapData = mapData;
        spawnLocation = new CustomLocation(location);
        this.level = level;
        this.mobData = mobData;
        this.difficulty = difficulty;
        dropDataList = new ArrayList<>(mobData.getDropDataList());

        entity = summonEntity(mobData, location, viewers);
        entity.setMetadata(Config.EnemyMetaAddress, new FixedMetadataValue(SomCore.plugin(), this));
        entity.addScoreboardTag(Config.SomEntityTag);
        entity.setCustomName("§c《" + mobData.getDisplay() + " Lv" + level + "》");

        updateStatus();
        setHealth(getMaxHealth());

        Mob mob = (Mob) entity;
        Pathfinder pathfinder = mob.getPathfinder();
        if (isBoss()) {
            healthBar = Bukkit.createBossBar(bossBarTitle(), BarColor.RED, BarStyle.SEGMENTED_20);
        }
        effectTask = SomTask.timer(() -> tickEffect(2), 2);
        aiTask = SomTask.syncTimer(() -> {
            if (!isInvalid()) {
                isLoad = true;
                if (isBoss()) {
                    healthBar.setTitle(bossBarTitle());
                    healthBar.setProgress(getHealthPercent());
                    healthBar.removeAll();
                    for (PlayerData viewer : getViewers()) {
                        healthBar.addPlayer(viewer.getPlayer());
                    }
                }
                if (targetOverride == null && targetOverrideEntity == null) {
                    hateMap.keySet().removeIf(entity -> entity instanceof PlayerData playerData && playerData.isOnline() && playerData.getPlayer().getGameMode() != GameMode.ADVENTURE);
                    hateMap.keySet().removeIf(SomEntity::isInvalid);
                    hateMap.keySet().removeIf(somEntity -> somEntity.getLocation().distance(getLocation()) > 128);
                    if (hateMap.isEmpty()) target = null;

                    if (target == null) {
                        for (SomEntity somEntity : SomEntity.nearestSomEntity(getTargets(), getLocation(), Radius)) {
                            addHate(somEntity, 1);
                            break;
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
                    if (target != null && mob.getBoundingBox().getWidthX() + 0.3 < getLocation().distance(target.getLocation())) {
                        mob.setAI(true);
                        pathfinder.moveTo(target.getLocation());
                    } else {
                        pathfinder.stopPathfinding();
                        mob.setAI(false);
                    }
                } else if (targetOverrideEntity != null){
                    pathfinder.moveTo(targetOverrideEntity.getLocation());
                } else {
                    pathfinder.moveTo(targetOverride);
                }
                if (!isSilence("Other")) {
                    for (PlayerData playerData : interactAblePlayers) {
                        BoundingBox boundingBox = mob.getBoundingBox().expand(1.5 + mobData.getCollisionSize());
                        if (playerData.getPlayer().isOnGround()) boundingBox.expand(0, 8, 0);
                        if (boundingBox.contains(playerData.getPlayer().getBoundingBox())) {
                            attack(playerData);
                        }
                    }
                }
                tick();
            } else {
                delete();
            }
        }, 20);
    }

    public String bossBarTitle() {
        return "§c§l" + mobData.getDisplay() + " " + scale(getHealthPercent()*100, 2) + "%";
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
            case Boss, WorldRaidBoss, LegendRaidBoss -> {
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
        return mobData.getStatus();
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
                case "BLOCK_DISPLAY", "FALLING_BLOCK" -> sound = null;
                default -> sound = Sound.valueOf("ENTITY_" + type + "_HURT");
            }
            if (sound != null) for (PlayerData viewer : viewers) {
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
    public HashMap<StatusType, Double> getBasicStatus() {
        return basicStatus;
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
    public boolean isDeath() {
        return isDeath;
    }

    @Override
    public boolean isInvalid() {
        return isDeath() || isDelete || !entity.isValid();
    }

    public SomEntity getTarget() {
        return target;
    }

    public MapData getMapData() {
        return mapData;
    }

    public DungeonInstance getDungeon(){
        return dungeon;
    }

    public boolean isInDungeon() {
        return dungeon != null;
    }

    public CustomLocation getTargetOverride() {
        return targetOverride;
    }

    public void setTargetOverride(CustomLocation targetOverride) {
        this.targetOverride = targetOverride;
    }

    public SomEntity getTargetOverrideEntity() {
        return targetOverrideEntity;
    }

    public void setTargetOverrideEntity(SomEntity targetOverrideEntity) {
        this.targetOverrideEntity = targetOverrideEntity;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isTargeting() {
        return target != null;
    }

    public double getHealthMultiply() {
        return healthMultiply;
    }

    public EnemyData setHealthMultiply(double healthMultiply) {
        this.healthMultiply = healthMultiply;
        updateStatus();
        setHealth(getMaxHealth());
        return this;
    }

    public void delete() {
        isDelete = true;
        enemyList.remove(this);
        aiTask.cancel();
        effectTask.cancel();
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
            SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, this).setRandomVector().setRandomSpeed(0.1f).setAmount(10);
            particle.spawn(getViewers(), entity.getBoundingBox().getCenter().toLocation(entity.getWorld()));
            List<String> damageRanking = new ArrayList<>();
            if (isBoss()) {
                damageRanking.add(decoText("ダメージランキング"));
                int i = 1;
                for (Map.Entry<SomEntity, Double> entry : damageMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
                    damageRanking.add(decoLore("[" + i + "]" + entry.getKey().getDisplayName()) + scale(entry.getValue()));
                    i++;
                }
            }
            Set<PlayerData> dropPlayer = new HashSet<>();
            double exp = (Classes.getExp(level)/level) * (0.8 + difficulty.getMultiply() * 0.2) * mobData.getTier().getMultiply() * multiply;
            if (dungeon != null ) {
                if (isBoss()) dungeon.clearTime();
                dropPlayer.addAll(interactAblePlayers);
            } else {
                for (PlayerData playerData : SomEntity.nearPlayer(mapData.getPlayerList(), deathLocation, 32)) {
                    if (playerData.hasParty()) {
                        dropPlayer.addAll(playerData.getParty().getMember());
                    } else dropPlayer.add(playerData);
                }
            }
            for (PlayerData playerData : dropPlayer) {
                if (playerData.isAFK() && (dungeon == null || !dungeon.isLegendRaid())) continue;
                if (isBoss()) playerData.sendMessage(damageRanking, null);
                if (hasEffect(DefensiveEnemy)) continue;
                Classes classes = playerData.getClasses();
                ClassType mainClass = classes.getMainClass();
                classes.addExp(mainClass, exp * AdjustExp(playerData, getLevel()));
                int equipmentExp = 2+(level/10);
                playerData.addEquipmentExp(equipmentExp, getLevel());
                playerData.getStatistics().add(Statistics.Type.EnemyKill, 1);
                if (dungeon != null) dungeon.equipmentDrop(playerData, 0.01);

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
                        playerData.getItemInventory().add(item, amount, percent);
                        if (item instanceof SomRecord) {
                            SomCore.globalMessageComponent(SomText.create(playerData.getDisplayName()).add("§aが").add(item.toSomText()).add("§aを§e獲得§aしました"));
                        }
                    }
                }

                double runePercent = 0.025;
                if (randomDouble(0, 1) < runePercent) {
                    SomRune rune = SomRune.getRandomRune();
                    rune.setQuality(randomDouble(0, 1));
                    rune.setLevel(getLevel());
                    rune.setTier(difficulty.ordinal()+1);
                    rune.randomPower();
                    if (playerData.getSetting().isRuneAutoCrash()) {
                        playerData.getItemInventory().add(RuneCrusher.getPowder(rune), RuneCrusher.amount(rune), runePercent);
                    } else {
                        playerData.getItemInventory().add(rune, 1, runePercent);
                    }
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
