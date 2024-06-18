package me.zeroseven.island.GUI.shop;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MailGUI  {

    public static Inventory createInventory(OfflinePlayer player, List<ShopItem> items){
        Inventory inv = Bukkit.createInventory(new MailGUIHolder(player, items), 27, "§5Mail");
        int i = 0;
        for(ShopItem shopItem : items){
            ItemStack is = shopItem.getItemStack();
            inv.setItem(i, is);
            i++;
        }
        return inv;
    }


}
