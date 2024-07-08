package SwordofMagic10.Component;

import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static SwordofMagic10.Component.Config.SomParticleAddress;

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
                if (playerData.getMining().getCoolTime().containsKey(location)) {
                    packet.getBlockData().write(0, WrappedBlockData.createData(Material.COBBLESTONE.createBlockData()));
                }
                if (playerData.getLumber().getCoolTime().containsKey(location)) {
                    packet.getBlockData().write(0, WrappedBlockData.createData(Material.STRIPPED_OAK_WOOD.createBlockData()));
                }
            }
        } else if (packetType == PacketType.Play.Server.SPAWN_ENTITY) {
            if (playerData.isBE()) {
                Entity entity = packet.getEntityModifier(event).read(0);
                if (entity != null) {
                    switch (entity.getType()) {
                        case ITEM_DISPLAY, BLOCK_DISPLAY -> event.setCancelled(true);
                    }
                }
            }
        }
    }
}
