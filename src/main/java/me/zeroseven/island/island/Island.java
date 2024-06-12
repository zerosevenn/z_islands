package me.zeroseven.island.island;

import me.zeroseven.island.minions.Minion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;



public class Island {

    private Location spawnLocation;
    private Location location;
    private Player owner;
    private List<Player> members;
    private List<Minion> minions;

    private IslandType islandType;

    public Island(Location spawnLocation, Location location, Player owner, List<Player> members, List<Minion> minions, IslandType islandType) {
        this.spawnLocation = spawnLocation;
        this.location = location;
        this.owner = owner;
        this.members = members;
        this.minions = minions;
        this.islandType = islandType;
    }

    public IslandType getIslandType() {
        return islandType;
    }

    public void setIslandType(IslandType islandType) {
        this.islandType = islandType;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public List<Minion> getMinions() {
        return minions;
    }

    public void setMinions(List<Minion> minions) {
        this.minions = minions;
    }


}

