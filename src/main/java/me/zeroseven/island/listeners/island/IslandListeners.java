package me.zeroseven.island.listeners.island;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.island.IslandType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import me.zeroseven.island.nms.SchematicLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.PanelUI;

public class IslandListeners implements Listener {

    private final IslandBuffer islandBuffer;
    private final IslandPlugin instance;
    private final ProtocolManager protocolManager;

    public IslandListeners(IslandPlugin instance) {
        this.instance = instance;
        this.islandBuffer = IslandPlugin.getIslandBuffer();
        this.protocolManager = instance.getProtocolManager();
        onClientInteract();
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();

        if (islandBuffer.isIslandLoaded(player)) {
            return;
        }

        Island island = islandBuffer.getPlayerIsland(player);
        if (island == null) {
            return;
        }

        Location islandLocation = island.getSpawnLocation();
        IslandType type = island.getIslandType();
        Chunk centralChunk = event.getChunk();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Chunk chunk = centralChunk.getWorld().getChunkAt(centralChunk.getX() + x, centralChunk.getZ() + z);
                if (chunk.equals(islandLocation.getChunk())) {
                    chunk.load();
                    SchematicLoader.loadIslandByType(player, islandLocation, type);
                    islandBuffer.setIslandLoaded(player, true);
                }
            }
        }

    }


    public void onClientInteract() {
        PacketListener packetListener =  new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ITEM, PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (event.getPacketType() == PacketType.Play.Client.USE_ITEM || event.getPacketType() == PacketType.Play.Client.BLOCK_DIG || event.getPacketType() == PacketType.Play.Client.BLOCK_PLACE) {
                    if (event.getPacket().getHands().size() > 0) {
                        BlockPosition blockPosition = event.getPacket().getBlockPositionModifier().read(0);
                        if (blockPosition != null) {
                            Location location = new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());

                            if(islandBuffer.getVisibleBlocks(player).get(location) == null)
                                return;

                            ItemStack block = islandBuffer.getVisibleBlocks(player).get(location);
                            if(block.getType().isBlock()) {
                                System.out.println("event success" + "Setting block: " + block.getType());
                                event.setCancelled(true);
                                SchematicLoader.sendBlockChange(player, location, block.getType());
                            }
                        }
                    }
                }
            }
        };

        protocolManager.addPacketListener(packetListener);
    }



}
