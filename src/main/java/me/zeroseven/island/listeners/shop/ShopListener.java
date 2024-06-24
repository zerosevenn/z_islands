package me.zeroseven.island.listeners.shop;

import me.zeroseven.island.GUI.ConfirmationGUI;
import me.zeroseven.island.GUI.ConfirmationGUIHolder;
import me.zeroseven.island.GUI.StockGUI;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.shop.Market;
import me.zeroseven.island.shop.ShopItem;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {


    private Market market;

    public ShopListener() {
        market = IslandPlugin.getMarket();
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        if(!(event.getInventory().getHolder() instanceof ConfirmationGUIHolder)){
            return;
        }

        ConfirmationGUIHolder confirmationGUIHolder = (ConfirmationGUIHolder) event.getClickedInventory().getHolder();

        if(event.getRawSlot() == 15){
            player.closeInventory();
        }else if(event.getRawSlot() == 11){
            ShopItem shopItem = confirmationGUIHolder.getShopItem();
            market.removeItem(shopItem);
            player.getInventory().addItem(shopItem.getItemStack());
            player.sendMessage("§aYou have successfully purchased the item!");
            player.closeInventory();
        }

    }
    @EventHandler
    public void onPlayerInteract(InventoryOpenEvent event){

        Player player = (Player) event.getPlayer();

        if(!(event.getInventory().getHolder() instanceof Barrel)){
            return;
        }

        event.setCancelled(true);
        player.closeInventory();

        Location location = ((Barrel) event.getInventory().getHolder()).getLocation().clone().add(0,1,0);

        if(location.getBlock().getType() != Material.GLASS){
            return;
        }

        for (Item item : location.getWorld().getEntitiesByClass(Item.class)) {
            if (item.getLocation().distance(location) <= 1) {
                ItemStack itemStack = item.getItemStack();

                System.out.println("Item: " + itemStack.getType());

                if(market.getSellingItems() == null){
                    return;
                }



                for (ShopItem si : market.getSellingItems()) {
                    System.out.println("ItemStack: " + si.getItemStack().getType());
                    System.out.println("Owner: " + si.getOwner());
                    System.out.println("Buyer: " + si.getBuyer());
                    if(si.getItemStack().isSimilar(itemStack)) {
                        if(player.equals(si.getOwner())){
                            Inventory inv = StockGUI.createInventory(player, market.getStockItems(1));
                            player.openInventory(inv);
                            return;
                        }

                        player.openInventory(ConfirmationGUI.createInventory(player, si));
                        return;
                    }
                }
            }
        }

        System.out.println("returning 3");


    }



}
