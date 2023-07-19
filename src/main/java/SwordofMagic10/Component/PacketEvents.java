package SwordofMagic10.Component;

import SwordofMagic10.Player.PlayerData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketEvents extends PacketAdapter {
    public PacketEvents(@NotNull PacketAdapter.AdapterParameteters params) {
        super(params);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        PacketContainer packet = event.getPacket();
        PacketType packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            Location location = packet.getBlockPositionModifier().read(0).toLocation(player.getWorld());
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (playerData.getCollect().getCoolTime().containsKey(location)) {
                    packet.getBlockData().write(0, WrappedBlockData.createData(Material.AIR.createBlockData()));
                }
            }
        } else if (packetType == PacketType.Play.Server.WORLD_PARTICLES
                || packetType == PacketType.Play.Server.STOP_SOUND
                || packetType == PacketType.Play.Server.ENTITY_SOUND
                || packetType == PacketType.Play.Server.CUSTOM_SOUND_EFFECT
                || packetType == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            if (playerData.isAFK()) {
                event.setCancelled(true);
            }
        }
    }
}
