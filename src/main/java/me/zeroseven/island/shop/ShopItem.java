package me.zeroseven.island.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopItem {

    private String id;
    private Player owner;
    private ItemStack itemStack;
    private int value;

    public ShopItem(Player owner, ItemStack itemStack, int value) {
        this.owner = owner;
        this.itemStack = itemStack;
        this.value = value;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Player getOwner() {
        return owner;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getValue() {
        return value;
    }
}
