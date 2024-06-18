package me.zeroseven.island.shop;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.database.MarketDAO;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Market {



    private List<ShopItem> sellingItems;

    private List<ShopItem> mailItems;

    private MarketDAO marketDAO;

    public Market(IslandPlugin instance) {
        this.sellingItems = new ArrayList<>();
        this.mailItems = new ArrayList<>();
        marketDAO = new MarketDAO(instance);
    }

    public void save(){
        marketDAO.insertMailItems(mailItems);
        marketDAO.insertSellingItems(sellingItems);
    }

    public void load(){
        sellingItems = marketDAO.getSellingItems();
        mailItems = marketDAO.getMailItems();
    }

    public List<ShopItem> getMailItems(Player player){
        List<ShopItem> shopItems = new ArrayList<>();
        for(ShopItem item : mailItems){
            if(item.getOwner().getName().equalsIgnoreCase(player.getName()) ){
                shopItems.add(item);
            }
        }
        return shopItems;
    }
    public List<ShopItem> getSellingItems(Player player){
        List<ShopItem> shopItems = new ArrayList<>();
        for(ShopItem item : sellingItems){
            if(item.getOwner().getName().equalsIgnoreCase(player.getName()) ){
                shopItems.add(item);
            }
        }
        return shopItems;
    }

    public void removeItem(ShopItem shopItem){
        sellingItems.remove(shopItem);
        mailItems.add(shopItem);
    }

    public void buyItem(Player player, ShopItem shopItem){
        sellingItems.remove(shopItem);
        shopItem.setOwner(player);
        mailItems.add(shopItem);
    }

    public void sellItem(ShopItem shopItem){
        sellingItems.add(shopItem);
    }





}
