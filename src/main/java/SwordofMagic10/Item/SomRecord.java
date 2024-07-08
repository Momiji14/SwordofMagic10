package SwordofMagic10.Item;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.Material.WRITABLE_BOOK;

public class SomRecord extends SomItem {

    public static List<SomRecord> load() {
        List<SomRecord> record = new ArrayList<>();
        record.add(getLedosia());
        record.add(getLogna());

        SomRecord froal = new SomRecord("FroalRecord", "フロール(Froal)", ENCHANTED_BOOK);
        froal.addRecord("まだ書いてないですごめんなさい");
        record.add(froal);

        record.add(getClam());
        record.add(getLark());

        SomRecord gargil = new SomRecord("GargilRecord", "ガージル(Gargil)", ENCHANTED_BOOK);
        gargil.addRecord("まだ書いてないですごめんなさい");
        record.add(gargil);

        SomRecord nefy = new SomRecord("NefyRecord", "ネフィ(Nefy)", ENCHANTED_BOOK);
        nefy.addRecord("まだ書いてないですごめんなさい");
        record.add(nefy);

        record.add(getGriphia());

        return record;
    }

    @NotNull
    private static SomRecord getLedosia() {
        SomRecord ledosia = new SomRecord("LedosiaRecord", "レドシア(Ledosia)", ENCHANTED_BOOK);

        ledosia.addRecord("俺が物心ついてから、初めて感じたものは誰かの温もりではなく、冷たい鉄の感触だった。");
        ledosia.addRecord("");
        ledosia.addRecord("生き残るためには誰かを殺す必要があった。最初の頃は引き金を引くことに抵抗もあったが、");
        ledosia.addRecord("こんな環境じゃ、そんな恐怖もすぐに薄れてしまう。なんせ殺らなきゃ殺られてしまうからだ。");
        ledosia.addRecord("");
        ledosia.addRecord("時には捕虜として捕まったりもした。");
        ledosia.addRecord("その時は、口八丁手八丁に立ち回り、なんとかロシアンルーレットに成功すれば生きて返してもらえるような場の空気にし、無事生還した。");
        ledosia.addRecord("危険には価値があるなんていうが、確かにそのとおりだ。");
        ledosia.addRecord("あのときの得も言えぬ恐怖、引き金を引く感覚を今でも忘れることはないだろう。");
        ledosia.addRecord("");
        ledosia.addRecord("そんな人生も最後には、最前線で銃弾の雨を浴びせられ、感傷に浸るも間もなくあっけなく死んだがな。");
        return ledosia;
    }

    @NotNull
    private static SomRecord getLogna() {
        SomRecord logna = new SomRecord("LognaRecord", "ログナ(Logna)", ENCHANTED_BOOK);

        logna.addRecord("私は海が好きで、船が好きだった。");
        logna.addRecord("船に乗りながら何気ないことを考えて、そして船の上でご飯を食べる。");
        logna.addRecord("そんなことが私の幸せだった。");
        logna.addRecord("");
        logna.addRecord("でもそんな幸せも今日で終わりのようだ。");
        logna.addRecord("船が沈んでいる。こんな大海原のど真ん中で。");
        logna.addRecord("救命ヘリの定員は残り１名。船に残っているのは私と、名前も知らない若い子の合計２人だ。");
        logna.addRecord("もちろん私は若い子に譲ったよ。");
        logna.addRecord("");
        logna.addRecord("未来ある若者を助けたみたいなほうがなんとなくかっこいいし、");
        logna.addRecord("それに、救命ヘリの\"１台目\"がここに到着しているんだ。");
        logna.addRecord("２台目もすぐに来るだろう。");
        logna.addRecord("");
        logna.addRecord("その時の私はそんなことを考えていた。しかしそんなことはなかったのだ。");
        logna.addRecord("");
        logna.addRecord("船が沈んでいる。変わらずに。");
        logna.addRecord("為す術はない。");
        logna.addRecord("");
        logna.addRecord("腰まで水が来ている。");
        logna.addRecord("為す術はない。");
        logna.addRecord("");
        logna.addRecord("船は完全に沈み、波に揉まれている。");
        logna.addRecord("為す術はない。");
        logna.addRecord("");
        logna.addRecord("体力には限界が訪れ、波に頭を上から抑えられるように沈んでいく。");
        logna.addRecord("為す術はない。");
        logna.addRecord("");
        logna.addRecord("肺は空気を失い、代わりに水が流れ込む。");
        logna.addRecord("為す術はない。");
        logna.addRecord("");
        logna.addRecord("溺死ってこんなにも苦しいんだ。");
        logna.addRecord("");
        logna.addRecord("私がこんなに苦しんで死んでも、誰もそれを知り、覚えていることはないし、");
        logna.addRecord("もちろん地図に私の墓標が記されるわけでもないんだろうな。");
        logna.addRecord("薄れゆく意識の中、そんな事を考えていた。");
        return logna;
    }

