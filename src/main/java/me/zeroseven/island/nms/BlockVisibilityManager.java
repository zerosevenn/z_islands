package me.zeroseven.island.nms;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class BlockVisibilityManager {
    private final ProtocolManager protocolManager;
    private final Set<Location> visibleBlocks = new HashSet<>();
    private final Player targetPlayer;

    public BlockVisibilityManager(Plugin plugin, ProtocolManager protocolManager, Player targetPlayer) {
        this.protocolManager = protocolManager;
        this.targetPlayer = targetPlayer;

        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!event.getPlayer().equals(targetPlayer)) {
                    return;
                }

                if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
                    handleBlockChange(event);
                } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
                    handleMultiBlockChange(event);
                }
            }
        });
    }

    private void handleBlockChange(PacketEvent event) {
        Location location = event.getPacket().getBlockPositionModifier().read(0).toLocation(targetPlayer.getWorld());
        if (!visibleBlocks.contains(location)) {
            event.getPacket().getBlockData().write(0, WrappedBlockData.createData(Material.AIR));
        }
    }

    private void handleMultiBlockChange(PacketEvent event) {
        int chunkX = event.getPacket().getIntegers().read(0);
        int chunkZ = event.getPacket().getIntegers().read(1);
        short chunkSectionCount = event.getPacket().getShorts().read(0);

        // Loop through each chunk section
        for (int i = 0; i < chunkSectionCount; i++) {
            short blockCount = event.getPacket().getShorts().read(1 + i * 2);
            byte[] blockDataArray = event.getPacket().getByteArrays().read(0);

            // Loop through each block in the chunk section
            for (int j = 0; j < blockCount; j++) {
                int blockOffset = 2 + j * 8;
                long blockData = readVarInt(blockDataArray, blockOffset);

                int blockX = (int) ((blockData >> 38) & 0x3FL);
                int blockY = (int) ((blockData >> 26) & 0xFFF);
                int blockZ = (int) ((blockData >> 0) & 0x3FL);

                Location blockLocation = new Location(targetPlayer.getWorld(), (chunkX << 4) + blockX, blockY, (chunkZ << 4) + blockZ);

                if (!visibleBlocks.contains(blockLocation)) {
                    // Set block to air
                    blockDataArray[blockOffset] = 0;
                    blockDataArray[blockOffset + 1] = 0;
                    blockDataArray[blockOffset + 2] = 0;
                    blockDataArray[blockOffset + 3] = 0;
                }
            }

            event.getPacket().getByteArrays().write(0, blockDataArray);
        }

    }

    private long readVarInt(byte[] data, int offset) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = data[offset + numRead];
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public void addVisibleBlock(Location location) {
        visibleBlocks.add(location);
        sendBlockUpdate(location, targetPlayer);
    }

    private void sendBlockUpdate(Location location, Player player) {
        player.sendBlockChange(location, location.getBlock().getBlockData());
    }
}
