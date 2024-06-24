package me.zeroseven.island.commands;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.shop.Market;
import me.zeroseven.island.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.UUID;

public class SellCommand implements CommandExecutor, Listener {

    private final Set<Item> shopItems = new HashSet<>();
    Market market = IslandPlugin.getMarket();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    double price = Double.parseDouble(args[0]);

                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    ShopItem si = new ShopItem(UUID.randomUUID().toString(), player, player, itemInHand, 0);

                    if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You need to be holding an item to create a shop.");
                        return true;
                    }

                    Location location = new Location(player.getWorld(), (int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());
                    Location blockLocation = location.clone();

                    blockLocation.getBlock().setType(Material.BARREL);
                    blockLocation.clone().add(0, 1, 0).getBlock().setType(Material.GLASS);

                    World world = player.getWorld();
                    Location itemLocation = new Location(player.getWorld(), (int) blockLocation.getX() + 0.5, (int) blockLocation.getY() + 1, (int) blockLocation.getZ() + 0.5);
                    Item droppedItem = world.dropItem(itemLocation, itemInHand.clone());
                    droppedItem.setVelocity(new Vector(0, 0, 0));
                    droppedItem.setPickupDelay(Integer.MAX_VALUE);
                    droppedItem.setGravity(false);
                    droppedItem.setCanMobPickup(false);
                    droppedItem.setCanPlayerPickup(false);
                    droppedItem.setCustomNameVisible(false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (droppedItem.isValid()) {
                                droppedItem.teleport(itemLocation);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 1L, 1L);

                    player.getInventory().setItemInMainHand(null);


                    System.out.println("Seller: " + si.getOwner());

                    market.sellItem(si);
                    shopItems.add(droppedItem);

                    Location clone = itemLocation.clone().add(0, 0.7, 0);
                    createHolograms(clone.add(0, 0.3, 0), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Item: " + itemInHand.getType().name(), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Price: " + price);

                    player.sendMessage(ChatColor.GREEN + "You created a shop with the price: " + ChatColor.GOLD + price);

                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Please enter a valid numeric value.");
                }
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
            armorStand.setMarker(true);
            location.add(0, 0.3, 0);
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
