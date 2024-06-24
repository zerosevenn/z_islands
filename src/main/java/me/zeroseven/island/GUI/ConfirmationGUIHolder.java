package me.zeroseven.island.GUI;

import me.zeroseven.island.shop.ShopItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ConfirmationGUIHolder implements InventoryHolder {
    private OfflinePlayer player;
    private ShopItem shopItem;

    public ConfirmationGUIHolder(OfflinePlayer player, ShopItem shopItem) {
        this.player = player;
        this.shopItem = shopItem;
        System.out.println("Holder: " + shopItem.getOwner());
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
