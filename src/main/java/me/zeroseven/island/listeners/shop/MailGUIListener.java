package me.zeroseven.island.listeners.shop;

import me.zeroseven.island.GUI.shop.MailGUIHolder;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.shop.Market;
import me.zeroseven.island.shop.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class MailGUIListener implements Listener {

    private Market market;

    public MailGUIListener(IslandPlugin instance) {
        market = new Market(instance);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getInventory().getHolder() instanceof MailGUIHolder mailGUIHolder)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        List<ShopItem> items = mailGUIHolder.getItems();
        items.stream()
                .filter(shopItem -> event.getCurrentItem() != null && event.getCurrentItem().isSimilar(shopItem.getItemStack()))
                .findFirst()
                .ifPresent(shopItem -> {
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(shopItem.getItemStack());
                        market.getMailItems(player).remove(shopItem);
                    }
                });
    }

}
