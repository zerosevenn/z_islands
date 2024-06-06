package me.zeroseven.island.GUI;

import me.zeroseven.island.config.MenuConfiguration;
import me.zeroseven.island.config.other.ConfigLoader;
import me.zeroseven.island.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class IslandGUI {

    public static Inventory CreateInventory(OfflinePlayer player, Island island){
        ConfigLoader configLoader = new ConfigLoader(new MenuConfiguration((JavaPlugin) Bukkit.getPluginManager().getPlugin("zIsland")).getConfiguration());
        Inventory inv = Bukkit.createInventory(new IslandGUIHolder(player, island), 27, "Island");
        ItemStack pane = configLoader.getItemStack("Island.Background");
        ItemStack theme = configLoader.getItemStack("Island.Theme");
        ItemStack minions = configLoader.getItemStack("Island.Minions");
        ItemStack shops = configLoader.getItemStack("Island.Shops");

        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, pane);
        }

        inv.setItem(10, theme);
        inv.setItem(13, minions);
        inv.setItem(16, shops);

        return inv;
    }

}
