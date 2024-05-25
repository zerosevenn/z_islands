package me.zeroseven.island.database.operator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLProvider {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Connection connection;
    private final FileConfiguration fileConfiguration;

    public MySQLProvider(FileConfiguration config) {
        this.host = config.getString("Database.host");
        this.port = config.getInt("Database.port");
        this.database = config.getString("Database.database");
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.fileConfiguration = config;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        setConnection(DriverManager.getConnection("jdbc:mysql://"
                + host
                + ":"
                + port
                + "/"
                + database
                + "?useSSL=false", username, password));
    }

    public void disconnect() throws SQLException {
        connection.close();
        Bukkit.getConsoleSender().sendMessage("§a§lDisconnecting of database!");
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
