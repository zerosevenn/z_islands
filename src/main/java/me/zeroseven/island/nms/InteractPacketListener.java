package me.zeroseven.island.nms;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.protocol.PacketType;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class InteractPacketListener extends PacketAdapter {


    private final JavaPlugin plugin;
    private final Map<UUID, Set<BlockPosition>> playerBlockViews;

    public InteractPacketListener(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.USE_ITEM);
        this.plugin = plugin;
        this.playerBlockViews = new HashMap<>();
        register();
    }

    private void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleBlockDig(event);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleUseItem(event);
            }
        });
    }

    private void handleBlockDig(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);
        BlockPosition position = packet.getBlockPositionModifier().read(0);

        if (digType == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
            if (isBlockInView(player, position)) {
                event.setCancelled(true);
            }
        }
    }

    private void handleUseItem(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        BlockPosition position = packet.getBlockPositionModifier().read(0);

        if (isBlockInView(player, position)) {
            event.setCancelled(true);
        }
    }

    private boolean isBlockInView(Player player, BlockPosition position) {
        Set<BlockPosition> visibleBlocks = playerBlockViews.getOrDefault(player.getUniqueId(), new HashSet<>());
        return visibleBlocks.contains(position);
    }

    public void addBlockToView(Player player, BlockPosition position) {
        playerBlockViews.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(position);
    }

    public void removeBlockFromView(Player player, BlockPosition position) {
        Set<BlockPosition> visibleBlocks = playerBlockViews.get(player.getUniqueId());
        if (visibleBlocks != null) {
            visibleBlocks.remove(position);
            if (visibleBlocks.isEmpty()) {
                playerBlockViews.remove(player.getUniqueId());
            }
        }
    }
}