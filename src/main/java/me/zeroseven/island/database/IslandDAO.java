package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLContainer;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.island.IslandType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandDAO extends MySQLContainer {

    private PlayersDAO playersDAO;
    private MinionsDAO minionsDAO;

    public IslandDAO(JavaPlugin instance) {
        super(instance);
        this.minionsDAO = new MinionsDAO(instance);
        this.playersDAO = new PlayersDAO(instance);
    }

    public void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ISLAND(owner VARCHAR(36) PRIMARY KEY, spawnLocation STRING, location STRING, islandType VARCHAR(16))";
        try(Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertIsland(Island island){

        if(getIsland(island.getOwner()) != null){
            updateIsland(island);
            return;
        }

        String sql = "INSERT INTO ISLAND(owner, spawnLocation, location, islandType) VALUES (?,?,?,?)";

        try(Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, island.getOwner().getName());
            stm.setString(2, setLocation(island.getSpawnLocation()));
            stm.setString(3, setLocation(island.getLocation()));
            stm.setString(4, island.getIslandType().toString());
            playersDAO.insertMembers(island.getOwner(), island.getMembers());
            minionsDAO.insertMinions(island.getMinions());
            stm.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Island getIsland(Player owner){
        String sql = "SELECT * FROM ISLAND WHERE owner = ?";
        try(Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, owner.getName());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String spawnLocationString = rs.getString("spawnLocation");
                String locationString = rs.getString("location");
                String islandType = rs.getString("islandType");
                Location spawnLocation = getLocation(spawnLocationString);
                Location location = getLocation(locationString);
                return new Island(
                        spawnLocation, location, owner,
                        playersDAO.getMembers(owner),
                        minionsDAO.selectMinionsByOwner(owner.getUniqueId()), IslandType.valueOf(islandType)
                );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void updateIsland(Island island) {
        String sql = "UPDATE ISLAND SET spawnLocation = ?, location = ?, islandType = ? WHERE owner = ?";
        try (Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, setLocation(island.getSpawnLocation()));
            stm.setString(2, setLocation(island.getLocation()));
            stm.setString(3, island.getIslandType().toString());
            stm.setString(4, island.getOwner().getName());
            stm.executeUpdate();

            playersDAO.updateMembers(island.getOwner(), island.getMembers());
            minionsDAO.updateMinions(island.getMinions());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Location getLocation(String location){
        if(location == null){
            return null;
        }
        String[] locations = location.split(",");
        World world= Bukkit.getWorld(locations[0]);
        int x = Integer.parseInt(locations[1]);
        int y = Integer.parseInt(locations[2]);
        int z = Integer.parseInt(locations[3]);
        return new Location(world, x, y, z);
    }

    public String setLocation(Location location){
        return  location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    private Connection getConnection(){
        return getConnection("island.db");
    }

}
