package me.zeroseven.island.buffer;

import me.zeroseven.island.database.IslandDAO;
import me.zeroseven.island.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class IslandBuffer {

    private JavaPlugin instance;
    private HashMap<Player, Island> islandHashMap;
    private IslandDAO islandDAO;

    public IslandBuffer(JavaPlugin instance) {
        this.instance = instance;
        this.islandHashMap = new HashMap<>();
        this.islandDAO = new IslandDAO(instance);
    }

    public Island getPlayerIsland(Player player){
        return islandHashMap.get(player);
    }

    public void loadPlayerIsland(Player player){
        if(islandDAO.getIsland(player) == null){
            return;
        }
        islandHashMap.put(player, islandDAO.getIsland(player));
    }

    public void updatePlayerIsland(Player player, Island island){
        islandHashMap.put(player, island);
    }

    public void savePlayerIsland(Player player){
        if(!islandHashMap.containsKey(player)){
            return;
        }
    }

}
