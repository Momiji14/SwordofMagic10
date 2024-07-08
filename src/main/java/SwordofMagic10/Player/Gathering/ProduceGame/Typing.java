package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.TypingDataLoader;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static SwordofMagic10.Component.Function.scale;
import static SwordofMagic10.SomCore.Log;

public class Typing {
    private final ProduceGame.Game Typing = ProduceGame.Game.Typing;
    private final PlayerData playerData;
    private BukkitTask task;
    private boolean start = false;
    private long startTime = 0;
    private String typing;

    public Typing(PlayerData playerData) {
        this.playerData = playerData;
    }

    public boolean isStart() {
        return start;
    }

    public void start() {
        if (!start) {
            start = true;
            playerData.closeInventory();

            new BukkitRunnable() {
                int i = 3;
                @Override
                public void run() {
                    playerData.sendTitle("§e" + i, "§r", 0, 22, 0);
                    SomSound.Tick.play(playerData);
                    i--;
                    if (i < 0) {
                        this.cancel();
                        startTask();
                    }
                }
            }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 20);
        }
    }

    public void startTask() {
        String title = TypingDataLoader.getRandomTitle();
        typing = TypingDataLoader.get(title);
        startTime = System.currentTimeMillis();
        SomSound.Hit.play(playerData);

        if (task != null) task.cancel();
        task = SomTask.timerPlayer(playerData, () -> {
            if (playerData.getProduceGame().check(ProduceGame.Game.Typing)) {
                playerData.sendTitle("§e" + title, "§a" + typing, 0, 22, 0);
            } else end();
        }, 1, 20);
    }

    public void typing(String rawTyping) {
        if (rawTyping.equalsIgnoreCase("end")) {
            end();
            return;
        }
        String typing = StringUtils.replaceEach(rawTyping, romaji, hiragana);
        if (this.typing.equalsIgnoreCase(typing)) {
            double kps = rawTyping.length()/((System.currentTimeMillis()-startTime)/1000.0);
            if (kps < 17.516) {
                playerData.getProduce().ProducePlayerTick(typing.length() * 3);
                playerData.getStatistics().add(Statistics.Type.TypingWordCount, typing.length());
                playerData.sendMessage("§e[" + Typing.getDisplay() + "]§r §e" + scale(kps, 3) + "KPS", SomSound.Nope);
                double currentKps = playerData.getStatistics().get(Statistics.Type.TypingKPS);
                if (kps > currentKps) {
                    playerData.sendMessage("§e" + Statistics.Type.TypingKPS.getDisplay() + "§aを§b更新§aしました §e[" + scale(currentKps, 3) + " -> " + scale(kps, 3) + "]", SomSound.Level);
                    playerData.getStatistics().set(Statistics.Type.TypingKPS, kps);
                }
                startTask();
            } else {
                List<String> message = new ArrayList<>();
                message.add("§e[" + Typing.getDisplay() + "] §cこのスコア(" + scale(kps, 3) + ")は世界記録を超えています");
                message.add("§e[" + Typing.getDisplay() + "] §c不正でない場合は証拠とともに運営に報告してください");
                playerData.sendMessage(message, SomSound.Nope);
                SomCore.WarnLog(playerData, " Typing KPS " + scale(kps, 3));
            }
        } else {
            List<String> message = new ArrayList<>();
            message.add("§e[" + Typing.getDisplay() + "]§r §c間違っています「" + typing + "」");
            message.add("§e[" + Typing.getDisplay() + "]§r §c終了する場合は「end」と入力してください");
            playerData.sendMessage(message, SomSound.Nope);
        }
    }

    public void end() {
        start = false;
        task.cancel();
        playerData.getProduceGame().leaveGame();
    }

    private static String[] romaji;
    private static String[] hiragana;
    private static final LinkedHashMap<String, String> kanaData = new LinkedHashMap<>();

    public static void registerKana(String kana, String... latin) {
        for (String str : latin) {
            kanaData.put(str, kana);
        }
    }

    public static void initialize() {
        registerKana("あ", "a");
        registerKana("い", "i", "yi");
        registerKana("う", "u", "wu", "whu");
        registerKana("え", "e");
        registerKana("お", "o");

        registerKana("うぁ", "wha");
        registerKana("うぃ", "wi", "whi");
        registerKana("うぇ", "we", "whe");
        registerKana("うぉ", "who");
        registerKana("ゐ", "wyi");
        registerKana("ゑ", "wye");

        registerKana("いぇ", "ye");

        registerKana("ぁ", "la", "xa");
        registerKana("ぃ", "li", "xi", "lyi", "xyi");
        registerKana("ぅ", "lu", "xu");
        registerKana("ぇ", "le", "xe");
        registerKana("ぉ", "lo", "xo");

        registerKana("か", "ka", "ca");
        registerKana("き", "ki");
        registerKana("く", "ku", "cu", "qu");
        registerKana("け", "ke");
        registerKana("こ", "ko", "co");

        registerKana("きゃ", "kya");
        registerKana("きぃ", "kyi");
        registerKana("きゅ", "kyu");
        registerKana("きぇ", "kye");
        registerKana("きょ", "kyo");

        registerKana("くゃ", "qya");
        registerKana("くゅ", "qyu");
        registerKana("くょ", "qyo");

        registerKana("くぁ", "qa", "qwa", "kwa");
        registerKana("くぃ", "qi", "qwi", "qyi");
        registerKana("くぅ", "qwu");
        registerKana("くぇ", "qe", "qwe", "qye");
        registerKana("くぉ", "qo", "qwo", "kwo");

        registerKana("が", "ga");
        registerKana("ぎ", "gi");
        registerKana("ぐ", "gu");
        registerKana("げ", "ge");
        registerKana("ご", "go");

        registerKana("ぎゃ", "gya");
        registerKana("ぎぃ", "gyi");
        registerKana("ぎゅ", "gyu");
        registerKana("ぎぇ", "gye");
        registerKana("ぎょ", "gyo");

        registerKana("ぐゃ", "gwa");
        registerKana("ぐぃ", "gwi");
        registerKana("ぐゅ", "gwu");
        registerKana("ぐぇ", "gwe");
        registerKana("ぐょ", "gwo");

        registerKana("ヵ", "lka", "xka");
        registerKana("ヶ", "lke", "xka");

        registerKana("さ", "sa");
        registerKana("し", "si", "ci", "shi");
        registerKana("す", "su");
        registerKana("せ", "se", "ce");
        registerKana("そ", "so");

        registerKana("しゃ", "sya", "sha");
        registerKana("しぃ", "syi");
        registerKana("しゅ", "syu", "shu");
        registerKana("しぇ", "sye", "she");
        registerKana("しょ", "syo", "sho");

        registerKana("すぁ", "swa");
        registerKana("すぃ", "swi");
        registerKana("すぅ", "swu");
        registerKana("すぇ", "swe");
        registerKana("すぉ", "swo");

        registerKana("ざ", "za");
        registerKana("じ", "zi", "ji");
        registerKana("ず", "zu");
        registerKana("ぜ", "ze");
        registerKana("ぞ", "zo");

        registerKana("じゃ", "zya", "ja", "jya");
        registerKana("じぃ", "zyi", "jyi");
        registerKana("じゅ", "zyu", "ju", "jyu");
        registerKana("じぇ", "zye", "je", "jye");
        registerKana("じょ", "zyo", "jo", "jyo");

        registerKana("ずゃ", "zwa");
        registerKana("ずぃ", "zwi");
        registerKana("ずゅ", "zwu");
        registerKana("ずぇ", "zwe");
        registerKana("ずょ", "zwo");

        registerKana("た", "ta");
        registerKana("ち", "ti", "chi");
        registerKana("つ", "tu", "tsu");
        registerKana("て", "te");
        registerKana("と", "to");

        registerKana("ちゃ", "tya", "cha", "cya");
        registerKana("ちぃ", "tyi", "cyi");
        registerKana("ちゅ", "tyu", "chu", "cyu");
        registerKana("ちぇ", "tye", "che", "cye");
        registerKana("ちょ", "tyo", "cho", "cyo");

        registerKana("つぁ", "tsa");
        registerKana("つぃ", "tsi");
        registerKana("つぇ", "tse");
        registerKana("つぉ", "tso");

        registerKana("てゃ", "tha");
        registerKana("てぃ", "thi");
        registerKana("てゅ", "thu");
        registerKana("てぇ", "the");
        registerKana("てょ", "tho");

        registerKana("とゃ", "twa");
        registerKana("とぃ", "twi");
        registerKana("とゅ", "twu");
        registerKana("とぇ", "twe");
        registerKana("とょ", "two");

        registerKana("だ", "da");
        registerKana("ぢ", "di");
        registerKana("づ", "du");
        registerKana("で", "de");
        registerKana("ど", "do");

        registerKana("ぢゃ", "dya");
        registerKana("ぢぃ", "dyi");
        registerKana("ぢゅ", "dyu");
        registerKana("ぢぇ", "dye");
        registerKana("ぢょ", "dyo");

        registerKana("でゃ", "dha");
        registerKana("でぃ", "dhi");
        registerKana("でゅ", "dhu");
        registerKana("でぇ", "dhe");
        registerKana("でょ", "dho");

        registerKana("どぁ", "dwa");
        registerKana("どぃ", "dwi");
        registerKana("どぅ", "dwu");
        registerKana("どぇ", "dwe");
        registerKana("どぉ", "dwo");

        registerKana("っ", "ltu", "xtu", "ltsu", "xtsu");

        registerKana("な", "na");
        registerKana("に", "ni");
        registerKana("ぬ", "nu");
        registerKana("ね", "ne");
        registerKana("の", "no");

        registerKana("にぁ", "nya");
        registerKana("にぃ", "nyi");
        registerKana("にぅ", "nyu");
        registerKana("にぇ", "nye");
        registerKana("にょ", "nyo");

        registerKana("は", "ha");
        registerKana("ひ", "hi");
        registerKana("ふ", "hu", "fu");
        registerKana("へ", "he");
        registerKana("ほ", "ho");

        registerKana("ひゃ", "hya");
        registerKana("ひぃ", "hyi");
        registerKana("ひゅ", "hyu");
        registerKana("ひぇ", "hye");
        registerKana("ひょ", "hyo");

        registerKana("ふぁ", "fwa", "fa");
        registerKana("ふぃ", "fwi", "fi", "fyi");
        registerKana("ふぅ", "fwu");
        registerKana("ふぇ", "fwe", "fe", "fye");
        registerKana("ふぉ", "fwo", "fo");

        registerKana("ふゃ", "fya");
        registerKana("ふゅ", "fyu");
        registerKana("ふょ", "fyo");

        registerKana("ば", "ba");
        registerKana("び", "bi");
        registerKana("ぶ", "bu");
        registerKana("べ", "be");
        registerKana("ぼ", "bo");

        registerKana("びゃ", "bya");
        registerKana("びぃ", "byi");
        registerKana("びゅ", "byu");
        registerKana("びぇ", "bye");
        registerKana("びょ", "byo");

        registerKana("ヴぁ", "va");
        registerKana("ヴぃ", "vi", "vyi");
        registerKana("ヴ", "vu");
        registerKana("ヴぇ", "ve", "vye");
        registerKana("ヴぉ", "vo");

        registerKana("ヴゃ", "vya");
        registerKana("ヴゅ", "vyu");
        registerKana("ヴょ", "vye");

        registerKana("ぱ", "pa");
        registerKana("ぴ", "pi");
        registerKana("ぷ", "pu");
        registerKana("ぺ", "pe");
        registerKana("ぽ", "po");

        registerKana("ぴゃ", "pya");
        registerKana("ぴぃ", "pyi");
        registerKana("ぴゅ", "pyu");
        registerKana("ぴぇ", "pye");
        registerKana("ぴょ", "pyo");

        registerKana("ま", "ma");
        registerKana("み", "mi");
        registerKana("む", "mu");
        registerKana("め", "me");
        registerKana("も", "mo");

        registerKana("みゃ", "mya");
        registerKana("みぃ", "myi");
        registerKana("みゅ", "myu");
        registerKana("みぇ", "mye");
        registerKana("みょ", "myo");

        registerKana("や", "ya");
        registerKana("ゆ", "yu");
        registerKana("よ", "yo");

        registerKana("ゃ", "lya", "xya");
        registerKana("ゅ", "lyu", "xyu");
        registerKana("ょ", "lyo", "xyo");

        registerKana("ら", "ra");
        registerKana("り", "ri");
        registerKana("る", "ru");
        registerKana("れ", "re");
        registerKana("ろ", "ro");

        registerKana("りゃ", "rya");
        registerKana("りぃ", "ryi");
        registerKana("りゅ", "ryu");
        registerKana("りぇ", "rye");
        registerKana("りょ", "ryo");

        registerKana("わ", "wa");
        registerKana("を", "wo");
        registerKana("ん", "nn", "xn", "n");

        registerKana("ゎ", "lwa", "xwa");

        for (Map.Entry<String, String> entry : new HashSet<>(kanaData.entrySet())) {
            if (!StringUtils.startsWithAny(entry.getKey(), "a", "i", "u", "e", "o", "n")) {
                registerKana("っ" + entry.getValue(), entry.getKey().charAt(0) + entry.getKey());
            }
        }

        registerKana("ー", "-");
        registerKana("、", ",");
        registerKana("。", ".");
        registerKana("？", "?");

        romaji = kanaData.keySet().toArray(new String[0]);
        hiragana = kanaData.values().toArray(new String[0]);
    }
}
