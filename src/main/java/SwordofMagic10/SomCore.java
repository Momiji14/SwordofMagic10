package SwordofMagic10;

import SwordofMagic10.Command.CommandRegister;
import SwordofMagic10.Command.Developer.MobClear;
import SwordofMagic10.Component.Events;
import SwordofMagic10.Component.PacketEvents;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.SomLoader;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Tutorial;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
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
import java.io.IOException;

public final class SomCore extends JavaPlugin implements PluginMessageListener {

    private static Plugin plugin;
    public static Location SpawnLocation;
    public static World World = Bukkit.getWorld("world");

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        World = Bukkit.getWorld("world");
        SpawnLocation = new Location(World, 120.5, -27, 147.5);
        new Events(this);
        SomLoader.load();
        SomSQL.connection();
        CommandRegister.run();
        Tutorial.run();

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        if (protocolManager != null) {
            protocolManager.addPacketListener(new PacketEvents(PacketAdapter.params(plugin, PacketType.Play.Server.BLOCK_CHANGE)));
        }

        for (World world : Bukkit.getWorlds()) {
            WorldManager.setupWorld(world);
        }

        try {
            MobClear.clean();
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData.get(player).loadAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SomTask.timer(() -> {
            for (PlayerData playerData : PlayerData.getPlayerList()) {
                playerData.saveSql();
            }
        }, 300, 20*60*15);
    }

    @Override
    public void onDisable() {
        try {
            MobClear.clean();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SomSQL.disconnect();

    }

    public static Plugin plugin() {
        return plugin;
    }

    public static void Log(String log) {
        Log(log, false);
    }
    public static void Log(String log, boolean stacktrace) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(log);
            }
        }
        Bukkit.getLogger().info(log);
        if (stacktrace) throw new RuntimeException("StackTrace Log");
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {

    }

    public static void TeleportServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(SomCore.plugin(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
