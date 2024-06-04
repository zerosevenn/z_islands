package me.zeroseven.island.GUI;

import me.zeroseven.island.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IslandThemeGUI {

    public static Inventory createInventory(OfflinePlayer player, Island island){
        Inventory inv = Bukkit.createInventory(new IslandGUIHolder(player, island), 45, "Select Theme");
        ItemStack pane = icon(Material.GRAY_STAINED_GLASS_PANE, ChatColor.BLACK + "");
        ItemStack black = icon(Material.LEGACY_IRON_FENCE, ChatColor.BLACK + "");
        ItemStack islandDesert = icon(Material.SAND, ChatColor.GOLD + "Select Desert Theme", Arrays.asList("§7Click to select"));
        ItemStack islandMedieval = icon(Material.MOSSY_STONE_BRICKS, ChatColor.GOLD + "Select Medieval Theme", Arrays.asList("§7Click to select"));
        ItemStack islandNether = icon(Material.NETHER_BRICKS, ChatColor.GOLD + "Select Nether Theme", Arrays.asList("§7Click to select"));
        ItemStack islandJungle = icon(Material.JUNGLE_WOOD, ChatColor.GOLD + "Select Jungle Theme", Arrays.asList("§7Click to select"));
        ItemStack islandDefault = icon(Material.CYAN_TERRACOTTA, ChatColor.GOLD + "Select Default Theme", Arrays.asList("§7Click to select"));

        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, pane);
        }


        for (int i = 9; i < 36 + 18; i+=9) {
            inv.setItem(i-1, black);
        }

        for (int i = 1; i < 27 +18; i+=9) {
            inv.setItem(i-1, black);
        }

        inv.setItem(30, islandDesert);
        inv.setItem(11, islandMedieval);
        inv.setItem(13, islandNether);
        inv.setItem(15, islandJungle);
        inv.setItem(32, islandDefault);

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
