package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLProvider;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayersDAO extends MySQLProvider {
    public PlayersDAO(FileConfiguration config) {
        super(config);
    }


}

