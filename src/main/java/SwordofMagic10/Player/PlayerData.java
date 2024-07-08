package SwordofMagic10.Player;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.DataBase.QuestDataLoader;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.Achievement.AchievementMenu;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.DungeonMenu;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.*;
import SwordofMagic10.Player.Gathering.ProduceGame.ProduceGame;
import SwordofMagic10.Player.Help.HelpMenu;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.Menu.*;
import SwordofMagic10.Player.Quest.QuestMenu;
import SwordofMagic10.Player.Quest.QuestPhase;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.Shop.SellManager;
import SwordofMagic10.Player.Shop.ShopManager;
import SwordofMagic10.Player.Skill.SkillManager;
import SwordofMagic10.SomCore;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static SwordofMagic10.Component.Config.DateFormat;
import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.SpawnLocation;

public class PlayerData implements SwordofMagic10.Entity.SomEntity, SomGuild.Member {

    private static final ConcurrentHashMap<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    public static PlayerData get(Player player) {
        if (!playerData.containsKey(player.getUniqueId())) {
            playerData.put(player.getUniqueId(), new PlayerData(player));
        }
        return playerData.get(player.getUniqueId());
    }

    public static Collection<PlayerData> getPlayerList() {
        playerData.values().removeIf(playerData -> !playerData.isOnline());
        return playerData.values();
    }

