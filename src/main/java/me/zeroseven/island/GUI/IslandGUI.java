package me.zeroseven.island.GUI;

import me.zeroseven.island.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandGUI {

    public static Inventory CreateInventory(OfflinePlayer player, Island island){
        Inventory inv = Bukkit.createInventory(new IslandGUIHolder(player, island), 27, "Island");
        ItemStack pane = icon(Material.WHITE_STAINED_GLASS_PANE, ChatColor.BLACK + "");




        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, pane);
        }

        return inv;
    }

    private static ItemStack icon(Material mat, String displayName) {

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }

}
