package SwordofMagic10.Player;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.QuestDataLoader;
import SwordofMagic10.DataBase.SkillGroupLoader;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Dungeon.DungeonMenu;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.*;
import SwordofMagic10.Player.Help.HelpMenu;
import SwordofMagic10.Player.Menu.*;
import SwordofMagic10.Player.Quest.QuestMenu;
import SwordofMagic10.Player.Quest.QuestPhase;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.Shop.SellManager;
import SwordofMagic10.Player.Shop.ShopManager;
import SwordofMagic10.Player.Skill.SkillGroup;
import SwordofMagic10.Player.Skill.SkillManager;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;
import static SwordofMagic10.SomCore.SpawnLocation;

public class PlayerData implements SwordofMagic10.Entity.SomEntity, SomGuild.Member {

    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();

    public static PlayerData get(Player player) {
        synchronized (playerData) {
            if (!playerData.containsKey(player.getUniqueId())) {
                playerData.put(player.getUniqueId(), new PlayerData(player));
            }
            return playerData.get(player.getUniqueId());
        }
    }

    public static Collection<PlayerData> getPlayerList() {
        synchronized (playerData) {
            return playerData.values();
        }
    }

    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (PlayerData playerData : playerData.values()) {
            complete.add(playerData.getPlayer().getName());
        }
        return complete;
    }

    public static String getDisplayName(UUID uuid) {
        return SomSQL.getString("PlayerData", "UUID", uuid.toString(), "DisplayName");
    }

    private final Player player;
    private final List<GUIManager> guiManagerList = new ArrayList<>();
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private final HashMap<DamageEffect, Double> damageEffect = new HashMap<>();
    private final HashMap<String, SomEffect> effect = new HashMap<>();
    private final HashMap<EquipSlot, SomEquipment> equipment = new HashMap<>();
    private SomParty party;
    private int levelSync = Classes.MaxLevel;
    private int tierSync = DungeonDifficulty.values().length;
    private int mel = 10000;
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
    private final QuestMenu questMenu;
    private final HelpMenu helpMenu;
    private final Setting setting;
    private final EquipmentMenu equipmentMenu;
    private final Mining mining;
    private final Lumber lumber;
    private final Collect collect;
    private final Produce produce;
    private final GatheringMenu gatheringMenu;
    private final Statistics statistics = new Statistics();

    private int afkTime = 0;
    private boolean locationLock = false;
    private Location playerSpawn;
    private Location lastLocation;
    private double movementSpeed = 0;

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
        helpMenu = new HelpMenu(this);
        questMenu = new QuestMenu(this);
        equipmentMenu = new EquipmentMenu(this);
        gatheringMenu = new GatheringMenu(this);
        mining = new Mining(this);
        lumber = new Lumber(this);
        collect = new Collect(this);
        produce = new Produce(this);

        setting = new Setting(this);
        lastLocation = new CustomLocation(player.getLocation());
        playerSpawn = SpawnLocation;
        initializeScoreBoard();
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

    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    public QuestMenu getQuestMenu() {
        return questMenu;
    }

    public Setting getSetting() {
        return setting;
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

    public Produce getProduce() {
        return produce;
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

    public int getLevelSync() {
        return levelSync;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setLevelSync(int levelSync) {
        this.levelSync = levelSync;
        if (classes.getLevel(classes.getMainClass()) > levelSync) {
            sendMessage("§eレベルシンク§aにより§eステータス§aが§c低下§aします");
        }
    }

    public void resetLevelSync() {
        if (classes.getLevel(classes.getMainClass()) > levelSync) {
            sendMessage("§eレベルシンク§aが§b解除§aされました");
        }
        levelSync = Classes.MaxLevel;
    }

    public int getTierSync() {
        return tierSync;
    }

    public void setTierSync(int tierSync) {
        this.tierSync = tierSync;
        sendMessage("§eティアシンク§aにより§eステータス§aが§c低下§aします");
    }

    public void resetTierSync() {
        sendMessage("§eティアシンク§aが§b解除§aされました");
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

    public void setPlayerSpawn(Location playerSpawn) {
        this.playerSpawn = playerSpawn;
    }

    public Location getPlayerSpawn() {
        return playerSpawn;
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

    public String getUUIDAsString() {
        return getUUID().toString();
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public void sendSomText(SomText text) {
        sendSomText(text, null);
    }

    public void sendSomText(SomText text, @Nullable SomSound sound) {
        player.spigot().sendMessage(text.toComponent());
        if (sound != null) sound.play(this);
    }

    public void sendSomText(List<SomText> texts, @Nullable SomSound sound) {
        for (SomText text : texts) {
            player.spigot().sendMessage(text.toComponent());
        }
        if (sound != null) sound.play(this);
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

    public void sendMessageNonMel() {
        sendMessage("§eメル§aが足りません", SomSound.Nope);
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
        }
        return list;
    }



    public boolean isLocationLock() {
        return locationLock;
    }

    public void setLocationLock(boolean locationLock) {
        player.setAllowFlight(locationLock);
        player.setFlying(locationLock);
        player.setFlySpeed(locationLock ? 0.0f : 0.2f);
        this.locationLock = locationLock;
    }

    private int evasionWait = 0;
    public void evasion() {
        if (isEvasionAble() && !isLocationLock()) {
            Vector vector;
            if (!player.isInWater()) {
                vector = getDirection().setY(0);
                if (player.isSneaking()) vector.multiply(-1);
                vector.normalize().setY(0.5);
            } else {
                vector = getDirection();
            }
            vector.multiply(getStatus(StatusType.Movement)/100f);
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

    public void addEquipmentExp(int addExp, int level) {
        for (EquipSlot equipSlot : EquipSlot.values()) {
            SomEquipment equipment = getEquipment(equipSlot);
            if (equipment != null && level >= equipment.getLevel()) {
                equipment.addExp(addExp);
            }
        }
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

    private boolean isDeath = false;
    private int respawnWait;
    private BukkitTask respawnTask;

    public void setRespawnWait(int respawnWait) {
        this.respawnWait = respawnWait;
    }

    public Collection<SomEntity> getDeathAllies() {
        List<SomEntity> list = new ArrayList<>();
        for (SomEntity ally : getAllies()) {
            if (ally.isDeath()) {
                list.add(ally);
            }
        }
        return list;
    }

    @Override
    public void death() {
        death(null);
    }
    public synchronized void death(String message) {
        if (message != null) sendMessage(message);
        isDeath = true;
        setLocationLock(true);
        SomTask.sync(() -> {
            Entity deathEntity = player.getWorld().spawnEntity(getLocation(), EntityType.INTERACTION);
            PlayerDisguise disguise = new PlayerDisguise(player);
            PlayerWatcher watcher = disguise.getWatcher();
            watcher.setSleeping(true);
            disguise.setEntity(deathEntity);
            disguise.setWatcher(watcher);
            disguise.startDisguise();
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§4You Are Dead", "§aRespawn Wait...", 20, 100, 0);
            respawnWait = 10;
            respawnTask = SomTask.timer(() -> {
                player.sendTitle("§4You Are Dead", "§aRespawn " + respawnWait + " sec", 0, 30, 10);
                respawnWait--;
                SomTask.sync(() -> {
                    boolean respawn = hasEffect("Resurrection");
                    if (respawnWait < 0) {
                        teleport(getPlayerSpawn());
                        if (getDungeonMenu().isInDungeon()) {
                            getDungeonMenu().getDungeon().addDeathCount(1);
                        }
                        respawn = true;
                    }
                    if (respawn) {
                        removeEffect("Resurrection");
                        player.setGameMode(GameMode.ADVENTURE);
                        respawnTask.cancel();
                        isDeath = false;
                        heal();
                        deathEntity.remove();
                        setLocationLock(false);
                    }
                });
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

    public boolean isAFK() {
        return afkTime >= 300;
    }

    public boolean isPlayMode() {
        return setting.isPlayMode();
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

    public HashMap<EquipSlot, SomEquipment> getEquipment() {
        return equipment;
    }

    public SomEquipment getEquipment(EquipSlot equipSlot) {
        return equipment.get(equipSlot);
    }

    public void equip(SomEquipment equip) {
        if (classes.getMainClass().getEquipAble().contains(equip.getEquipmentCategory())) {
            EquipSlot equipSlot = equip.getEquipmentCategory().getEquipSlot();
            if (equipment.containsKey(equipSlot)) {
                itemInventory.add(equipment.get(equipSlot), 1);
            }
            itemInventory.remove(equip, 1);
            equipment.put(equipSlot, equip);
            if (equip.getLevel() > getLevel()) {
                sendSomText(SomText.create("§eレベル§aが足りないため§e装備性能§aが§c低下§aします §e[").addText(equip.toSomText().addText("§e]")));
            }
            for (SomRune rune : equip.getRune()) {
                if (rune.getLevel() > getLevel()) {
                    sendSomText(SomText.create("§eレベル§aが足りないため§eルーン性能§aが§c低下§aします §e[").addText(rune.toSomText().addText("§e]")));
                }
            }
            updateStatus();
        } else {
            sendMessage("§c装備条件§aを満たしていません §e[クラス制限]", SomSound.Nope);
        }
    }

    public void unEquip(EquipSlot equipSlot) {
        if (equipment.containsKey(equipSlot)) {
            itemInventory.add(equipment.get(equipSlot), 1);
        }
        equipment.remove(equipSlot);
        updateStatus();
    }

    final List<String> ScoreKey = new ArrayList<>();
    private Scoreboard board;
    private Objective sidebarObject;
    private Team team;

    public void initializeScoreBoard() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebarObject = board.registerNewObjective("Sidebar", Criteria.DUMMY, decoSeparator("Sword of Magic 10"));
        sidebarObject.setDisplaySlot(DisplaySlot.SIDEBAR);
        team = board.registerNewTeam(player.getName());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        team.addPlayer(player);
        team.setCanSeeFriendlyInvisibles(true);
        player.setScoreboard(board);
    }

    private BukkitTask tickTask;
    private BukkitTask tick5Task;
    private BukkitTask secondTask;
    private BukkitTask secondSyncTask;
    public void updateTick() {
        tickTask = SomTask.timer(() -> {
            if (player.isOnline()) {
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
                        player.sendActionBar(
                                "§e《" + mainClass.getColorDisplay() + " §eLv" + classes.getLevel(mainClass) + "》" +
                                "§c《Health: " + scale(getHealth()) + "/" + scale(getMaxHealth()) + "§c》" +
                                skillCastProgress +
                                "§b《Mana: " + scale(getMana()) + "/" + scale(getMaxMana()) + "》" +
                                "§a《Exp: " + classes.getExp(mainClass) + "/" + Classes.getReqExp(classes.getLevel(mainClass)) + "》"
                                //"§e《Movement: " + scale(movementSpeed*4) + "ms/s》"
                        );
                    }
                }
            } else {
                tickTask.cancel();
            }
        }, 20, 1);
        tick5Task = SomTask.timer(() -> {
            if (player.isOnline()) {
                setMovementSpeed(getLastLocation().distance(getLocation()));
                if (isPlayMode() && !isLoading()) {
                    ClassType mainClass = classes.getMainClass();
                    if (mainClass != null) {
                        setLastLocation(getLocation());
                        for (String entry : board.getEntries()) {
                            board.resetScores(entry);
                        }
                        ScoreKey.clear();
                        ScoreKey.add(decoLore("メル") + mel);
                        if (dungeonMenu.isInDungeon()) {
                            Collection<PlayerData> member = new ArrayList<>(dungeonMenu.getDungeon().getMember());
                            ScoreKey.add(decoSeparator("ダンジョン[" + dungeonMenu.getDungeon().getDifficulty() + "] (" + member.size() + "/5)"));
                            for (PlayerData player : member) {
                                ClassType playerClass = player.getClasses().getMainClass();
                                ScoreKey.add("§7・" + player.getDisplayName() + " §a" + scale(player.getHealthPercent()*100) + "% " + playerClass.getColorNick() + " §eLv" + player.getClasses().getLevel(playerClass));
                            }
                        }

                        synchronized (effect) {
                            effect.values().removeIf(effect -> effect.getTime() <= 0);
                            if (effect.size() > 0) {
                                ScoreKey.add(decoSeparator("バフ・デバフ"));
                                for (SomEffect effect : effect.values()) {
                                    ScoreKey.add("§7・" + (effect.isBuff() ? "§e" : "§c") + effect.getDisplay() + "[" + effect.getStack() + "]§r §a" + scale(effect.getTime()) + "秒");
                                }

                            }
                        }

                        int i = 15;
                        for (String scoreName : ScoreKey) {
                            Score sidebarScore = sidebarObject.getScore(scoreName);
                            sidebarScore.setScore(i);
                            i--;
                            if (i < 1) break;
                        }
                        player.setPlayerListName(mainClass.getColor() + "[" + mainClass.getColorNick() + "]§r " + getDisplayName() + " §eLv" + getLevel());
                        for (PlayerData playerData : PlayerData.getPlayerList()) {
                            ClassType playerMainClass = playerData.getClasses().getMainClass();
                            if (playerMainClass != null) {
                                Player player = playerData.getPlayer();
                                Team team = board.getPlayerTeam(player);
                                if (team == null) {
                                    team = board.registerNewTeam(player.getName());
                                    team.addPlayer(player);
                                }
                                team.setPrefix(playerMainClass.getColor() + "[" + playerMainClass.getColorNick() + "]§r ");
                                team.setSuffix(" §c" + scale(playerData.getHealth()) + "♥");
                            }
                        }
                    }
                }
                if (evasionWait > 0) {
                    evasionWait -= 5;
                    if (evasionWait <= 0) {
                        inventoryViewer.updateBar(true);
                    }
                }
            } else {
                tick5Task.cancel();
            }

        }, 20, 5);

        secondTask = SomTask.timer(() -> {
            if (player.isOnline()) {
                if (movementSpeed == 0) {
                    afkTime++;
                } else {
                    afkTime = 0;
                }
                if (isAFK()) {
                    sendTitle("§7You Are AFK", "§a" + afkTime, 0, 25, 0);
                }
                addPlaytime(1);
                tickEffect();
            } else {
                secondTask.cancel();
            }
        }, 20, 20);

        secondSyncTask = SomTask.syncTimer(() -> {
            if (player.isOnline()) {
                ClassType mainClass = classes.getMainClass();
                player.setHealth(MinMax(getHealthPercent(), 0.01, 1) * 20);
                player.setFoodLevel((int) Math.ceil(MinMax(getMana()/getMaxMana(), 0, 1) * 20));
                player.setLevel(classes.getLevel(mainClass));
                player.setExp(MinMax(classes.getExpPercent(mainClass), 0.001f, 0.999f));
            } else {
                secondSyncTask.cancel();
            }
        }, 20, 20);
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
        SomTask.sync(() -> {
            for (Entity passenger : player.getPassengers()) {
                player.removePassenger(passenger);
            }
            player.teleport(location);
            player.setVelocity(vector);
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

    private boolean saving = false;
    private boolean loading = true;
    private String dataBase = "PlayerData";

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
        saveSql(dataBase, true);
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
        if (getClasses().getMainClass() != null && getQuestMenu().getClearQuest().contains("ようこそアライネへ")) {
            saving = true;
            SomSQL.setSql(table, "UUID", uuid, "PlayTime", getPlaytime());
            SomSQL.setSql(table, "UUID", uuid, "DisplayName", player.getName());
            long startTime = System.currentTimeMillis();
            SomJson json = new SomJson();
            Location saveLocation = player.getLocation();
            json.set("Location.world", saveLocation.getWorld().getName());
            json.set("Location.x", saveLocation.x());
            json.set("Location.y", saveLocation.y());
            json.set("Location.z", saveLocation.z());
            json.set("Mel", getMel());

            json.set("MainClass", getClasses().getMainClass().toString());
            for (ClassType classType : ClassType.values()) {
                String path = "Classes." + classType;
                json.set(path + ".Level", getClasses().getLevel(classType));
                json.set(path + ".Exp", getClasses().getExp(classType));
                for (int i = 1; i < getClasses().getSkillGroup(classType).size(); i++) {
                    json.set(path + ".SkillGroup.Slot-" + i, getClasses().getSkillGroup(classType).get(i).getId());
                }
                for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
                    SomSkill skill = palletMenu.getPallet(classType)[i];
                    if (skill != null) json.set(path + ".Skill-" + i, skill.getId());
                }
            }

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
                for (QuestPhase phase : somQuest.getQuestData().getPhase()) {
                    questJson.set("Phase-" + i, phase.toJson());
                    i++;
                }
                json.addArray("OrderQuest", questJson);
            });
            for (String clearQuest : getQuestMenu().getClearQuest()) {
                json.addArray("ClearQuest", clearQuest);
            }

            json.set("Statistics", getStatistics().save());
            json.set("Setting", getSetting().save());
            json.set("Gathering", getGatheringMenu().save());

            SomSQL.setSql(table, "UUID", uuid, "Json", json.toString());
            SomSQL.setSql(table, "UUID", uuid, "PlayTime", getPlaytime());
            long endTime = System.currentTimeMillis();
            sendMessage("§eプレイヤーデータ§aを§bセーブ§aしました §e(" + scaleDouble(json.toString().getBytes(StandardCharsets.UTF_8).length / 1024f) + "KiB/" + (endTime - startTime) + "ms)", SomSound.Tick);
            saving = false;
        }
    }

    public void loadAsync() {
        SomTask.run(this::loadSql);
    }

    public void loadSql() {
        loadSql(dataBase, player.getUniqueId().toString());
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
            setMel(json.getInt("Mel", mel));

            if (json.has("MainClass")) getClasses().setMainClass(ClassType.valueOf(json.getString("MainClass")));
            for (ClassType classType : ClassType.values()) {
                SkillGroup first = getClasses().getSkillGroup(classType).get(0);
                getClasses().getSkillGroup(classType).clear();
                getClasses().getSkillGroup(classType).add(first);
                String path = "Classes." + classType;
                getClasses().setLevel(classType, json.getInt(path + ".Level", 1));
                getClasses().setExp(classType, json.getInt(path + ".Exp", 0));
                for (int i = 1; i < Classes.UnlockSkillGroupSlot.length; i++) {
                    try {
                        if (json.has(path + ".SkillGroup.Slot-" + i)) {
                            SkillGroup group = SkillGroupLoader.getSkillGroup(json.getString(path + ".SkillGroup.Slot-" + i));
                            getClasses().getSkillGroup(classType).add(group);
                        }
                    } catch (Exception ignore) {}
                }
                for (int i = 0; i < palletMenu.getPallet(classType).length; i++) {
                    try {
                        String key = path + ".Skill-" + i;
                        if (json.has(key)) palletMenu.setPallet(classType, i, skillManager.getSkill(json.getString(key)));
                    } catch (Exception ignore) {}
                }
            }

            equipment.clear();
            for (EquipSlot equipSlot : EquipSlot.values()) {
                if (json.has("Item.Equipment." + equipSlot)) {
                    try {
                        equipment.put(equipSlot, (SomEquipment) SomItem.fromJson(json.getSomJson("Item.Equipment." + equipSlot)));
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
            }
            getQuestMenu().getClearQuest().clear();
            for (String clearQuest : json.getStringList("ClearQuest")) {
                getQuestMenu().addClearQuest(clearQuest);
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
                if (getQuestMenu().getClearQuest().contains("ようこそアライネへ")) {
                    if (SpawnLocation.distance(player.getLocation()) > 256) teleport(SomCore.SpawnLocation);
                } else {
                    teleport(Tutorial.SpawnLocation);
                }
            });
        }
    }
}
