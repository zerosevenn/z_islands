package me.zeroseven.island.GUI.shop;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MailGUIHolder implements InventoryHolder {
    OfflinePlayer p;
    Inventory inv;

    List<ShopItem> items;

    public MailGUIHolder(OfflinePlayer p, List<ShopItem> items) {
        this.p = p;
        this.items = items;
    }

    public OfflinePlayer getOfflinePlayer() {
        return p;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
