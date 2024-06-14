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
import java.util.HashSet;
import java.util.Set;

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

        Location islandLocation = island.getLocation();
        IslandType type =  island.getIslandType();

        Chunk chunk = event.getChunk();


        if (chunk.equals(islandLocation.getChunk())) {
            for(Chunk chunks : getChunksWithinRenderDistance(player)) {
                chunks.setForceLoaded(true);
                chunks.load();
                try {
                    wait(5);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


                SchematicLoader.loadIslandByType(player, islandLocation, type);
                islandBuffer.setIslandLoaded(player, true);
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
    public static Set<Chunk> getChunksWithinRenderDistance(Player player) {
        Set<Chunk> chunks = new HashSet<>();
        Location location = player.getLocation();
        int renderDistance = 5;

        int playerChunkX = location.getChunk().getX();
        int playerChunkZ = location.getChunk().getZ();

        int minChunkX = playerChunkX - renderDistance;
        int maxChunkX = playerChunkX + renderDistance;
        int minChunkZ = playerChunkZ - renderDistance;
        int maxChunkZ = playerChunkZ + renderDistance;

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                chunks.add(location.getWorld().getChunkAt(x, z));
            }
        }
        return chunks;
    }




}
