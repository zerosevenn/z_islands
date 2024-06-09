package me.zeroseven.island.GUI.island;

import me.zeroseven.island.config.MenuConfiguration;
import me.zeroseven.island.config.other.ConfigLoader;
import me.zeroseven.island.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class IslandThemeGUI {

    public static Inventory createInventory(OfflinePlayer player, Island island){


        ConfigLoader configLoader = new ConfigLoader(new MenuConfiguration((JavaPlugin) Bukkit.getPluginManager().getPlugin("zIsland")).getConfiguration());

        Inventory inv = Bukkit.createInventory(new IslandGUIHolder(player, island), 27, "Select Theme");
        ItemStack pane = configLoader.getItemStack("Island_Theme.Background");
        ItemStack black = configLoader.getItemStack("Island_Theme.Tapper");
        ItemStack islandDesert = configLoader.getItemStack("Island_Theme.IslandDesert");
        ItemStack islandMedieval = configLoader.getItemStack("Island_Theme.IslandMedieval");
        ItemStack islandNether = configLoader.getItemStack("Island_Theme.IslandNether");
        ItemStack islandJungle = configLoader.getItemStack("Island_Theme.IslandJungle");

        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, pane);
        }


        for (int i = 9; i < 36; i+=9) {
            inv.setItem(i-1, black);
        }

        for (int i = 1; i < 27; i+=9) {
            inv.setItem(i-1, black);
        }

        inv.setItem(14, islandDesert);
        inv.setItem(11, islandMedieval);
        inv.setItem(12, islandNether);
        inv.setItem(15, islandJungle);

        return inv;
    }


    private static ItemStack icon(Material mat, String displayName) {

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }
    private static ItemStack icon(Material mat, String displayName, List<String> lore) {

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }


}
