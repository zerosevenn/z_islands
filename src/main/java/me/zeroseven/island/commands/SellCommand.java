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

                    // Obtenha a localização do jogador
                    Location location = player.getLocation().add(2, 1, 0);
                    Location blockLocation = location.clone().add(0, -1, 0);

                    // Gera o Bloco de Barril
                    blockLocation.getBlock().setType(Material.BARREL);

                    // Coloque o bloco de vidro acima do Bloco de Barril
                    blockLocation.clone().add(0, 1, 0).getBlock().setType(Material.GLASS);

                    // Obtenha o item que o jogador está segurando
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "Você precisa estar segurando um item para criar uma loja.");
                        return true;
                    }

                    // Crie um item flutuante acima do bloco de vidro
                    World world = player.getWorld();
                    // Definindo a posição central exata do bloco de vidro
                    Location itemLocation = blockLocation.clone().add(0,1,0);
                    Item droppedItem = world.dropItem(itemLocation, itemInHand.clone());
                    droppedItem.setVelocity(new Vector(0, 0, 0));
                    droppedItem.setPickupDelay(Integer.MAX_VALUE); // Prevenir a coleta do item
                    droppedItem.setGravity(false);
                    droppedItem.setCanMobPickup(false);
                    droppedItem.setCanPlayerPickup(false);
                    droppedItem.setCustomNameVisible(false); // Oculta o nome do item
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

                    // Remova o item da mão do jogador
                    player.getInventory().setItemInMainHand(null);

                    // Adicione o item à lista de itens da loja
                    shopItems.add(droppedItem);

                    // Crie hologramas
                    createHolograms(location.clone().add(0, 0.5, 0), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Item: " + itemInHand.getType().name(), net.md_5.bungee.api.ChatColor.of("#FFD700") + "Price: " + price);

                    player.sendMessage(ChatColor.GREEN + "Você criou uma loja com o preço: " + ChatColor.GOLD + price);

                } catch (NumberFormatException e) {
                    if(args[0].equalsIgnoreCase("mail")){

                    }
                    player.sendMessage(ChatColor.RED + "Por favor, insira um valor numérico válido.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
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
            armorStand.setMarker(true); // Opcional: torna o hitbox muito pequeno e não interativo
            location.add(0, 0.3, 0); // Ajusta a distância vertical entre os hologramas
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
