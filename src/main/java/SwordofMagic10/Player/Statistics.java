package SwordofMagic10.Player;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSQL;
import org.bukkit.Material;

import java.time.LocalDateTime;
import java.util.HashMap;

import static SwordofMagic10.Component.Config.DateFormat;
import static SwordofMagic10.Component.Config.ServiceStart;
import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.Component.Function.scale;

public class Statistics {

    private final PlayerData playerData;
    public Statistics(PlayerData playerData) {
        this.playerData = playerData;
    }
    private LocalDateTime playStart = LocalDateTime.now();
    private LocalDateTime lastLogout = LocalDateTime.now();
    private final HashMap<Type, Double> enums = new HashMap<>();

    public LocalDateTime getPlayStart() {
        return playStart;
    }

    public void setPlayStart(LocalDateTime playStart) {
        this.playStart = playStart;
    }

    public LocalDateTime getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(LocalDateTime lastLogout) {
        this.lastLogout = lastLogout;
    }

    public CustomItemStack viewItem() {
        CustomItemStack item = new CustomItemStack(Material.GLOW_ITEM_FRAME).setDisplay("統計情報");
        item.addLore(decoLore("ゲーム開始日") + playStart.format(DateFormat));
        item.addLore(decoLore("プレイ時間") + scale(playerData.getPlaytime()/3600.0, 2) + "H");
        for (Type type : Type.values()) {
            item.addLore(decoLore(type.getDisplay()) + scale(get(type), type.getScale()));
        }
        return item;
    }

    public enum Type {
        EnemyKill("エネミー討伐数", 0),
        DungeonClear("ダンジョンクリア数", 0),
        QuestClear("クエストクリア", 0),
        DayQuestClear("デイリークリア", 0),
        AimLabCPS("AimLab最高CPS", 3),
        AimLabClick("AimLabクリック数", 0),
        LightsOutClearTime("LOクリアタイム", 3, true),
        LightsOutClearCount("LOクリア回数", 0),
        FishingCPS("漁獲最高CPS", 3),
        Pachislot("スロット最高スコア", 0),
        PachislotCount("スロット合計スコア", 0),
        TypingKPS("タイピング最高KPS", 3),
        TypingWordCount("タイピング文字数", 0),
        PianoTileKPS("ピアノタイル最高KPS", 3),
        PianoTileCombo("ピアノタイル最高コンボ", 0),
        PianoTileCount("ピアノタイルタップ数", 0),

        MiningCount("採掘回数", 0),
        LumberCount("伐採回数", 0),
        CollectCount("採集回数", 0),
        FishingCount("漁獲回数", 0),
        HuntingCount("狩猟回数", 0),
        ;

        private final String display;
        private final int scale;
        private final boolean asc;
        Type(String display, int scale) {
            this.display = display;
            this.scale = scale;
            asc = false;
        }

        Type(String display, int scale, boolean asc) {
            this.display = display;
            this.scale = scale;
            this.asc = asc;
        }

        public String getDisplay() {
            return display;
        }

        public int getScale() {
            return scale;
        }

        public boolean isASC() {
            return asc;
        }

        public static Type[] Ranking() {
            return new Type[]{EnemyKill, DungeonClear, AimLabCPS, LightsOutClearTime, Pachislot, TypingKPS, PianoTileKPS, FishingCPS};
        }
    }

    public double get(Type key) {
        return enums.getOrDefault(key, 0.0);
    }

    public void set(Type key, double score) {
        enums.put(key, score);
    }

    public void add(Type key, double score) {
        enums.merge(key, score, Double::sum);
    }

    public static final String Table = "PlayerStatistics";

    public SomJson save() {
        SomJson json = new SomJson();
        json.set("PlayStart", playStart.format(DateFormat));
        json.set("LastLogout", LocalDateTime.now().format(DateFormat));

        enums.forEach((type, value) -> {
            String[] priValue = new String[]{playerData.getUUIDAsString(), type.toString()};
            SomSQL.setSql(Statistics.Table, priKey, priValue, "Username", playerData.getUsername());
            SomSQL.setSql(Statistics.Table, priKey, priValue, "Value", value);
        });

//        enums.forEach((key, value) -> json.set(key.toString(), value));
        return json;
    }

    private static final String[] priKey = new String[]{"UUID", "Type"};
    public void load(SomJson json) {
        try {
            setPlayStart(LocalDateTime.parse(json.getString("PlayStart"), DateFormat));
        } catch (Exception e) {
            setPlayStart(LocalDateTime.parse(ServiceStart, DateFormat));
        }
        try {
            setLastLogout(LocalDateTime.parse(json.getString("LastLogout"), DateFormat));
        } catch (Exception e) {
            setLastLogout(LocalDateTime.now());
        }

        for (Type type : Type.values()) {
            String[] priValue = new String[]{playerData.getUUIDAsString(), type.toString()};
            if (SomSQL.existSql(Statistics.Table, priKey, priValue)) {
                enums.put(type, SomSQL.getDouble(Statistics.Table, priKey, priValue, "Value"));
            }
        }

//        for (Type key : Type.values()) {
//            if (json.has(key.toString())) {
//                enums.put(key, json.getDouble(key.toString(), get(key)));
//            }
//        }
    }
}
