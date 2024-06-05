package me.zeroseven.island.listeners;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ConnectionListener implements Listener {

    private JavaPlugin instance;
    private IslandBuffer islandBuffer;

    public ConnectionListener(JavaPlugin instance) {
        this.instance = instance;
        this.islandBuffer = IslandPlugin.getIslandBuffer();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        islandBuffer.loadPlayerIsland(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        islandBuffer.savePlayerIsland(player);
    }


}
