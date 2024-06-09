package me.zeroseven.island.database;

import me.zeroseven.island.database.operator.MySQLContainer;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopDAO extends MySQLContainer  {
    public ShopDAO(JavaPlugin instance) {
        super(instance);
    }

}
