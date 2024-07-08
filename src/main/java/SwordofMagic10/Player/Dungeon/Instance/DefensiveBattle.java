package SwordofMagic10.Player.Dungeon.Instance;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.DataBase.MobDataLoader;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class DefensiveBattle {

    private static final CustomLocation SpawnLocation = new CustomLocation(SomCore.World, -999.5, -7, 35.5);

    private static final CustomLocation[][] Pathfinder = new CustomLocation[3][7];
    private static final List<DefensiveBattle> instance = new ArrayList<>();
    private static final String Prefix = "§c[防衛戦]§r";
    public static final List<MobData> mobList = new ArrayList<>();
    public static final Set<String> mobIDList = new HashSet<>() {{
       add("カメナ");
    }};

    public static void initialize() {
        for (String id : mobIDList) {
            mobList.add(MobDataLoader.getMobData(id));
        }

        Pathfinder[0][0] = new CustomLocation(SomCore.World, -1098, -37, 29);
        Pathfinder[0][1] = new CustomLocation(SomCore.World, -1060, -32, 12);
        Pathfinder[0][2] = new CustomLocation(SomCore.World, -1060, -26, 47);
        Pathfinder[0][3] = new CustomLocation(SomCore.World, -1050, -24, 47);
        Pathfinder[0][4] = new CustomLocation(SomCore.World, -1050, -19, 13);
        Pathfinder[0][5] = new CustomLocation(SomCore.World, -1042, -17, 13);
        Pathfinder[0][6] = new CustomLocation(SomCore.World, -1034, -8, 45);

        Pathfinder[1][0] = new CustomLocation(SomCore.World, -1006, -37, -64);
        Pathfinder[1][1] = new CustomLocation(SomCore.World, -1026, -35, -29);
        Pathfinder[1][2] = new CustomLocation(SomCore.World, -984, -23, -29);
        Pathfinder[1][3] = new CustomLocation(SomCore.World, -986, -21, -17);
        Pathfinder[1][4] = new CustomLocation(SomCore.World, -1013, -17, -15);
        Pathfinder[1][5] = new CustomLocation(SomCore.World, -1014, -14, -4);
        Pathfinder[1][6] = new CustomLocation(SomCore.World, -977, -8, -5);

        Pathfinder[2][0] = new CustomLocation(SomCore.World, -897, -37, 29);
        Pathfinder[2][1] = new CustomLocation(SomCore.World, -939, -32, 13);
        Pathfinder[2][2] = new CustomLocation(SomCore.World, -939, -26, 46);
        Pathfinder[2][3] = new CustomLocation(SomCore.World, -948, -24, 43);
        Pathfinder[2][4] = new CustomLocation(SomCore.World, -950, -18, 11);
        Pathfinder[2][5] = new CustomLocation(SomCore.World, -965, -15, 11);
        Pathfinder[2][6] = new CustomLocation(SomCore.World, -965, -8, 45);
    }

    public static List<DefensiveBattle> getInstanceList() {
        return instance;
    }

    public static MobData randomMobData() {
        return mobList.get(randomInt(0, mobIDList.size()));
    }

    private final MatchType matchType;
    private int wave = 1;
    private long waveStart = 0;
    private final Set<PlayerData> member = new CopyOnWriteArraySet<>();
    private final ConcurrentHashMap<Integer, Set<EnemyData>> enemies = new ConcurrentHashMap<>();
    private boolean start = false;
    private boolean end = false;
    private int CoreHealth = 50;
    private long startTime = 0;
    private final BossBar bossBar = Bukkit.createBossBar("§eマッチング待機中...", BarColor.YELLOW, BarStyle.SEGMENTED_10);

    private final MapData mapData;
    private BukkitTask task;
    public DefensiveBattle(MatchType matchType) {
        instance.add(this);
        this.matchType = matchType;
        for (int i = 0; i < Pathfinder.length; i++) {
            enemies.put(i, new HashSet<>());
        }
        mapData = MapDataLoader.getMapData("DefensiveBattle");

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (PlayerData member : member) {
                    if (!member.isOnline()) {
                        resetPlayer(member);
                    }
                }
                if (member.isEmpty()) {
                    this.cancel();
                    delete();
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 50, 50);
    }

    public void delete() {
        if (task != null) task.cancel();
        end = true;
        enemies.values().forEach(enemies -> enemies.forEach(EnemyData::delete));
        for (PlayerData playerData : member) {
            resetPlayer(playerData);
        }
        instance.remove(this);
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public Set<PlayerData> getMember() {
        return member;
    }

    public Set<EnemyData> getEnemies() {
        Set<EnemyData> list = new HashSet<>();
        for (Set<EnemyData> enemies : enemies.values()) {
            list.addAll(enemies);
        }
        return list;
    }

    public void joinPlayer(PlayerData playerData) {
        playerData.setDefensiveBattle(this);
        member.add(playerData);
        bossBar.addPlayer(playerData.getPlayer());
        sendMessage(playerData.getDisplayName() + "§aが§b参加§aしました", SomSound.Tick);
        if (start) {
            joinTeleport(playerData);
            SomTask.sync(() -> {
                for (EnemyData enemy : getEnemies()) {
                    playerData.getPlayer().showEntity(SomCore.plugin(), enemy.getLivingEntity());
                }
            });
        } else if (member.size() >= 3 || member.size() == matchType.getLimit()) {
            start();
        }
    }

    public void resetPlayer(PlayerData playerData) {
        sendMessage(playerData.getDisplayName() + "§aが" + (playerData.isAFK() ? "§7AFK§aにより§4強制退場" : "§c退場") + "§aしました", SomSound.Tick);
        playerData.teleport(DungeonInstance.RaidEnter);
        MapDataLoader.getMapData("Alaine").enter(playerData);
        playerData.setDefensiveBattle(null);
        member.remove(playerData);
        bossBar.removePlayer(playerData.getPlayer());
    }

    public int getWave() {
        return wave;
    }

    public long getWaveStart() {
        return waveStart;
    }

    public boolean isEnd() {
        return end;
    }

    public long getStartTime() {
        return startTime;
    }

    public static final SomEffect DefensiveEnemy = new SomEffect.Toggle("DefensiveEnemy", "防衛戦エネミー", true);

    public void joinTeleport(PlayerData playerData) {
        playerData.teleport(SpawnLocation);
        mapData.enter(playerData);;
    }

    private void start() {
        if (!start) {
            start = true;
            sendTitleMessage("§eMember Ready","§eメンバーがそろいました", "§e開始条件§aを満たしたため§c防衛戦§aを§b開始§aします", SomSound.Level, 60);
            SomTask.delay(() -> {
                for (PlayerData playerData : member) {
                    joinTeleport(playerData);
                }
                sendTitle("","§eまもなく準備フェーズです", 10, 30, 10, SomSound.Tick);
                startTime = System.currentTimeMillis();
                if (task != null) task.cancel();
                task = new BukkitRunnable() {
                    int readyTime = 15;
                    int count = 0;
                    DungeonDifficulty difficulty = difficulty();
                    boolean readyPhase = true;
                    boolean coreAttack = false;
                    boolean bossWave = false;
                    boolean bossSpawn = false;
                    @Override
                    public void run() {
                        if (readyPhase) {
                            int skipMember = 0;
                            for (PlayerData member : member) if (member.getLivingEntity().isSneaking()) skipMember++;
                            if (member.size() * 2f / 3f <= skipMember) readyTime = 0;
                            bossBar.setTitle("§e準備フェーズ " + readyTime + "秒");
                            for (PlayerData playerData : member) {
                                playerData.sendTitle("§e準備フェーズ " + readyTime + "秒", "§e3分の2がスニークをしているとスキップされます ( "+skipMember+" / "+(int)Math.ceil(member.size()*2f/3f)+" )", 0, 21, 0);
                                if (readyTime <= 5) SomSound.Tick.play(playerData);
                            }
                            readyTime--;
                            if (readyTime < 0) {
                                count = count();
                                difficulty = difficulty();
                                bossBar.setColor(BarColor.RED);
                                sendTitleMessage("§4§nWave " + wave, "§c戦闘フェーズ", "§eWave" + wave + "§aを§b開始§aします", SomSound.BossSpawn);
                                bossBar.setColor(BarColor.RED);
                                bossBar.setTitle("§c戦闘フェーズ");
                                readyPhase = false;
                                waveStart = System.currentTimeMillis();
                            }
                        } else {
                            coreAttack = false;
                            enemies.forEach((index, enemies) -> {
                                enemies.removeIf(EnemyData::isInvalid);
                                for (EnemyData enemy : enemies) {
                                    if (enemy.getTargetOverride() == SpawnLocation) {
                                        if (enemy.getLocation().distance(SpawnLocation) < 5) {
                                            CoreHealth--;
                                            coreAttack = true;
                                        }
                                    } else if (enemy.getTargetOverride().distance(enemy.getLocation()) < 1.5) {
                                        enemy.setIndex(enemy.getIndex()+1);
                                        if (enemy.getIndex() < Pathfinder[index].length) {
                                            enemy.setTargetOverride(Pathfinder[index][enemy.getIndex()].clone().addXZ(randomDouble(-1.5, 1.5), randomDouble(-1.5, 1.5)));
                                        } else {
                                            enemy.setTargetOverride(SpawnLocation);
                                        }
                                    } else {
                                        for (int i = 0; i < Pathfinder[index].length; i++) {
                                            if (enemy.getLocation().getY() < Pathfinder[index][i].getY()) {
                                                enemy.setIndex(i);
                                                enemy.setTargetOverride(Pathfinder[index][i]);
                                                break;
                                            }
                                        }
                                    }
                                }
                            });
                            if (coreAttack) {
                                sendTitle("", "§dディメンションコア§aが§c攻撃§aされています", 0, 20, 5, SomSound.Nope);
                            }
                            int enemyCount = count;
                            for (Set<EnemyData> enemies : enemies.values()) {
                                enemyCount += enemies.size();
                            }
                            bossBar.setTitle("§cエネミー数 " + enemyCount + " §8(" + count + ")");
                            if (count > 0) {
                                SomTask.sync(() -> {
                                    for (int i = 0; i < 32; i++) {
                                        if (count > 0) {
                                            int index = randomInt(0, Pathfinder.length);
                                            EnemyData enemyData = EnemyData.spawn(randomMobData(), level(), difficulty, Pathfinder[index][0], member, mapData).setHealthMultiply(matchType.getMultiply());
                                            enemyData.setTargetOverride(Pathfinder[index][1]);
                                            enemyData.addEffect(DefensiveEnemy, enemyData);
                                            enemyData.updateStatus();
                                            enemies.get(index).add(enemyData);
                                            count--;
                                        } else break;
                                    }
                                    if (bossWave && bossSpawn) {
                                        bossSpawn();
                                        bossSpawn = false;
                                    }
                                });
                            } else {
                                if (enemyCount <= 5) {
                                    enemies.values().forEach(enemies -> enemies.forEach(enemy -> enemy.getLivingEntity().setGlowing(true)));
                                    if (enemyCount == 0) {
                                        sendTitleMessage("§b§nWave " + wave + " Clear!", "§eフェーズクリア", "§eWave" + wave + "§aを§bクリア§aしました", SomSound.Level);
                                        int level = level();
                                        double exp = Classes.getExp(level)/(1+level*0.2);
                                        SomItem enhanceStone = ItemDataLoader.getItemData("精錬石").setTier(difficulty.ordinal()+1);
                                        SomItem tierStone = ItemDataLoader.getItemData("昇級石").setTier(difficulty.ordinal()+1);
                                        SomItem qualityStone = ItemDataLoader.getItemData("品質変更石").setTier(difficulty.ordinal()+1);
                                        for (PlayerData playerData : member) {
                                            if (!playerData.isAFK()) {
                                                playerData.getClasses().addExp(exp * difficulty.getMultiply());
                                                //精練石　100%
                                                int amountS = 5;
                                                if (bossWave) amountS *= 2;
                                                playerData.getItemInventory().add(enhanceStone, amountS, 1);
                                                if (wave >= 12) {
                                                    double getPercent = 0;
                                                    switch (matchType){
                                                        case World, Party -> getPercent =  0.0175;
                                                        case Duo -> getPercent =  0.0225;
                                                        case Solo -> getPercent =  0.035;
                                                    }
                                                    if (bossWave) getPercent *= 2;
                                                    if (randomDouble(0, 1) < getPercent) {
                                                        //アナウンスするように
                                                        SomAmulet.Bottle bottle = ItemDataLoader.getRandomBottle();
                                                        playerData.getItemInventory().add(bottle, 1, getPercent);
                                                        SomCore.globalMessageComponent(SomText.create(playerData.getDisplayName()).add("§aが").add(bottle.toSomText()).add("§aを§e獲得§aしました"));
                                                    }
                                                }
                                                if (randomDouble(0, 1) < 0.1) {
                                                    int amountU = 1;
                                                    if (bossWave) amountU *= 2;
                                                    playerData.getItemInventory().add(tierStone, amountU, 0.1);
                                                }
                                                if (randomDouble(0, 1) < 0.9) {
                                                    int amountH = 1;
                                                    if (bossWave) amountH *= 3;
                                                    playerData.getItemInventory().add(qualityStone, amountH, 0.9);
                                                }
                                            }
                                        }
                                        bossWave = false;
                                        readyTime = 10;
                                        wave++;
                                        if (difficulty() != difficulty(level(wave+1))) {
                                            bossWave = true;
                                            bossSpawn = true;
                                        }
                                        bossBar.setColor(BarColor.YELLOW);
                                        readyPhase = true;
                                    }
                                }
                            }
                        }
                        bossBar.setProgress(MinMax(CoreHealth/50f,0, 1));
                        if (CoreHealth < 0) {
                            this.cancel();
                            sendTitleMessage("§4§nDimensionCore is Broken", "§dディメンションコア§aが§4破壊§aされました", "§dディメンションコア§aが§4破壊§aされました", SomSound.BossSkill, 80);
                            SomTask.delay(() -> delete(), 100);
                        }
                        if (!readyPhase && System.currentTimeMillis()-waveStart > 1000*60*5) {
                            this.cancel();
                            sendTitleMessage("§4§nTime Over", "§4タイムオーバー", "§4タイムオーバー", SomSound.BossSkill, 80);
                            SomTask.delay(() -> delete(), 100);
                        }
                        for (PlayerData member : member) {
                            if (!member.isOnline() || member.isAFK() || member.getLocation().distance(SpawnLocation) > 150) {
                                resetPlayer(member);
                            }
                        }
                        if (member.isEmpty()) {
                            this.cancel();
                            delete();
                        }
                    }
                }.runTaskTimerAsynchronously(SomCore.plugin(), 60, 20);
            }, 60);
        }
    }

    private final String[] bossList = new String[]{"デバリ", "ヨーケ", "ヘンサル"};
    public void bossSpawn() {
        CustomLocation location = new CustomLocation(SomCore.World, -1004.5, -37, -58.5);
        MobData mobData = MobDataLoader.getMobData(bossList[randomInt(0, bossList.length)]);
        EnemyData enemyData = EnemyData.spawn(mobData, level(), difficulty(), location, member, mapData).setHealthMultiply(matchType.getMultiply());
        enemyData.setTargetOverride(Pathfinder[1][1]);
        enemyData.addEffect(DefensiveEnemy, enemyData);
        enemyData.updateStatus();
        enemies.get(1).add(enemyData);
        sendMessage("§4" + mobData.getDisplay() + "§aが§c侵入§aしてきました", SomSound.BossSkill);
    }

    public int count() {
        return (wave-1)*3 + 30;
    }

    public int level() {
        return level(wave);
    }

    public int level(int wave) {
        if (wave > 12) {
            return 60+(wave-12)*2;
        }
        return wave*5;
    }

    public DungeonDifficulty difficulty(int level) {
        return DungeonDifficulty.fromLevel(level);
    }

    public DungeonDifficulty difficulty() {
        return difficulty(level());
    }

    public void sendMessage(String message, SomSound sound) {
        for (PlayerData playerData : member) {
            playerData.sendMessage(Prefix + message, sound);
        }
    }

    public void sendTitle(String title, String subtitle, int fade, int time, int out, SomSound sound) {
        for (PlayerData playerData : member) {
            playerData.sendTitle(title, subtitle, fade, time, out);
            sound.play(playerData);
        }
    }

    public void sendTitleMessage(String title, String subtitle, String message, SomSound sound) {
        sendTitleMessage(title, subtitle, message, sound, 10);
    }

    public void sendTitleMessage(String title, String subtitle, String message, SomSound sound, int time) {
        for (PlayerData playerData : member) {
            playerData.sendTitle(title, subtitle, 10, time, 10);
            playerData.sendMessage(Prefix + message, sound);
        }
    }


    public enum MatchType {
        Solo("ソロ", "防衛戦に1人で挑みます", Material.TOTEM_OF_UNDYING, 1, 0.3),
        Duo("デュオ", "防衛戦に最大2人で挑みます", Material.EMERALD, 2, 0.5),
        Party("パーティ", "防衛戦に最大5人で挑みます", Material.AXOLOTL_BUCKET, 5, 1),
        World("ワールド", "防衛戦に最大30人で挑みます", Material.CRYING_OBSIDIAN, 30, 2),
        ;

        private final String display;
        private final List<String> lore;
        private final Material icon;
        private final int limit;
        private final double multiply;

        MatchType(String display, String lore, Material icon, int limit, double multiply) {
            this.display = display;
            this.lore = loreText(Collections.singletonList(lore));
            this.icon = icon;
            this.limit = limit;
            this.multiply = multiply;
        }

        public String getDisplay() {
            return display;
        }

        public List<String> getLore() {
            return lore;
        }

        public Material getIcon() {
            return icon;
        }

        public int getLimit() {
            return limit;
        }

        public double getMultiply() {
            return multiply;
        }

        public CustomItemStack viewItem() {
            return new CustomItemStack(icon).setDisplay("§e" + getDisplay() + "モード").addLore(getLore()).setCustomData("MatchType", toString());
        }

        public DefensiveBattle.MatchType next() {
            if (this.ordinal()+1 < DefensiveBattle.MatchType.values().length) {
                return DefensiveBattle.MatchType.values()[this.ordinal()+1];
            } else return DefensiveBattle.MatchType.values()[0];
        }
    }
}
