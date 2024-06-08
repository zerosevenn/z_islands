package me.zeroseven.island.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.World;


import java.util.HashSet;
import java.util.Set;

public class SellCommand implements CommandExecutor, Listener {

    private final Set<Item> shopItems = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    double price = Double.parseDouble(args[0]);

                    // Get the player's location
                    Location location = player.getLocation();
                    Location blockLocation = location.clone().add(0, -1, 0);

                    // Generate the End Portal Frame
                    blockLocation.getBlock().setType(Material.END_PORTAL_FRAME);

                    // Place the glass block above the End Portal Frame
                    blockLocation.clone().add(0, 1, 0).getBlock().setType(Material.GLASS);

                    // Get the item the player is holding
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You need to be holding an item to create a shop.");
                        return true;
                    }

                    // Create a floating item above the glass block
                    World world = player.getWorld();
                    Item droppedItem = world.dropItem(blockLocation.clone().add(0, 1, 0), itemInHand.clone());
                    droppedItem.setVelocity(new Vector(0, 0, 0));
                    droppedItem.setPickupDelay(Integer.MAX_VALUE); // Prevent item pickup

                    // Schedule a task to repeatedly teleport the item to its original position
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (droppedItem.isValid()) {
                                droppedItem.teleport(blockLocation.clone().add(0, 1.0, 0));
                                droppedItem.setCanMobPickup(false);
                                droppedItem.setGravity(false);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 1L, 1L);

                    // Remove the item from the player's hand
                    player.getInventory().setItemInMainHand(null);

                    // Add the item to the shop items list
                    shopItems.add(droppedItem);

                    // Create holograms
                    createHolograms(location.clone().add(0, 0.5, 0), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Item: " + itemInHand.getType().name(), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Price: " + price);

                    player.sendMessage(ChatColor.GREEN + "You have created a shop with the price: " + ChatColor.GOLD + price);

                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Please enter a valid numeric value.");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "Correct usage: /sell <value>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
        return true;
    }

    private void createHolograms(Location location, String... lines) {
        for (String line : lines) {
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setCustomName(line);
            armorStand.setCustomNameVisible(true);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setMarker(true); // Optional: makes the hitbox very small and not interactable
            location.add(0, 0.3, 0); // Adjust vertical distance between holograms
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if (shopItems.contains(item)) {
            event.setCancelled(true);
        }
    }
}
