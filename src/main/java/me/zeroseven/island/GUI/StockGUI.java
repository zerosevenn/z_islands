package me.zeroseven.island.GUI;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class StockGUI {

    public static Inventory createInventory(OfflinePlayer p, List<ShopItem> stockItems){
        Inventory inv = Bukkit.createInventory(new StockGUIHolder(p, stockItems), 4*9, "§6Stock");
        int i = 0;
        if(stockItems == null){
            return inv;
        }
        for(ShopItem shopItem : stockItems){
            inv.setItem(i, shopItem.getItemStack());
            i++;
        }
        return inv;
    }

}
