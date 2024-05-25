package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLContainer;
import me.zeroseven.island.database.operator.MySQLProvider;
import me.zeroseven.island.islands.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class IslandDAO extends MySQLProvider {
    public IslandDAO(FileConfiguration config) {
        super(config);
    }

    public void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ISLAND(owner VARCHAR(36), spawnLocation STRING, location STRING)";
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

}
