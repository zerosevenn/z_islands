package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLContainer;
import me.zeroseven.island.island.Island;
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
        String sql = "CREATE TABLE IF NOT EXISTS ISLAND(owner VARCHAR(36) PRIMARY KEY, spawnLocation STRING, location STRING)";
        try(Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertIsland(Island island){
        String sql = "INSERT INTO ISLAND(owner, spawnLocation, location) VALUES (?,?,?)";
        try(Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(0, island.getOwner().getName());
            stm.setString(1, setLocation(island.getSpawnLocation()));
            stm.setString(2, setLocation(island.getLocation()));
            stm.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Island getIsland(Player owner){
        String sql = "SELECT * FROM ISLAND";
        try(Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)){
            ResultSet rs = stm.executeQuery();
            String spawnLocationString = rs.getString("spawnLocation");
            String locationString = rs.getString("location");
            Location spawnLocation = getLocation(spawnLocationString);
            Location location = getLocation(locationString);
            return new Island(
                    spawnLocation, location, owner,
                    playersDAO.getMembers(owner),
                    minionsDAO.selectMinionsByOwner(owner.getUniqueId())
            );
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public Location getLocation(String location){
        String[] locations = location.split(",");
        World world= Bukkit.getWorld(locations[0]);
        int x = Integer.parseInt(locations[1]);
        int y = Integer.parseInt(locations[2]);
        int z = Integer.parseInt(locations[3]);
        return new Location(world, x, y, z);
    }

    public String setLocation(Location location){
        return  location.getWorld() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getZ();
    }

    private Connection getConnection(){
        return getConnection("island.db");
    }

}
