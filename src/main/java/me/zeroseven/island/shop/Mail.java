package me.zeroseven.island.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Mail {

    private Player owner;
    private Inventory inventory;

    public Mail(Player owner) {
        this.owner = owner;
        this.inventory = Bukkit.createInventory(owner, 9*5, "§6Items Mail");
    }

    public Player getOwner() {
        return owner;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
