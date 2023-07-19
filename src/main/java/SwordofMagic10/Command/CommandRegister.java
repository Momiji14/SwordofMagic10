package SwordofMagic10.Command;

import SwordofMagic10.Command.Developer.*;
import SwordofMagic10.Command.Player.*;
import org.bukkit.Bukkit;

public class CommandRegister {

    public static void run() {
        register("test", new Test());
        register("somReload", new SomReload());
        register("load", new Load());
        register("save", new Save());
        register("mobClear", new MobClear());
        register("mobSpawn", new MobSpawn());
        register("playMode", new PlayMode());
        register("getItem", new GetItem());
        register("setLevel", new SetLevel());
        register("bossBarMessage", new BossBarMessage());
        register("displayParticle", new DisplayParticle());
        register("heal", new Heal());

        register("menu", new Menu());
        register("party", new Party());
        register("skipDeath", new SkipDeath());
        register("playerRide", new PlayerRide());
        register("particleDensity", new ParticleDensity());
        register("death", new Death());
        register("talkSpeed", new TalkSpeed());
        register("help", new Help());
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
