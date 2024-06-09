package me.zeroseven.island.buffer;

import me.zeroseven.island.database.IslandDAO;
import me.zeroseven.island.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class IslandBuffer {

    private JavaPlugin instance;
    private HashMap<Player, Island> islandHashMap;
    private HashMap<Player, Boolean> isLoadedMap;
    private HashMap<Player, Player> friendIslandMap;
    private IslandDAO islandDAO;

    public IslandBuffer(JavaPlugin instance) {
        this.instance = instance;
        this.islandHashMap = new HashMap<>();
        this.isLoadedMap = new HashMap<>();
        this.friendIslandMap = new HashMap<>();
        this.islandDAO = new IslandDAO(instance);
    }

    public Island getPlayerIsland(Player player){
        return islandHashMap.get(player);
    }

    public void loadPlayerIsland(Player player){
        if(islandDAO.getIsland(player) == null){
            return;
        }
        System.out.println("Trying load player");
        islandHashMap.put(player, islandDAO.getIsland(player));
    }

    public void updatePlayerIsland(Player player, Island island){
        islandHashMap.put(player, island);
    }

    public void savePlayerIsland(Player player){
        if(!islandHashMap.containsKey(player)){
            return;
        }
        System.out.println("Trying save player");
        islandDAO.insertIsland(getPlayerIsland(player));
    }

    public boolean isIslandLoaded(Player player) {
        return isLoadedMap.getOrDefault(player, false);
    }

    public void setIslandLoaded(Player player, boolean isLoaded) {
        isLoadedMap.put(player, isLoaded);
    }

    public Player getFriendIsland(Player player) {
        return friendIslandMap.get(player);
    }

    public void setFriendIsland(Player player, Player friend) {
        friendIslandMap.put(player, friend);
    }

    public void removeFriendIsland(Player player) {
        friendIslandMap.remove(player);
    }
}