    @NotNull
    private static SomRecord getLark() {
        SomRecord lark = new SomRecord("LarkRecord", "Lark(ラーク)", ENCHANTED_BOOK);

        lark.addRecord("私は足音が嫌いだ。");
        lark.addRecord("足音が近づいた後、止まり、決まって誰かが連れて行かれるからだ。");
        lark.addRecord("そして、連れて行かれたやつがどうなるかは知っている。");
        lark.addRecord("");
        lark.addRecord("本当に、本当に怖いんだ。");
        lark.addRecord("恐怖で頭がおかしくなりそうだ。");
        lark.addRecord("息を潜めるしか無い。");
        lark.addRecord("");
        lark.addRecord("ただここにいて１つだけわかったことがある。");
        lark.addRecord("それは、連れて行かれるやつの法則性だ。");
        lark.addRecord("今、ここにいる人間の中で一番恐怖している人間を、研究員は連れて行く。");
        lark.addRecord("");
        lark.addRecord("なので私は、この檻の中で同じようなことを思っている人間に話しかけ、");
        lark.addRecord("\"友達\"を作り、その関係に縋ることにした。");
        lark.addRecord("\"それに、もし私の番が来たとしてもそいつを差し出せるように？\"");
        lark.addRecord("意味はないのだろうが、そうでもしないと次に連れて行かれるのは私になるだろう。");
        lark.addRecord("");
        lark.addRecord(".");
        lark.addRecord(".");
        lark.addRecord(".");
        lark.addRecord("");
        lark.addRecord("怪物になったあとは理性を失ってしまうものかと思ったが、意外とそんなことはないんだな。");
        lark.addRecord("むしろ変に冷静だよ。");
        lark.addRecord("それに今日は、何故だかとても騒がしい。");
        return lark;
    }

    @NotNull
    public static SomRecord getClam() {
        SomRecord clam = new SomRecord("ClamRecord", "クラム(Clam)", ENCHANTED_BOOK);

        clam.addRecord("私自身に大層なストーリーがあるわけじゃない。");
        clam.addRecord("ただただ、書き留めるほどでもない日常を歩んでいます。");
        clam.addRecord("私もまた、自然の一部だから。");
        clam.addRecord("");
        clam.addRecord("でも、もう、私達には関わらないでください。");
        clam.addRecord("");
        clam.addRecord("お願いだから。");
        return clam;
    }

    @NotNull
    public static SomRecord getGriphia() {
        SomRecord clam = new SomRecord("GriphiaRecord", "グリフィア(Griphia)", ENCHANTED_BOOK);

        clam.addRecord("まだ書いてないですごめんなさい");
        return clam;
    }

    public SomRecord(String id, String display, Material icon) {
        setId(id);
        setDisplay(display);
        setIcon(icon);
        setLore("クリックでレコードを表示します");
        setItemCategory(ItemCategory.Record);
    }

    private List<String> record = new ArrayList<>();

    public List<String> getRecord() {
        return record;
    }

    public void setRecord(List<String> record) {
        this.record = record;
    }

    public void addRecord(String text) {
        record.add(text);
    }

    public void use(PlayerData playerData) {
        List<String> message = new ArrayList<>();
        message.add(decoText(getDisplay()));
        message.addAll(loreText(getRecord()));
        playerData.sendMessage(message, SomSound.Tick);
    }
}
