package me.zeroseven.island.GUI;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StockGUIHolder implements InventoryHolder {

    private OfflinePlayer player;

    private List<ShopItem> stockItems;

    public StockGUIHolder(OfflinePlayer player, List<ShopItem> stockItems) {
        this.player = player;
        this.stockItems = stockItems;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public List<ShopItem> getStockItems() {
        return stockItems;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}
