package me.zeroseven.island.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sk89q.worldedit.world.block.BlockType;
import me.zeroseven.island.database.operator.MySQLProvider;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MinionsDAO extends MySQLProvider {
    public MinionsDAO(FileConfiguration config) {
        super(config);
    }

    public void createTable(){
        String sql = "CREATE TABLE Minion (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    type VARCHAR(50)," +
                "    location_x DOUBLE," +
                "    location_y DOUBLE," +
                "    location_z DOUBLE," +
                "    world_name VARCHAR(100)," +
                "    owner_uuid VARCHAR(36)," +
                "    level INT," +
                "    experience DOUBLE," +
                "    drops TEXT," +
                "    FOREIGN KEY (owner_uuid) REFERENCES Owner(uuid)" +
                ");";
        try(Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void insertMinions(List<Minion> minions) {
        String sql = "INSERT INTO Minion (type, location_x, location_y, location_z, world_name, owner_uuid, level, experience, drops) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Minion minion : minions) {
                preparedStatement.setString(1, minion.getType().toString());
                preparedStatement.setDouble(2, minion.getLocation().getX());
                preparedStatement.setDouble(3, minion.getLocation().getY());
                preparedStatement.setDouble(4, minion.getLocation().getZ());
                preparedStatement.setString(5, minion.getLocation().getWorld().getName());
                preparedStatement.setString(6, minion.getOwnerID().toString());
                preparedStatement.setInt(7, minion.getLevel());
                preparedStatement.setDouble(8, minion.getExperience());
                preparedStatement.setString(9, serializeDrops(minion.getDrops()));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Minion> selectMinionsByOwner(UUID ownerUuid) {
        String sql = "SELECT * FROM Minion WHERE owner_uuid = ?";
        List<Minion> minions = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            switch (resultSet.getString("type")){
                case "block" ->
                    minions.add(new BlockMinion(
                            resultSet.getInt("id"),
                            MinionType.fromString(resultSet.getString("type")),
                            deserializeDrops(resultSet.getString("drops")),
                            new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("z"), resultSet.getDouble("y"), resultSet.getDouble("x")),
                            ownerUuid,
                            resultSet.getInt("level"),
                            resultSet.getDouble("experience"),
                            new ItemStack(Material.AIR)));
                case "crop" ->
                        minions.add(new CropMinion(
                            resultSet.getInt("id"),
                            MinionType.fromString(resultSet.getString("type")),
                            deserializeDrops(resultSet.getString("drops")),
                            new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("z"), resultSet.getDouble("y"), resultSet.getDouble("x")),
                            ownerUuid,
                            resultSet.getInt("level"),
                            resultSet.getDouble("experience"),
                            new ItemStack(Material.AIR)));
                case "mob" ->
                    minions.add(new MobMinion(
                            resultSet.getInt("id"),
                            MinionType.fromString(resultSet.getString("type")),
                            deserializeDrops(resultSet.getString("drops")),
                            new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("z"), resultSet.getDouble("y"), resultSet.getDouble("x")),
                            ownerUuid,
                            resultSet.getInt("level"),
                            resultSet.getDouble("experience"),
                            new ItemStack(Material.AIR)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return minions;
    }

    private String serializeDrops(ItemStack[] drops) {
        Gson gson = new Gson();
        return gson.toJson(drops);
    }

    private List<ItemStack> deserializeDrops(String drops) {
        Gson gson = new Gson();
        Type itemStackListType = new TypeToken<List<ItemStack>>() {}.getType();
        return gson.fromJson(drops, itemStackListType);
    }

}
