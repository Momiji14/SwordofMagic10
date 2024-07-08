package SwordofMagic10.Command;

import SwordofMagic10.Command.Developer.*;
import SwordofMagic10.Command.Player.*;
import org.bukkit.Bukkit;

public class CommandRegister {

    public static void run() {
        register("test", new Test());
        register("somReload", new SomReload());
        register("load", new Load());
        register("cloneLoad", new Load());
        register("save", new Save());
        register("mobClear", new MobClear());
        register("mobSpawn", new MobSpawn());
        register("playMode", new PlayMode());
        register("getItem", new GetItem());
        register("setLevel", new SetLevel());
        register("bossBarMessage", new BossBarMessage());
        register("displayParticle", new DisplayParticle());
        register("heal", new Heal());
        register("clearCT", new ClearCT());
        register("flySpeed", new FlySpeed());
        register("somRestart", new SomRestart());
        register("raidEnter", new RaidEnter());
        register("getEffect", new GetEffect());
        register("cast", new Cast());
        register("afk", new AFK());
        register("tpMap", new TPMap());
        register("playerVote", new PlayerVote());

        register("menu", new Menu());
        register("party", new Party());
        register("skipDeath", new SkipDeath());
        register("playerRide", new PlayerRide());
        register("particleDensity", new ParticleDensity());
        register("particleDensityOther", new ParticleDensityOther());
        register("particleDensityEnemy", new ParticleDensityEnemy());
        register("death", new Death());
        register("talkSpeed", new TalkSpeed());
        register("help", new Help());
        register("damageLog", new DamageLog());
        register("expLog", new ExpLog());
        register("itemLog", new ItemLog());
        register("buffLog", new BuffLog());
        register("skillLog", new SkillLog());
        register("quickCast", new QuickCast());
        register("pvp", new PvP());
        register("trade", new Trade());
        register("playerInfo", new PlayerInfo());
        //register("auction", new Auction());
        register("effectInfo", new EffectInfo());
        register("gatheringDrop", new GatheringDrop());
        register("viewItem", new ViewItem());
        register("market", new Market());
        register("order", new Order());
        register("viewAmount", new ViewAmount());
        register("reqClassExp", new ReqClassExp());
        register("reqGatheringExp", new ReqGatheringExp());
        register("searchSpawner", new SearchSpawner());
        register("petName", new PetName());
        register("favorite", new Favorite());
        register("pushStorage", new PushStorage());
    }

    //コマンドの登録
    static void register(String command, SomCommand executor) {
        try {
            Bukkit.getPluginCommand(command).setExecutor(executor);
            if (executor instanceof SomTabComplete tabComplete) {
                try {
                    Bukkit.getPluginCommand(command).setTabCompleter(tabComplete);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
