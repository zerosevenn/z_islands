package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLContainer;
import me.zeroseven.island.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketDAO extends MySQLContainer {

    public MarketDAO(JavaPlugin instance) {
        super(instance);
        createTables();
    }

    private Connection getConnection() {
        return getConnection("market.db");
    }

    private void createTables() {
        String createSellingItemsTable = "CREATE TABLE IF NOT EXISTS sellingItems (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "owner VARCHAR(36)," +
                "buyer VARCHAR(36)," +
                "itemStack BLOB," +
                "value INT" +
                ");";

        String createMailItemsTable = "CREATE TABLE IF NOT EXISTS mailItems (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "owner VARCHAR(36)," +
                "buyer VARCHAR(36)," +
                "itemStack BLOB," +
                "value INT" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSellingItemsTable);
            stmt.execute(createMailItemsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert single item into sellingItems
    public void insertSellingItem(ShopItem item) {
        if (itemExists("sellingItems", item.getId())) {
            return;
        }

        String query = "INSERT INTO sellingItems (id, owner, buyer, itemStack, value) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getOwner().getUniqueId().toString());
            pstmt.setString(3, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
            pstmt.setBytes(4, serializeItemStack(item.getItemStack()));
            pstmt.setInt(5, item.getValue());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert single item into mailItems
    public void insertMailItem(ShopItem item) {
        if (itemExists("mailItems", item.getId())) {
            return;
        }

        String query = "INSERT INTO mailItems (id, owner, buyer, itemStack, value) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getOwner().getUniqueId().toString());
            pstmt.setString(3, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
            pstmt.setBytes(4, serializeItemStack(item.getItemStack()));
            pstmt.setInt(5, item.getValue());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert list of items into sellingItems
    public void insertSellingItems(List<ShopItem> items) {
        String query = "INSERT INTO sellingItems (id, owner, buyer, itemStack, value) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (ShopItem item : items) {
                if (itemExists("sellingItems", item.getId())) {
                    continue;
                }
                pstmt.setString(1, item.getId());
                pstmt.setString(2, item.getOwner().getUniqueId().toString());
                pstmt.setString(3, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
                pstmt.setBytes(4, serializeItemStack(item.getItemStack()));
                pstmt.setInt(5, item.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert list of items into mailItems
    public void insertMailItems(List<ShopItem> items) {
        String query = "INSERT INTO mailItems (id, owner, buyer, itemStack, value) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (ShopItem item : items) {
                if (itemExists("mailItems", item.getId())) {
                    continue;
                }
                pstmt.setString(1, item.getId());
                pstmt.setString(2, item.getOwner().getUniqueId().toString());
                pstmt.setString(3, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
                pstmt.setBytes(4, serializeItemStack(item.getItemStack()));
                pstmt.setInt(5, item.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get list of all selling items
    public List<ShopItem> getSellingItems() {
        List<ShopItem> items = new ArrayList<>();
        String query = "SELECT * FROM sellingItems";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ShopItem item = new ShopItem(
                        rs.getString("id"),
                        getPlayerFromUUID(UUID.fromString(rs.getString("owner"))),
                        rs.getString("buyer") != null ? getPlayerFromUUID(UUID.fromString(rs.getString("buyer"))) : null,
                        deserializeItemStack(rs.getBytes("itemStack")),
                        rs.getInt("value")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Get list of all mail items
    public List<ShopItem> getMailItems() {
        List<ShopItem> items = new ArrayList<>();
        String query = "SELECT * FROM mailItems";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ShopItem item = new ShopItem(
                        rs.getString("id"),
                        getPlayerFromUUID(UUID.fromString(rs.getString("owner"))),
                        rs.getString("buyer") != null ? getPlayerFromUUID(UUID.fromString(rs.getString("buyer"))) : null,
                        deserializeItemStack(rs.getBytes("itemStack")),
                        rs.getInt("value")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Get list of selling items by player
    public List<ShopItem> getSellingItems(Player player) {
        List<ShopItem> items = new ArrayList<>();
        String query = "SELECT * FROM sellingItems WHERE owner = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShopItem item = new ShopItem(
                            rs.getString("id"),
                            player,
                            rs.getString("buyer") != null ? getPlayerFromUUID(UUID.fromString(rs.getString("buyer"))) : null,
                            deserializeItemStack(rs.getBytes("itemStack")),
                            rs.getInt("value")
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Get list of mail items by player
    public List<ShopItem> getMailItems(Player player) {
        List<ShopItem> items = new ArrayList<>();
        String query = "SELECT * FROM mailItems WHERE owner = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShopItem item = new ShopItem(
                            rs.getString("id"),
                            player,
                            rs.getString("buyer") != null ? getPlayerFromUUID(UUID.fromString(rs.getString("buyer"))) : null,
                            deserializeItemStack(rs.getBytes("itemStack")),
                            rs.getInt("value")
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Update single selling item
    public void updateSellingItem(ShopItem item) {
        String query = "UPDATE sellingItems SET owner = ?, buyer = ?, itemStack = ?, value = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getOwner().getUniqueId().toString());
            pstmt.setString(2, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
            pstmt.setBytes(3, serializeItemStack(item.getItemStack()));
            pstmt.setInt(4, item.getValue());
            pstmt.setString(5, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update single mail item
    public void updateMailItem(ShopItem item) {
        String query = "UPDATE mailItems SET owner = ?, buyer = ?, itemStack = ?, value = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getOwner().getUniqueId().toString());
            pstmt.setString(2, item.getBuyer() != null ? item.getBuyer().getUniqueId().toString() : null);
            pstmt.setBytes(3, serializeItemStack(item.getItemStack()));
            pstmt.setInt(4, item.getValue());
            pstmt.setString(5, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete single selling item
    public void deleteSellingItem(String id) {
        String query = "DELETE FROM sellingItems WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete single mail item
    public void deleteMailItem(String id) {
        String query = "DELETE FROM mailItems WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean itemExists(String tableName, String id) {
        String query = "SELECT 1 FROM " + tableName + " WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] serializeItemStack(ItemStack itemStack) {
        return itemStack.serializeAsBytes();
    }

    private ItemStack deserializeItemStack(byte[] bytes) {
        return ItemStack.deserializeBytes(bytes);
    }

    private Player getPlayerFromUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
