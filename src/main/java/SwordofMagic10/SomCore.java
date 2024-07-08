package SwordofMagic10;

import SwordofMagic10.Command.CommandRegister;
import SwordofMagic10.Command.Developer.MobClear;
import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.SomLoader;
import SwordofMagic10.Entity.Enemy.Dummy;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Gathering.Hunting;
import SwordofMagic10.Player.Gathering.ProduceGame.Typing;
import SwordofMagic10.Player.InventoryViewer;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Player.QuickGUI.QuickGUI;
import SwordofMagic10.Player.Tutorial;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public final class SomCore extends JavaPlugin implements PluginMessageListener {

    private static Plugin plugin;
    public static Location SpawnLocation;
    public static World World = Bukkit.getWorld("world");
    public static String SNCChannel = "snc:main";
    public static String ID = "Error";
    public static boolean SomReload = false;

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, SNCChannel);
        ID = new File(".").getAbsoluteFile().getParentFile().getName();

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        if (protocolManager != null) {
            protocolManager.addPacketListener(new PacketEvents(PacketAdapter.params(plugin, PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.SPAWN_ENTITY)));
        }

        World = Bukkit.getWorld("world");
        SpawnLocation = new Location(World, 120.5, -27, 147.5);
        for (World world : Bukkit.getWorlds()) {
            WorldManager.setupWorld(world);
        }

        new Events(this);
        SomLoader.load();
        SomSQL.connection();
        CommandRegister.run();
        Hunting.spawner();
        Tutorial.run();
        DurationSkill.setup();
        Dummy.run();
        //Auction.run();
        DungeonInstance.updateJoinStatus();
        Typing.initialize();
        QuickGUI.load();
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "citizens load");

        try {
            MobClear.clean();
            for (Player player : SomCore.getPlayers()) {
                PlayerData.get(player).loadAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SomTask.timer(() -> {
            for (PlayerData playerData : PlayerData.getPlayerList()) {
                playerData.saveSql();
                playerData.sendSomText(tips.text());
            }
            tips = tips.next();
        }, 300, 20*60*15);

        Board.initialize();
    }

    private Tips tips = Tips.values()[0];
    private static final String TipsPrefix = "§e[TIPS]";
    public enum Tips {
        Discord(SomText.create("§bSword of Magic Network§aの§9Discord§aはこちら!\n↓最新情報を見逃さないようにしよう↓\n").addOpenURL("§ehttps://discord.gg/YSnGhhG", "§eクリックでURLを開く", "https://discord.gg/YSnGhhG")),
        VoteJMS(SomText.create("§b投票§aをすることで§e報酬§aを手に入れることができます\n").addOpenURL("§ehttps://minecraft.jp/servers/mc.somrpg.net", "§eクリックでURLを開く", "https://minecraft.jp/servers/mc.somrpg.net")),
        ;

        private final SomText text;

        Tips(SomText text) {
            this.text = text;
        }

        public SomText text() {
            return text;
        }

        public Tips next() {
            if (this.ordinal()+1 < Tips.values().length) {
                return Tips.values()[this.ordinal()+1];
            } else return Tips.values()[0];
        }
    }

    @Override
    public void onDisable() {
        try {
            MobClear.clean();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public static Plugin plugin() {
        return plugin;
    }

    public static Collection<Player> getPlayers() {
        Collection<Player> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                list.add(player);
            }
        }
        return list;
    }

    public static void Log(Exception e) {
        Log(e.toString());
        for (int i = 0; i < e.getStackTrace().length; i++) {
            Log(e.getStackTrace()[i].toString());
        }
    }
    public static void Log(String log) {
        Log(log, false);
    }
    public static void Log(String log, boolean stacktrace) {
        for (Player player : SomCore.getPlayers()) {
            if (player.hasPermission("som10.reload")) {
                player.sendMessage(log);
            }
        }
        Bukkit.getLogger().info(log);
        if (stacktrace) throw new RuntimeException("StackTrace Log");
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {

    }

    public static void sendMessageComponent(Player player, Component component) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Component");
            out.writeUTF(player.getName());
            out.writeUTF(JSONComponentSerializer.json().serialize(component));
            player.sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void globalMessageComponent(SomText somText) {
        globalMessageComponent(somText.toComponent());
    }

    public static void globalMessageComponent(Component component) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GlobalComponent");
            out.writeUTF(JSONComponentSerializer.json().serialize(component));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
                break;
            }
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WarnLog(PlayerData sender, String log) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Som10WarnLog");
            out.writeUTF(sender.getPlayer().getName() + ": " + log);
            sender.getPlayer().sendPluginMessage(SomCore.plugin(), SNCChannel, b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
