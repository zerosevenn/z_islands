package me.zeroseven.island.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopItem {

    private String id;
    private Player owner;
    private Player buyer;
    private ItemStack itemStack;
    private int value;

    public ShopItem(String id, Player owner, Player buyer, ItemStack itemStack, int value) {
        this.id = id;
        this.owner = owner;
        this.buyer = buyer;
        this.itemStack = itemStack;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getBuyer() {
        return buyer;
    }

    public void setBuyer(Player buyer) {
        this.buyer = buyer;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
