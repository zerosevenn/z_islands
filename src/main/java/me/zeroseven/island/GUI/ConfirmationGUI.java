package me.zeroseven.island.GUI;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.Buffer;

public class ConfirmationGUI {

    public static Inventory createInventory(OfflinePlayer player, ShopItem shopItem){
        Inventory inv = Bukkit.createInventory(new ConfirmationGUIHolder(player, shopItem), 3*9, "§6Confirm Purchase");

        ItemStack panel = icon(Material.GRAY_STAINED_GLASS_PANE, ChatColor.BLACK + "");
        ItemStack confirm = icon(Material.GREEN_WOOL, "§aClick to confirm");
        ItemStack cancel = icon(Material.RED_WOOL, "§cClick to cancel");

        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, panel);
        }

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

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
