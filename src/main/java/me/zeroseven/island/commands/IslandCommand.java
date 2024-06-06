package me.zeroseven.island.commands;

import me.zeroseven.island.GUI.IslandGUI;
import me.zeroseven.island.GUI.IslandThemeGUI;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandLoader;
import me.zeroseven.island.nms.PacketBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class IslandCommand implements CommandExecutor {



    private IslandLoader islandLoader;
    private IslandBuffer islandBuffer;
    private PacketBlockManager packetBlockManager;

    public IslandCommand(IslandPlugin instance) {
        this.islandLoader = new IslandLoader(instance);
        this.islandBuffer = IslandPlugin.getIslandBuffer();
        this.packetBlockManager = IslandPlugin.getBlockManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!cmd.getName().equalsIgnoreCase("island")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            Island island = islandBuffer.getPlayerIsland(player);
            if (island == null) {
                player.sendMessage(ChatColor.RED + "You are not on a Island!");
                return false;
            }
            player.openInventory(IslandGUI.CreateInventory(player, island));
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                Location playerLocation = player.getLocation();

                Location islandLocation = new Location(playerLocation.getWorld(), playerLocation.getX() + 58.00,
                        playerLocation.getY() + 70.00, playerLocation.getZ() + 31);

                Island island = new Island(islandLocation, playerLocation, player, new ArrayList<>(), new ArrayList<>());
                islandBuffer.updatePlayerIsland(player, island);

                islandLoader.loadSchematic("positionisland.schem", player.getWorld(), player.getLocation(), player);

                packetBlockManager.getBlockSet().put(player.getUniqueId(), islandLoader.getVisibleBlocks());

                player.sendMessage(ChatColor.YELLOW + "Island placed sucessfully!");
                player.teleport(islandLocation);

            }

            if (args[0].equalsIgnoreCase("theme")) {
                Island island = islandBuffer.getPlayerIsland(player);
                if (island == null) {
                    player.sendMessage(ChatColor.RED + "You are not on a Island!");
                    return false;
                }
                player.openInventory(IslandThemeGUI.createInventory(player, island));
            }
        }
        return false;
    }



}
