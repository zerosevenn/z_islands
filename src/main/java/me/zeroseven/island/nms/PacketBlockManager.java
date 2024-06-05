package me.zeroseven.island.nms;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketBlockManager extends PacketAdapter {

    private JavaPlugin plugin;
    private Map<UUID, Set<BlockPosition>> blockSet;

    public PacketBlockManager(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST,
                PacketType.Play.Server.BLOCK_CHANGE,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Server.BLOCK_ACTION);
        this.plugin = plugin;
        this.blockSet = new HashMap<>();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event.getPacket());
            BlockPosition blockLocation = wrapper.getLocation();

            if (hasBeenFaked(event.getPlayer(), blockLocation)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            BlockPosition blockPosition = event.getPacket().getBlockPositionModifier().read(0);

            if (hasBeenFaked(event.getPlayer(), blockPosition)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean hasBeenFaked(Player player, BlockPosition location) {
        Set<BlockPosition> playerFakeBlocks = blockSet.get(player.getUniqueId());
        return playerFakeBlocks != null && playerFakeBlocks.contains(location);
    }

    public Map<UUID, Set<BlockPosition>> getBlockSet() {
        return blockSet;
    }
}
