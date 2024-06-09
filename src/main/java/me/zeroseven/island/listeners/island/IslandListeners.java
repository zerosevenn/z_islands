package me.zeroseven.island.listeners.island;

import codes.kooper.blockify.events.BlockifyInteractEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IslandListeners implements Listener {


    private JavaPlugin instance;
    private IslandBuffer islandBuffer;

    public IslandListeners(JavaPlugin instance) {
        this.instance = instance;
        this.islandBuffer = IslandPlugin.getIslandBuffer();
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event){
        if(islandBuffer.getPlayerIsland(event.getPlayer()) == null){
            return;
        }

        Island island = islandBuffer.getPlayerIsland(event.getPlayer());

        Location location = island.getLocation();

        if(location.getChunk().equals(event.getChunk())){
        }

    }
}
