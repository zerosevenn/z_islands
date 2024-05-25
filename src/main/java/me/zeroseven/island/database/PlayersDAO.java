package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayersDAO extends MySQLProvider {

    public PlayersDAO(FileConfiguration config) {
        super(config);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS members (" +
                "member_id INT AUTO_INCREMENT PRIMARY KEY," +
                "owner VARCHAR(36)," +
                "member_name VARCHAR(255) NOT NULL," +
                "FOREIGN KEY (owner) REFERENCES ISLAND(owner))";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertMembers(Player player, List<Player> members) {
        String ownerUUID = player.getUniqueId().toString();
        String sql = "INSERT INTO members (owner, member_name) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement memberStatement = conn.prepareStatement(sql)) {
            for (Player member : members) {
                memberStatement.setString(1, ownerUUID);
                memberStatement.setString(2, member.getName());
                memberStatement.addBatch();
            }
            memberStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Player> getMembers(Player player) {
        String ownerUUID = player.getUniqueId().toString();
        List<Player> members = new ArrayList<>();
        String sql = "SELECT member_name FROM members WHERE owner = ?";

        try (Connection conn = getConnection();
             PreparedStatement memberStatement = conn.prepareStatement(sql)) {
            memberStatement.setString(1, ownerUUID);
            try (ResultSet memberResult = memberStatement.executeQuery()) {
                while (memberResult.next()) {
                    String memberName = memberResult.getString("member_name");
                    Player member = Bukkit.getPlayer(memberName);
                    if (member != null) {
                        members.add(member);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public void deleteMember(Player player, Player member) {
        String ownerUUID = player.getUniqueId().toString();
        String memberName = member.getName();
        String sql = "DELETE FROM members WHERE owner = ? AND member_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement memberStatement = conn.prepareStatement(sql)) {
            memberStatement.setString(1, ownerUUID);
            memberStatement.setString(2, memberName);
            memberStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