    public static Collection<PlayerData> getPlayerListNonAFK() {
        List<PlayerData> list = new ArrayList<>(getPlayerList());
        list.removeIf(PlayerData::isAFK);
        return list;
    }

    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (PlayerData playerData : playerData.values()) {
            complete.add(playerData.getPlayer().getName());
        }
        return complete;
    }

    public static String getUsername(UUID uuid) {
        return SomSQL.getString("PlayerData", "UUID", uuid.toString(), "Username");
    }

    private final Player player;
    private final List<GUIManager> guiManagerList = new ArrayList<>();
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private final HashMap<StatusType, Double> basicStatus = new HashMap<>();
    private final ConcurrentHashMap<DamageEffect, Double> damageEffect = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SomEffect> effect = new ConcurrentHashMap<>();
    private final HashMap<EquipSlot, SomEquip> equipment = new HashMap<>();
    private SomParty party;
    private int levelSync = Classes.MaxLevel;
    private int tierSync = DungeonDifficulty.values().length;
    private int mel = 10000;
    private final HashMap<String, Integer> viewAmount = new HashMap<>();
    private final InventoryViewer inventoryViewer;
    private final Classes classes;
    private final DungeonMenu dungeonMenu;
    private final UserMenu userMenu;
    private final PalletMenu palletMenu;
    private final StorageMenu storageMenu;
    private final SomInventory itemInventory;
    private final SomInventory itemStorage;
    private final ShopManager shopManager;
    private final SellManager sellManager;
    private final SkillManager skillManager;
    private final UpgradeMenu upgradeMenu;
    private final EnhanceMenu enhanceMenu;
    private final TierMenu tierMenu;
    private final QualityMenu qualityMenu;
    private final RuneMenu runeMenu;
    private final RuneSynthesis runeSynthesis;
    private final RemakeMenu remakeMenu;
    private final QuestMenu questMenu;
    private final HelpMenu helpMenu;
    private final SeriesMenu seriesMenu;
    private final LevelReduceMenu levelReduceMenu;
    private final Setting setting;
    private final SettingMenu settingMenu;
    private final EquipmentMenu equipmentMenu;
    private final InfoMenu infoMenu;
    private final Mining mining;
    private final Lumber lumber;
    private final Collect collect;
    private final Fishing fishing;
    private final Hunting hunting;
    private final Produce produce;
    private final ProduceGame produceGame;
    private final GatheringMenu gatheringMenu;
    private final AmuletMenu amuletMenu;
    private final Statistics statistics;
    private final Market market;
    private final Order order;
    private final AchievementMenu achievementMenu;
    private final PetMenu petMenu;

    private int afkTime = 0;
    private CustomLocation afkLocation;
    private Location locationLock;
    private Location playerSpawn;
    private Location lastLocation;
    private double movementSpeed = 0;
    private DefensiveBattle defensiveBattle;
    private MapData mapData;

    public PlayerData(Player player) {
        this.player = player;
        dungeonMenu = new DungeonMenu(this);
        userMenu = new UserMenu(this);
        skillManager = new SkillManager(this);
        classes = new Classes(this);
        inventoryViewer = new InventoryViewer(this);
        itemInventory = new SomInventory(this);
        itemStorage = new SomInventory(this);
        palletMenu = new PalletMenu(this);
        storageMenu = new StorageMenu(this);
        shopManager = new ShopManager(this);
        sellManager = new SellManager(this);
        upgradeMenu = new UpgradeMenu(this);
        enhanceMenu = new EnhanceMenu(this);
        tierMenu = new TierMenu(this);
        qualityMenu = new QualityMenu(this);
        runeMenu = new RuneMenu(this);
        runeSynthesis = new RuneSynthesis(this);
        remakeMenu = new RemakeMenu(this);
        helpMenu = new HelpMenu(this);
        seriesMenu = new SeriesMenu(this);
        levelReduceMenu = new LevelReduceMenu(this);
        questMenu = new QuestMenu(this);
        equipmentMenu = new EquipmentMenu(this);
        infoMenu = new InfoMenu(this);
        gatheringMenu = new GatheringMenu(this);
        mining = new Mining(this);
        lumber = new Lumber(this);
        collect = new Collect(this);
        fishing = new Fishing(this);
        hunting = new Hunting(this);
        produce = new Produce(this);
        produceGame = new ProduceGame(this);
        market = new Market(this);
        order = new Order(this);
        amuletMenu = new AmuletMenu(this);
        statistics = new Statistics(this);
        achievementMenu = new AchievementMenu(this);
        petMenu = new PetMenu(this);


        setting = new Setting(this);
        settingMenu = new SettingMenu(this);
        lastLocation = new CustomLocation(player.getLocation());
        deathLocation = new CustomLocation(player.getLocation());
        afkLocation = new CustomLocation(player.getLocation());
        playerSpawn = SpawnLocation;
        MapDataLoader.getMapData("Alaine").enter(this);
        initialize();
    }

    @Override
    public String getDisplayName() {
        return getDisplayColor() + player.getName();
    }

    @Override
    public LivingEntity getLivingEntity() {
        return player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getUsername() {
        return player.getName();
    }

    public List<GUIManager> getGuiManagerList() {
        return guiManagerList;
    }

    public void addGUIManager(GUIManager manager) {
        guiManagerList.add(manager);
    }

    public SomInventory getItemInventory() {
        return itemInventory;
    }

    public SomInventory getItemStorage() {
        return itemStorage;
    }

    public InventoryViewer getInventoryViewer() {
        return inventoryViewer;
    }

    public PalletMenu getPalletMenu() {
        return palletMenu;
    }

    public StorageMenu getStorageMenu() {
        return storageMenu;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SellManager getSellManager() {
        return sellManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public UpgradeMenu getUpgradeMenu() {
        return upgradeMenu;
    }

    public EnhanceMenu getEnhanceMenu() {
        return enhanceMenu;
    }

    public TierMenu getTierMenu() {
        return tierMenu;
    }

    public QualityMenu getQualityMenu() {
        return qualityMenu;
    }

    public RuneMenu getRuneMenu() {
        return runeMenu;
    }

    public RuneSynthesis getRuneSynthesis() {
        return runeSynthesis;
    }

    public RemakeMenu getRemakeMenu() {
        return remakeMenu;
    }

    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    public SeriesMenu getSeriesMenu() {
        return seriesMenu;
    }

    public LevelReduceMenu getLevelReduceMenu() {
        return levelReduceMenu;
    }

    public QuestMenu getQuestMenu() {
        return questMenu;
    }

    public Setting getSetting() {
        return setting;
    }

    public SettingMenu getSettingMenu() {
        return settingMenu;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Classes getClasses() {
        return classes;
    }

    public DungeonMenu getDungeonMenu() {
        return dungeonMenu;
    }

    public UserMenu getUserMenu() {
        return userMenu;
    }

    public EquipmentMenu getEquipmentMenu() {
        return equipmentMenu;
    }

    public InfoMenu getInfoMenu() {
        return infoMenu;
    }

    public GatheringMenu getGatheringMenu() {
        return gatheringMenu;
    }

    public Mining getMining() {
        return mining;
    }

    public Lumber getLumber() {
        return lumber;
    }

    public Collect getCollect() {
        return collect;
    }

    public Fishing getFishing() {
        return fishing;
    }

    public Hunting getHunting() {
        return hunting;
    }

    public Produce getProduce() {
        return produce;
    }

    public ProduceGame getProduceGame() {
        return produceGame;
    }

    public Market getMarket() {
        return market;
    }

    public Order getOrder() {
        return order;
    }

    public AmuletMenu getAmuletMenu() {
        return amuletMenu;
    }

    public AchievementMenu getAchievementMenu() {
        return achievementMenu;
    }

    public PetMenu getPetMenu() {
        return petMenu;
    }

    public Location getLastLocation() {
        return lastLocation != null ? lastLocation : player.getLocation();
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public DefensiveBattle getDefensiveBattle() {
        return defensiveBattle;
    }

    public void setDefensiveBattle(DefensiveBattle defensiveBattle) {
        this.defensiveBattle = defensiveBattle;
    }

    public boolean isInDefensiveBattle() {
        return defensiveBattle != null;
    }

    public int getLevelSync() {
        return levelSync;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setLevelSync(int levelSync) {
        this.levelSync = levelSync;
        if (getRawLevel() > levelSync) {
            sendMessage("§eレベルシンク§aにより§eステータス§aが§c低下§aします");
        }
    }

    public void resetLevelSync() {
        if (getRawLevel() > levelSync) {
            sendMessage("§eレベルシンク§aが§b解除§aされました");
        }
        levelSync = Classes.MaxLevel;
    }

    public int getTierSync() {
        return tierSync;
    }

    boolean isTierSync = false;
    public void setTierSync(int tierSync) {
        this.tierSync = tierSync;
        for (SomEquip item : equipment.values()) {
            if (item.getTier() > tierSync) {
                tierSyncMessage();
                return;
            }
            if (item instanceof SomEquipment equipItem) {
                for (SomRune rune : equipItem.getRune()) {
                    if (rune.getTier() > tierSync) {
                        tierSyncMessage();
                        return;
                    }
                }
            }
        }
    }

    public void tierSyncMessage() {
        sendMessage("§eティアシンク§aにより§eステータス§aが§c低下§aします");
        isTierSync = true;
    }

    public void resetTierSync() {
        if (isTierSync) {
            sendMessage("§eティアシンク§aが§b解除§aされました");
            isTierSync = false;
        }
        tierSync = DungeonDifficulty.values().length;
    }

    public SomParty getParty() {
        return party;
    }

    public boolean hasParty() {
        return party != null;
    }

    public void setParty(SomParty party) {
        this.party = party;
    }

    public void setRank(PlayerRank rank) {
        SomSQL.setSql("PlayerRank", "UUID", getUUIDAsString(), "Rank", rank.toString());
    }

    public boolean hasRank(PlayerRank rank) {
        return getRank().ordinal() >= rank.ordinal();
    }

    public PlayerRank getRank() {
        if (SomSQL.existSql("PlayerRank", "UUID", getUUIDAsString())) {
            return PlayerRank.valueOf(SomSQL.getString("PlayerRank", "UUID", getUUIDAsString(), "Rank"));
        } else {
            return PlayerRank.Normal;
        }
    }

    public void setPlayerSpawn(Location playerSpawn) {
        this.playerSpawn = playerSpawn;
    }

    public Location getPlayerSpawn() {
        return playerSpawn != null ? playerSpawn : deathLocation;
    }

    public int getMel() {
        return mel;
    }

    public void setMel(int mel) {
        this.mel = mel;
    }

    public void addMel(int mel) {
        this.mel += mel;
    }

    public void removeMel(int mel) {
        this.mel -= mel;
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    public String getUUIDAsString() {
        return getUUID().toString();
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public HashMap<String, Integer> getViewAmount() {
        return viewAmount;
    }

    public BossBar sendBossBarMessage(String text, int delay, boolean chat) {
        return sendBossBarMessage(text, BarColor.WHITE, delay, chat);
    }

    public BossBar sendBossBarMessage(String text, BarColor color, int delay, boolean chat) {
        if (chat) sendMessage(text);
        BossBar bossBar = Bukkit.createBossBar(text, color, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        if (delay > 0) SomTask.syncDelay(bossBar::removeAll, delay);
        return bossBar;
    }

    public void sendMessage(String text) {
        sendMessage(text, null);
    }

    public void sendMessage(String text, SomSound sound) {
        player.sendMessage(hexColor(text));
        if (sound != null) sound.play(this);
    }

    public void sendMessage(List<String> text, SomSound sound) {
        for (String str : text) {
            player.sendMessage(hexColor(str));
        }
        if (sound != null) sound.play(this);
    }

    public void sendSomText(SomText text) {
        sendSomText(text, null);
    }

    public void sendSomText(SomText text, @Nullable SomSound sound) {
        player.sendMessage(text.toComponent());
        if (sound != null) sound.play(this);
    }

    public void sendSomText(List<SomText> texts, @Nullable SomSound sound) {
        for (SomText text : texts) {
            player.sendMessage(text.toComponent());
        }
        if (sound != null) sound.play(this);
    }

    public void sendMessageNonMel() {
        sendMessage("§eメル§aが足りません", SomSound.Nope);
    }

    public boolean sendMessageIsAFK() {
        if (isAFK()) {
            sendMessage("§7AFK中§aは出来ません", SomSound.Nope);
            return true;
        }
        return false;
    }

    public void sendMessageReqRank(PlayerRank rank) {
        sendMessage("§aこの§e機能§aを§e利用§aするには" + rank.getDisplay() + "§a以上§aが§c必要§aです", SomSound.Nope);
    }

    public void sendIsFavorite() {
        sendMessage("§dお気に入り§eアイテム§aです", SomSound.Nope);
    }

    public boolean sendMessageIsSomReload() {
        if (SomCore.SomReload) {
            sendMessage("§4リロード中§aは出来ません", SomSound.Nope);
            sendMessage("§4リロード中§aは出来ません", SomSound.Nope);
            return true;
        }
        return false;
    }

    public boolean sendMessageIsInCity() {
        if (!isInCity()) {
            sendMessage("§b街内§aでのみ§e利用§aできます");
            return false;
        } else return true;
    }


    public void sendTitle(String title, String subtitle, int in, int time, int out) {
        player.sendTitle(hexColor(title), hexColor(subtitle), in, time, out);
    }

    @Override
    public HashMap<StatusType, Double> getBaseStatus() {
        return new HashMap<>() {{
            put(StatusType.MaxHealth, 100.0);
            put(StatusType.MaxMana, 100.0);
            put(StatusType.HealthRegen, 1.0);
            put(StatusType.ManaRegen, 1.0);
            put(StatusType.ATK, 0.0);
            put(StatusType.MAT, 0.0);
            put(StatusType.DEF, 0.0);
            put(StatusType.MDF, 0.0);
            put(StatusType.SPT, 0.0);
            put(StatusType.Hate, 100.0);
            put(StatusType.Critical, 10.0);
            put(StatusType.CriticalDamage, 10.0);
            put(StatusType.CriticalResist, 10.0);
            put(StatusType.CastTime, 1.0);
            put(StatusType.CoolTime, 1.0);
            put(StatusType.RigidTime, 1.0);
            put(StatusType.DamageMultiply, 1.0);
            put(StatusType.DamageResist, 1.0);
            put(StatusType.Movement, 100.0);
        }};
    }

    @Override
    public Collection<PlayerData> interactAblePlayers() {
        Set<PlayerData> list = new HashSet<>();
        list.add(this);
        if (dungeonMenu.isInDungeon()) {
            list.addAll(dungeonMenu.getDungeon().getMember());
        } else if (isInDefensiveBattle()) {
            list.addAll(defensiveBattle.getMember());
        }
        return list;
    }



    public boolean isLocationLock() {
        return locationLock != null;
    }

    public Location getLocationLock() {
        return locationLock;
    }

    public void setLocationLock(Location locationLock) {
        this.locationLock = locationLock;
        player.setAllowFlight(isLocationLock());
        player.setFlying(isLocationLock());
        player.setFlySpeed(isLocationLock() ? 0.0f : 0.2f);
    }

    public void resetLocationLock() {
        setLocationLock(null);
    }

    private int evasionWait = 0;
    public void evasion() {
        if (isEvasionAble() && !isLocationLock()) {
            Vector vector;
            double multiply = getStatus(StatusType.Movement)/50f;
            if (!player.isInWater()) {
                vector = getDirection().setY(0);
                vector.multiply(multiply);
                if (player.isSneaking()) vector.multiply(-1);
                vector.setY(0.5);
            } else {
                vector = getDirection().multiply(multiply);
            }
            player.setVelocity(vector);
            setEvasionWait(20);
        }
    }

    public void setInvisibility(int tick) {
        SomTask.sync(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, tick, 0, true, true)));
    }

    public boolean isEvasionAble() {
        return evasionWait <= 0;
    }

    public void setEvasionWait(int evasionWait) {
        this.evasionWait = evasionWait;
        inventoryViewer.updateBar(true);
    }

    private static final int rafLevel = 5;
    public void addEquipmentExp(int addExp, int level) {
        int max = 0;
        for (EquipSlot equipSlot : EquipSlot.values()) {
            SomEquip item = getEquipment(equipSlot);
            if (item instanceof SomEquipment equipItem) {
                if (level >= equipItem.getLevel()-rafLevel) {
                    max = addExp;
                    equipItem.addExp(addExp);
                } else {
                    int exp = (int) (addExp * 0.1)+1;
                    if (exp > max) max = exp;
                    equipItem.addExp(exp);
                }
            }
        }
        if (getSetting().isExpLog()) {
            sendMessage("§a[ExpLog]§r装備EXP §e+" + max);
        }
    }

    public List<PlayerData> getMember() {
        List<PlayerData> list = new ArrayList<>();
        if (dungeonMenu.isInDungeon()) {
            list.addAll(dungeonMenu.getDungeon().getMember());
        } else if (isInDefensiveBattle()) {
            list.addAll(getDefensiveBattle().getMember());
        } else if (!isInInstance()) {
            list.addAll(mapData.getPlayerList());
        } else {
            list.add(this);
        }
        return list;
    }

    public List<PlayerData> getMemberNoDeath() {
        List<PlayerData> list = new ArrayList<>(getMember());
        list.removeIf(PlayerData::isDeath);
        return list;
    }

    public List<PlayerData> getMemberNoDeathNoMe() {
        List<PlayerData> list = new ArrayList<>(getMemberNoDeath());
        list.remove(this);
        return list;
    }

    public List<PlayerData> getMemberNoMe() {
        List<PlayerData> list = new ArrayList<>(getMember());
        list.remove(this);
        return list;
    }

    public boolean isInCity() {
        return mapData.getId().equals("Alaine") && !isInInstance();
    }

    public boolean isInInstance() {
        return dungeonMenu.isInDungeon() || isInDefensiveBattle();
    }
    
    public boolean hasBottle(String bottleID) {
        if (hasEquipment(EquipSlot.Amulet)) {
            if (getEquipment(EquipSlot.Amulet) instanceof SomAmulet amulet) {
                for (SomAmulet.Bottle bottle : amulet.getBottles()) {
                    if (bottle.getId().equals(bottleID)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasBottle(SomAmulet.Bottle bottle) {
        if (hasEquipment(EquipSlot.Amulet)) {
            if (getEquipment(EquipSlot.Amulet) instanceof SomAmulet amulet) {
                for (SomAmulet.Bottle hasBottle : amulet.getBottles()) {
                    if (hasBottle.getId().equals(bottle.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void hurt(Collection<PlayerData> viewers) {
        player.sendHurtAnimation(0);
        SomSound.Hurt.playRadius(player.getLocation(), viewers);
    }
    @Override
    public boolean isDeath() {
        return isDeath;
    }

    @Override
    public boolean isInvalid() {
        return isDeath || !player.isOnline();
    }

    public boolean isOnline() {
        return player.isOnline();
    }

    private boolean isDeath = false;
    private CustomLocation deathLocation;
    private int respawnWait;
    private boolean respawn = false;
    private BukkitTask respawnTask;

    public void setRespawnWait(int respawnWait) {
        this.respawnWait = respawnWait;
    }

    public Collection<SomEntity> getDeathAllies() {
        List<SomEntity> alies = new ArrayList<>(getViewers());
        alies.removeIf(entity -> !entity.isDeath());
        return alies;
    }

    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    public CustomLocation getDeathLocation() {
        return deathLocation;
    }

    @Override
    public void death() {
        death(null);
    }
    public synchronized void death(String message) {
        if (isDeath || hasEffect("ShadowPool")) return;
        if (message != null) sendMessage(message);
        isDeath = true;
        deathLocation = getLocation();
        setPlayerSpawn(mapData.getGateLocation());
        SomTask.sync(() -> {
            for (Entity passenger : player.getPassengers()) {
                player.removePassenger(passenger);
            }
            player.closeInventory();
            setLocationLock(deathLocation);
            Entity deathEntity = player.getWorld().spawnEntity(deathLocation, EntityType.INTERACTION);
            deathEntity.addScoreboardTag(Config.SomEntityTag);
            PlayerDisguise disguise = new PlayerDisguise(player);
            PlayerWatcher watcher = disguise.getWatcher();
            watcher.setSleeping(true);
            disguise.setNameVisible(false);
            disguise.setEntity(deathEntity);
            disguise.setWatcher(watcher);
            disguise.startDisguise();
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§4You Are Dead", "§aRespawn Wait...", 20, 100, 0);
            respawnWait = 10;
            if (getDungeonMenu().isInDungeon()) {
                DungeonInstance dungeon = getDungeonMenu().getDungeon();
                dungeon.addDeathCount(this);
                if (dungeon.getMatchType() == DungeonMenu.MatchType.Solo) {
                    respawnWait = 0;
                }
            }
            respawnTask = SomTask.timer(() -> {
                if (dungeonMenu.isInDungeon() && dungeonMenu.getDungeon().isLegendRaid()) {
                    player.sendTitle("§4You Are Dead", "§eWait Resurrection", 0, 30, 10);
                } else {
                    player.sendTitle("§4You Are Dead", "§aRespawn " + respawnWait + " sec", 0, 30, 10);
                    respawnWait--;
                }
                if (respawnWait < 0) respawn = true;
                if (respawn) {
                    respawnTask.cancel();
                    isDeath = false;
                    heal();
                    resetLocationLock();
                    respawn = false;
                    addEffect(SomEffect.List.Invincible.getEffect().setTime(3), this);
                    SomTask.sync(() -> {
                        player.setGameMode(GameMode.ADVENTURE);
                        disguise.stopDisguise();
                        deathEntity.remove();
                        if (respawnWait < 0) {
                            teleport(getPlayerSpawn());
                        } else {
                            teleport(deathLocation);
                        }
                    });
                }
                if (!isOnline()) {
                    respawnTask.cancel();
                    SomTask.sync(() -> {
                        disguise.stopDisguise();
                        deathEntity.remove();
                    });
                }
            }, 100, 20);
        });
    }

    public boolean isBE() {
        return player.getName().charAt(0) == '.';
    }

    public void heal() {
        setHealth(getMaxHealth());
        setMana(getMaxMana());
    }

    @Override
    public int getLevel() {
        return Math.min(levelSync, classes.getLevel(classes.getMainClass()));
    }

    public int getRawLevel() {
        return  classes.getLevel(classes.getMainClass());
    }

    public boolean isAFK() {
        if (dungeonMenu.isInDungeon() && dungeonMenu.getDungeon().isLegendRaid()) return false;
        if (produceGame.isInGame() || fishing.isFishing()) {
            return afkTime >= 900;
        }
        return afkTime >= 300;
    }

    public void setAfkTime(int afkTime) {
        this.afkTime = afkTime;
    }

    public boolean isPlayMode() {
        return setting.isPlayMode();
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

    public HashMap<EquipSlot, SomEquip> getEquipment() {
        return equipment;
    }

    public SomEquip getEquipment(EquipSlot equipSlot) {
        return equipment.get(equipSlot);
    }

    public boolean hasEquipment(EquipSlot equipSlot) {
        return equipment.containsKey(equipSlot);
    }

    public void equip(SomEquip item) {
        if (item instanceof SomEquipment equipItem) {
            if (classes.getMainClass().getEquipAble().contains(equipItem.getEquipmentCategory())) {
                rawEquip(item);
                if (equipItem.getLevel() > getLevel()) {
                    sendSomText(SomText.create("§eレベル§aが足りないため§e装備性能§aが§c低下§aします §e[").add(equipItem.toSomText().add("§e]")));
                }
                for (SomRune rune : equipItem.getRune()) {
                    if (rune.getLevel() > getLevel()) {
                        sendSomText(SomText.create("§eレベル§aが足りないため§eルーン性能§aが§c低下§aします §e[").add(rune.toSomText().add("§e]")));
                    }
                }
            } else {
                sendMessage("§c装備条件§aを満たしていません §e[クラス制限]", SomSound.Nope);
            }
        } else rawEquip(item);
    }

    private void rawEquip(SomEquip item) {
        if (equipment.containsKey(item.getEquipSlot())) {
            itemInventory.add(equipment.get(item.getEquipSlot()), 1);
        }
        if (item.getEquipSlot() == EquipSlot.Amulet) clearEffectNonPotion();
        itemInventory.remove(item, 1);
        equipment.put(item.getEquipSlot(), item);
        updateStatus();
    }

    public void unEquip(EquipSlot equipSlot) {
        if (equipment.containsKey(equipSlot)) {
            itemInventory.add(equipment.get(equipSlot), 1);
        }
        if (equipSlot == EquipSlot.Amulet) clearEffectNonPotion();
        equipment.remove(equipSlot);
        updateStatus();
    }

    final List<String> ScoreKey = new ArrayList<>();
    private Scoreboard board;
    private Objective sidebarObject;
    private Team team;
    private TextDisplay display;

    public void initialize() {
        player.setGravity(true);
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebarObject = board.registerNewObjective("Sidebar", Criteria.DUMMY, decoSeparator("Sword of Magic 10"));
        sidebarObject.setDisplaySlot(DisplaySlot.SIDEBAR);
        team = board.registerNewTeam(player.getName());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        team.addPlayer(player);
        team.setCanSeeFriendlyInvisibles(true);
        player.setScoreboard(board);
        for (Player player : SomCore.getPlayers()) {
            team.addPlayer(player);
        }
        displayHolo();
        if (isBE()) {
            sidebarObject.getScore("§cBE版はサイドバー利用不可").setScore(0);
        }
    }

    public void displayHolo() {
        if (display == null || !display.isValid()) {
            SomTask.sync(() -> {
                display = (TextDisplay) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.TEXT_DISPLAY);
                display.setBackgroundColor(Color.fromARGB(0, 0,0, 0));
                display.setBillboard(Display.Billboard.VERTICAL);
                display.addScoreboardTag(Config.SomParticleAddress);
                display.setTransformation(new CustomTransformation().setOffset(0, 0.6, 0));
                display.setSeeThrough(true);
            });
        }
    }

    public TextDisplay getDisplayHolo() {
        return display;
    }


    private BukkitTask tickTask;
    private BukkitTask tick5Task;
    private BukkitTask secondTask;
    private BukkitTask secondSyncTask;
    private BukkitTask second30Task;

    private final int barLength = 30;

    public void updateTick() {
        if (tickTask != null) tickTask.cancel();
        if (tick5Task != null) tick5Task.cancel();
        if (secondTask != null) secondTask.cancel();
        if (secondSyncTask != null) secondSyncTask.cancel();
        if (second30Task != null) second30Task.cancel();
        tickTask = SomTask.timerPlayer(this, () -> {
            tickEffect(1);
            if (interaction > 0) interaction--;
            if (isPlayMode() && !isLoading()) {
                addHealth(getHealthRegen() * 0.05);
                addMana(getManaRegen() * 0.05);
                ClassType mainClass = classes.getMainClass();
                if (mainClass != null) {
                    String skillCastProgress;
                    if (skillManager.isCastable()) {
                        skillCastProgress = "§a《Skill Castable》";
                    } else if (skillManager.getSkillCastProgress() < 1) {
                        skillCastProgress = "§e《Casting " + scale(skillManager.getSkillCastProgress()*100) + "%》";
                    } else {
                        skillCastProgress = "§d《Active " + skillManager.getCastSkill().getDisplay() + "》";
                    }
                    String actionBar =
                            "§e《" + mainClass.getColorDisplay() + " §eLv" + classes.getLevel(mainClass) + "》" +
                                    "§c《Health: " + scale(getHealth()) + "/" + scale(getMaxHealth()) + "§c》" +
                                    skillCastProgress +
                                    "§b《Mana: " + scale(getMana()) + "/" + scale(getMaxMana()) + "》" +
                                    "§a《Exp: " + scale(classes.getExp(mainClass)) + "/" + scale(Classes.getReqExp(classes.getLevel(mainClass))) + "》";
                    if (isBE()) actionBar += " ".repeat(20) + "§e《所持メル: " + mel + "メル》";
                    player.sendActionBar(actionBar);
                    double hp = getHealthPercent();
                    String color;
                    if (hp > 0.5) color = "§a";
                    else if (hp > 0.25) color = "§e";
                    else  color = "§c";
                    int healthBar = (int) MinMax(getHealthPercent()*barLength, 0, barLength);
                    //int manaBar = (int) MinMax(getManaPercent()*barLength, 0, barLength);
                    String healthBarText = color + "|".repeat(healthBar) + "§7" + "|".repeat(barLength - healthBar);
                    //String manaBarText = "§b" + "|".repeat(healthBar) + "§7" + "|".repeat(barLength - manaBar);
                    String displayText = mainClass.getColor() + "[" + mainClass.getColorNick() + "]§r " + getDisplayName() + " §c" + scale(getHealth()) + "♥\n";
                    if (getProduceGame().isInGame()) {
                        displayText += "§e[" + getProduceGame().getGame().getDisplay() + "]";
                    } else if (isAFK()) {
                        displayText += "§8[AFK]";
                    } else {
                        displayText += healthBarText;
                    }
                    display.setText(displayText);
                }
            }
        }, 20, 1);
        tick5Task = SomTask.timerPlayer(this, () -> {
            setMovementSpeed(getLastLocation().distance(getLocation()));
            if (isPlayMode() && !isLoading()) {
                ClassType mainClass = classes.getMainClass();
                if (mainClass != null) {
                    setLastLocation(getLocation());
                    if (!isBE()) {
                        for (String entry : board.getEntries()) {
                            board.resetScores(entry);
                        }
                        ScoreKey.clear();
                        ScoreKey.add(decoLore("メル") + mel);

                        if (Auction.Auctioning) {
                            ScoreKey.addAll(Auction.TextLine);
                        }

                        if (!viewAmount.isEmpty()) {
                            ScoreKey.add(decoSeparator("所持アイテム数"));
                            viewAmount.forEach((id, tier) -> {
                                SomItem item = ItemDataLoader.getItemData(id);
                                item.setTier(tier);
                                StringBuilder text = new StringBuilder("§7・§r" + item.getColorDisplay() + "§ex");
                                itemInventory.getStack(item).ifPresentOrElse(stack -> text.append(stack.getAmount()), () -> text.append(0));
                                ScoreKey.add(text.toString());
                            });
                        }
                        if (gatheringMenu.isJoin()) {
                            ScoreKey.add(decoSeparator("ギャザリングレベル"));
                            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                                ScoreKey.add("§7・§e" + type.getDisplay() + "Lv" + gatheringMenu.getLevel(type) + " §a" + scale(gatheringMenu.getExpPercent(type)*100, 2) + "%");
                            }
                            ItemStack tool = player.getInventory().getItemInMainHand();
                            if (CustomItemStack.hasCustomData(tool, "ToolExp")) {
                                ScoreKey.add(decoSeparator("ツール性能"));
                                ScoreKey.add(decoLore("ツール精錬値") + CustomItemStack.getCustomDataInt(tool, "ToolExp"));
                            }
                        }
                        if (questMenu.hasTrackingQuest()) {
                            QuestPhase questPhase = questMenu.getTrackingQuest().getNowPhase();
                            ScoreKey.add(decoSeparator(questPhase.getDisplay()));
                            ScoreKey.addAll(questPhase.sidebarLine(this));
                        }
                        if (dungeonMenu.isInDungeon()) {
                            DungeonInstance dungeon = dungeonMenu.getDungeon();
                            ScoreKey.add(decoSeparator(dungeon.getMatchType().getDisplay() + "ダンジョン[" + dungeon.getDifficulty() + "]"));
                            ScoreKey.add(decoLore("経過時間") + (dungeon.getStartTime() == 0 ? "0" : scale((System.currentTimeMillis()-dungeon.getStartTime())/1000.0, 2)) + "秒");
                            ScoreKey.add(decoSeparator("ダンジョンメンバー (" + dungeon.getMember().size() + "/" + dungeon.getMatchType().getLimit() + ")"));
                            for (PlayerData player : dungeon.getMember()) {
                                ClassType playerClass = player.getClasses().getMainClass();
                                ScoreKey.add("§7・" + player.getDisplayName() + " §a" + scale(player.getHealthPercent()*100) + "% " + playerClass.getColorNick() + " §eLv" + player.getClasses().getLevel(playerClass));
                            }
                        }
                        if (isInDefensiveBattle()) {
                            DefensiveBattle defensiveBattle = getDefensiveBattle();
                            ScoreKey.add(decoSeparator(defensiveBattle.getMatchType().getDisplay() + "防衛戦"));
                            ScoreKey.add(decoLore("経過時間") + (defensiveBattle.getStartTime() == 0 ? "0" : scale((System.currentTimeMillis()-defensiveBattle.getStartTime())/1000.0, 2)) + "秒");
                            ScoreKey.add(decoLore("Wave" + defensiveBattle.getWave()) + (defensiveBattle.getWaveStart() == 0 ? "0" : scale((System.currentTimeMillis()-defensiveBattle.getWaveStart())/1000.0, 2)) + "秒 §e[" + defensiveBattle.difficulty() + "]");
                            ScoreKey.add(decoSeparator("防衛戦メンバー (" + defensiveBattle.getMember().size() + "/" + defensiveBattle.getMatchType().getLimit() + ")"));
                            for (PlayerData player : defensiveBattle.getMember()) {
                                ClassType playerClass = player.getClasses().getMainClass();
                                ScoreKey.add("§7・" + player.getDisplayName() + " §a" + scale(player.getHealthPercent()*100) + "% " + playerClass.getColorNick() + " §eLv" + player.getClasses().getLevel(playerClass));
                            }
                        }
                        if (!effect.isEmpty()) {
                            ScoreKey.add(decoSeparator("バフ・デバフ"));
                            for (SomEffect effect : effect.values()) {
                                ScoreKey.add("§7・" + effect.getColorDisplay() + "[" + effect.getStack() + "]§r §a" + (effect.getTime() == -1 ? "" : (scale(effect.getTime()) + "秒")));
                            }
                        }
                        int i = ScoreKey.size();
                        for (String scoreName : ScoreKey) {
                            Score sidebarScore = sidebarObject.getScore(scoreName);
                            sidebarScore.setScore(i);
                            i--;
                        }
                    }
                    player.setPlayerListName("§3[" + mapData.getSuffix() + "] " +  mainClass.getColor() + "[" + mainClass.getColorNick() + "]§r " + getDisplayName() + " §eLv" + getRawLevel());

                    if (evasionWait > 0) {
                        evasionWait -= 5;
                        if (evasionWait <= 0) {
                            inventoryViewer.updateBar(true);
                        }
                    }
                }
            }
        }, 20, 5);

        secondTask = SomTask.timerPlayer(this, () -> {
            if (afkLocation.distanceXZ(getLocation()) < 1.0) {
                afkTime++;
            } else {
                afkLocation = getLocation();
                afkTime = 0;
            }
            if (isAFK() && !produceGame.getTyping().isStart()) {
                sendTitle("§7You Are AFK", "§a" + afkTime, 0, 25, 0);
            }

            addPlaytime(1);
            displayHolo();
        }, 20, 20);

        secondSyncTask = SomTask.syncTimer(() -> {
            if (player.isOnline()) {
                ClassType mainClass = classes.getMainClass();
                player.setHealth(MinMax(getHealthPercent(), 0.01, 1) * 20);
                player.setFoodLevel((int) Math.ceil(MinMax(getMana()/getMaxMana(), 0, 1) * 20));
                player.setLevel(classes.getLevel(mainClass));
                player.setExp(MinMax(classes.getExpPercent(mainClass), 0.001f, 0.999f));
                player.addPassenger(display);
                for (Player player : SomCore.getPlayers()) {
                    team.addPlayer(player);
                }

                if (setting.isViewSelfNamePlate()) {
                    if (!player.canSee(getDisplayHolo())) {
                        player.showEntity(SomCore.plugin(), getDisplayHolo());
                    }
                } else {
                    if (player.canSee(getDisplayHolo())) {
                        player.hideEntity(SomCore.plugin(), getDisplayHolo());
                    }
                }

                if (isInCity()) {
                    for (PlayerData playerData : PlayerData.getPlayerList()) {
                        if (this != playerData) {
                            show(playerData);
                        }
                    }
                } else {
                    Collection<PlayerData> member = getMember();
                    for (PlayerData playerData : PlayerData.getPlayerList()) {
                        if (this != playerData) {
                            if (member.contains(playerData)) {
                                show(playerData);
                            } else {
                                hide(playerData);
                            }
                        }
                    }
                }
            } else {
                secondSyncTask.cancel();
                display.remove();
            }
        }, 20, 20);

        second30Task = SomTask.timerPlayer(this, () -> {
            if (!isInCity()) {
                if (hasEquipment(EquipSlot.AlchemyStone)) {
                    if (getEquipment(EquipSlot.AlchemyStone) instanceof SomAlchemyStone stone && stone.getExp() > 0) {
                        stone.addExp(-1);
                        if (stone.getExp() == 0) updateStatus();
                    } else {
                        sendMessage("§e錬金石§aの§e精錬値§aが切れています", SomSound.Nope);
                    }
                }
            }
        }, 20, 30*20);
    }

    public void show(PlayerData playerData) {
        if (!player.canSee(playerData.getPlayer())) {
            player.showPlayer(SomCore.plugin(), playerData.getPlayer());
        }
        if (!player.canSee(playerData.getDisplayHolo())) {
            player.showEntity(SomCore.plugin(), playerData.getDisplayHolo());
        }
        for (SomPet pet : playerData.getPetMenu().getSummon()) {
            LivingEntity petEntity = pet.getLivingEntity();
            if (!player.canSee(petEntity)) {
                player.showEntity(SomCore.plugin(), petEntity);
            }
        }
    }

    public void hide(PlayerData playerData) {
        if (player.canSee(playerData.getPlayer())) {
            player.hidePlayer(SomCore.plugin(), playerData.getPlayer());
        }
        if (player.canSee(playerData.getDisplayHolo())) {
            player.hideEntity(SomCore.plugin(), playerData.getDisplayHolo());
        }
        for (SomPet pet : playerData.getPetMenu().getSummon()) {
            LivingEntity petEntity = pet.getLivingEntity();
            if (player.canSee(petEntity)) {
                player.hideEntity(SomCore.plugin(), petEntity);
            }
        }
    }

    public String getDisplayColor() {
        if (isAFK()) return "§7";
        if (getSetting().isPvPMode()) return "§c";
        return "§r";
    }

    public void teleport(Location location) {
        teleport(location, new Vector());
    }

    public void teleport(Location location, Vector vector) {
        if (isLocationLock()) setLocationLock(location);
        if (isDeath()) {
            setPlayerSpawn(location);
            deathLocation = new CustomLocation(location);
        }
        SomTask.sync(() -> {
            List<Entity> returnPassenger = new ArrayList<>();
            for (Entity passenger : player.getPassengers()) {
                player.removePassenger(passenger);
                if (passenger.getScoreboardTags().contains(Config.SomParticleAddress)) {
                    returnPassenger.add(passenger);
                }
            }
            for (SomPet pet : petMenu.getSummon()) {
                pet.getLivingEntity().teleport(location);
                pet.setVelocity(vector);
            }
            player.teleport(location);
            player.setVelocity(vector);
            for (Entity passenger : returnPassenger) {
                player.addPassenger(passenger);
            }
        });
    }

    private boolean clickAbleInventory = true;

    public boolean isClickAbleInventory() {
        return clickAbleInventory;
    }

    public void setClickAbleInventory(boolean clickAbleInventory) {
        this.clickAbleInventory = clickAbleInventory;
    }

    private int interaction = 0;
    public boolean isInteraction() {
        if (isPlayMode()) {
            if (interaction <= 0) {
                interaction++;
                return true;
            }
            if (interaction > 100) {
                player.kickPlayer("Too Many Interaction");
            }
            return false;
        } else return true;
    }

    public void delete() {
        if (dungeonMenu.isInDungeon()) {
            dungeonMenu.getDungeon().leavePlayer(this);
        }
        playerData.remove(player.getUniqueId());
    }

    public void closeInventory() {
        SomTask.sync(player::closeInventory);
    }

    private long playtime = 0;
    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public void addPlaytime(long playtime) {
        this.playtime += playtime;
    }

    public boolean isTutorialClear() {
        return classes.isValidClass() && getQuestMenu().getClearQuest().contains("ようこそアライネへ");
    }

    private boolean saving = false;
    private boolean loading = true;
    public static final String Table = "playerData";

    public boolean isSaving() {
        return saving;
    }

    public boolean isLoading() {
        return loading;
    }

    public void saveAsync() {
        SomTask.run(this::saveSql);
    }

    public void saveSql() {
        saveSql(Table, true);
    }

    public void saveSql(String table, boolean check) {
        saving = true;
        try {
            if (check) {
                String uuid = getPlayer().getUniqueId().toString();
                if (SomSQL.existSql(table, "UUID", uuid) && SomSQL.getLong(table, "UUID", uuid, "PlayTime") > getPlaytime()) {
                    sendMessage("§cデータの整合性に問題が生じたため§bセーブを中断しました", SomSound.Nope);
                    return;
                }
            }
            save(table, player.getUniqueId().toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage("§eプレイヤーデータ§aの§bセーブ中§aに§cエラー§aが発生しました", SomSound.Nope);
        } finally {
            saving = false;
        }
    }

    private void save(String table, String uuid) {
        if (isTutorialClear()) {
            saving = true;
            long startTime = System.currentTimeMillis();

            SomSQL.setSql(table, "UUID", uuid, "PlayTime", getPlaytime());
            SomSQL.setSql(table, "UUID", uuid, "Username", player.getName());
            SomSQL.setSql(table, "UUID", uuid, "Mel", getMel());

            SomJson json = new SomJson();

            json.set("MainClass", getClasses().getMainClass().toString());
            classes.save();
//            for (ClassType classType : ClassType.values()) {
//                String path = "Classes." + classType;
//                json.set(path + ".Level", getClasses().getLevel(classType));
//                json.set(path + ".Exp", getClasses().getExp(classType));
//                for (int i = 1; i < getClasses().getSkillGroup(classType).size(); i++) {
//                    json.set(path + ".SkillGroup.Slot-" + i, getClasses().getSkillGroup(classType).get(i).getId());
//                }
//                for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
//                    SomSkill skill = palletMenu.getPallet(classType)[i];
//                    if (skill != null) json.set(path + ".Skill-" + i, skill.getId());
//                }
//                for (int i = 0; i < palletMenu.getItemPallet().length; i++) {
//                    SomItem item = palletMenu.getItemPallet()[i];
//                    if (item != null) json.set(path + ".Item-" + i, item.toJson());
//                }
//            }

            for (EquipSlot equipSlot : EquipSlot.values()) {
                if (getEquipment(equipSlot) != null) {
                    json.set("Item.Equipment." + equipSlot, getEquipment(equipSlot).toJson());
                }
            }
            for (SomTool.Type toolType : SomTool.Type.values()) {
                if (getGatheringMenu().hasTool(toolType)) {
                    json.set("Item.Tool." + toolType, getGatheringMenu().getTool(toolType).toJson());
                }
            }
            for (SomItemStack stack : getItemInventory().getInventory()) {
                json.addArray("Item.Inventory", stack.toJson());
            }

            for (SomItemStack stack : getItemStorage().getInventory()) {
                json.addArray("Item.Storage", stack.toJson());
            }

            getQuestMenu().getQuests().forEach((id, somQuest) -> {
                SomJson questJson = new SomJson();
                questJson.set("Id", id);
                questJson.set("PhaseIndex", somQuest.getPhaseIndex());
                int i = 0;
                for (QuestPhase phase : somQuest.getPhase()) {
                    questJson.set("Phase-" + i, phase.toJson());
                    i++;
                }
                json.addArray("OrderQuest", questJson);
            });
            getQuestMenu().getClearQuestTime().forEach((questId, time) -> json.addArray("ClearQuest", questId + "," + time.format(DateFormat)));

            json.set("Statistics", getStatistics().save());
            json.set("Setting", getSetting().save());
            json.set("Gathering", getGatheringMenu().save());

            SomSQL.setSql(table, "UUID", uuid, "Json", json.toString());
            long endTime = System.currentTimeMillis();
            sendMessage("§eプレイヤーデータ§aを§bセーブ§aしました §e(" + scaleDouble(json.toString().getBytes(StandardCharsets.UTF_8).length / 1024f) + "KiB/" + (endTime - startTime) + "ms)", SomSound.Tick);
            saving = false;
        }
    }

    public void loadAsync() {
        SomTask.run(this::loadSql);
    }

    public void loadSql() {
        loadSql(Table, player.getUniqueId().toString());
    }

    public void loadSql(String table, String uuid) {
        try {
            load(table, uuid);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage("§eプレイヤーデータ§aの§bロード中§aに§cエラー§aが発生しました", SomSound.Nope);
        }
    }

    private void load(String table, String uuid) {
        loading = true;
        long startTime = System.currentTimeMillis();
        float dataSize = 0;
        if (SomSQL.existSql(table, "UUID", uuid)) {
            setPlaytime(SomSQL.getLong(table, "UUID", uuid, "PlayTime"));
            SomJson json = new SomJson(SomSQL.getString(table, "UUID", uuid, "Json"));
            dataSize = json.toString().getBytes(StandardCharsets.UTF_8).length/1024f;
            setMel(SomSQL.getInt(table, "UUID", uuid, "Mel"));

            if (json.has("MainClass")) getClasses().setMainClass(ClassType.valueOf(json.getString("MainClass")));
            classes.load();
//            for (ClassType classType : ClassType.values()) {
//                SkillGroup first = getClasses().getSkillGroup(classType).get(0);
//                getClasses().getSkillGroup(classType).clear();
//                getClasses().getSkillGroup(classType).add(first);
//                String path = "Classes." + classType;
//                getClasses().setLevel(classType, json.getInt(path + ".Level", 1));
//                getClasses().setExp(classType, json.getInt(path + ".Exp", 0));
//                for (int i = 1; i < Classes.UnlockSkillGroupSlot.length; i++) {
//                    try {
//                        if (json.has(path + ".SkillGroup.Slot-" + i)) {
//                            SkillGroup group = SkillGroupLoader.getSkillGroup(json.getString(path + ".SkillGroup.Slot-" + i));
//                            getClasses().getSkillGroup(classType).add(group);
//                        }
//                    } catch (Exception ignore) {}
//                }
//                for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
//                    try {
//                        String key = path + ".Skill-" + i;
//                        if (json.has(key)) palletMenu.setPallet(classType, i, skillManager.getSkill(json.getString(key)));
//                    } catch (Exception ignore) {}
//                }
//                for (int i = 0; i < palletMenu.getItemPallet().length; i++) {
//                    try {
//                        String key = path + ".Item-" + i;
//                        if (json.has(key)) palletMenu.setItemPallet(i, (SomPotion) SomItem.fromJson(json.getSomJson(key)));
//                    } catch (Exception ignore) {}
//                }
//            }

            equipment.clear();
            if (json.has("Item.AlchemyStone")) equipment.put(EquipSlot.AlchemyStone, (SomEquip) SomItem.fromJson(json.getSomJson("Item.AlchemyStone")));
            if (json.has("Item.Wish")) equipment.put(EquipSlot.Amulet, (SomEquip) SomItem.fromJson(json.getSomJson("Item.Wish")));
            for (EquipSlot equipSlot : EquipSlot.values()) {
                if (json.has("Item.Equipment." + equipSlot)) {
                    try {
                        equipment.put(equipSlot, (SomEquip) SomItem.fromJson(json.getSomJson("Item.Equipment." + equipSlot)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log("§c" + e.getMessage());
                    }
                }
            }
            for (SomTool.Type toolType : SomTool.Type.values()) {
                if (json.has("Item.Tool." + toolType)) {
                    try {
                        getGatheringMenu().setTool(toolType, (SomTool) SomItem.fromJson(json.getSomJson("Item.Tool." + toolType)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log("§c" + e.getMessage());
                    }
                }
            }
            getItemInventory().clear();
            for (String data : json.getList("Item.Inventory")) {
                try {
                    getItemInventory().add(SomItemStack.fromJson(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log("§c" + e.getMessage());
                }
            }

            getItemStorage().clear();
            for (String data : json.getList("Item.Storage")) {
                try {
                    getItemStorage().add(SomItemStack.fromJson(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log("§c" + e.getMessage());
                }
            }

            getQuestMenu().getQuests().clear();
            for (String questData : json.getList("OrderQuest")) {
                try {
                    SomJson questJson = new SomJson(questData);
                    SomQuest somQuest = new SomQuest(QuestDataLoader.getQuestData(questJson.getString("Id")));
                    somQuest.setPhaseIndex(questJson.getInt("PhaseIndex", 0));
                    int i = 0;
                    for (QuestPhase questPhase : somQuest.getQuestData().getPhase()) {
                        try {
                            QuestPhase newPhase = questPhase.fromJson(questJson.getSomJson("Phase-" + i));
                            somQuest.setPhase(i, newPhase);
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log("§c" + e.getMessage());
                        }
                    }
                    getQuestMenu().addQuest(somQuest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getQuestMenu().getClearQuest().clear();
            for (String clearQuest : json.getStringList("ClearQuest")) {
                String[] split = clearQuest.split(",");
                try {
                    getQuestMenu().addClearQuest(split[0], LocalDateTime.parse(split[1], DateFormat));
                } catch (Exception e) {
                    getQuestMenu().addClearQuest(split[0], LocalDateTime.now().minusDays(1));
                }
            }

            getStatistics().load(json.getSomJson("Statistics"));
            getSetting().load(json.getSomJson("Setting"));
            getGatheringMenu().load(json.getSomJson("Gathering"));
        }

        long endTime = System.currentTimeMillis();
        sendMessage("§eプレイヤーデータ§aを§bロード§aしました §e(" + scaleDouble(dataSize) + "KiB/" + (endTime - startTime) + "ms)", SomSound.Tick);
        loading = false;
        if (isPlayMode()) {
            updateStatus();
            updateTick();
            inventoryViewer.nextUpdate();
            heal();
            SomTask.sync(() -> {
                player.setGameMode(GameMode.ADVENTURE);
                if (isTutorialClear()) {
                    if (SpawnLocation.distance(player.getLocation()) > 256) teleport(SomCore.SpawnLocation);
                } else {
                    teleport(Tutorial.SpawnLocation);
                }
            });
        } else {
            setting.applyNightVision(true);
        }
    }
}
