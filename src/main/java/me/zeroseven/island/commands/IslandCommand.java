package me.zeroseven.island.commands;

import me.zeroseven.island.GUI.island.IslandGUI;
import me.zeroseven.island.GUI.island.IslandThemeGUI;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.config.IslandConfiguration;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandLoader;
import me.zeroseven.island.nms.PacketBlockManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IslandCommand implements CommandExecutor {



    private IslandLoader islandLoader;
    private IslandBuffer islandBuffer;
    private PacketBlockManager packetBlockManager;
    private FileConfiguration islandConfiguration;

    public IslandCommand(IslandPlugin instance) {
        this.islandLoader = new IslandLoader(instance);
        this.islandBuffer = IslandPlugin.getIslandBuffer();
        this.packetBlockManager = IslandPlugin.getBlockManager();
        this.islandConfiguration = new IslandConfiguration(instance).getConfiguration();
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

            Chunk playerChunk = player.getLocation().getChunk();
            World world = player.getWorld();

            if(args[0].equalsIgnoreCase("buffer")){
                Island island = islandBuffer.getPlayerIsland(player);
                Location islandLocation =island.getLocation();
                player.sendMessage(ChatColor.GREEN + "Loading buffer informations: "
                        , "Location: x-" + (int) islandLocation.getX() + ", y-" + (int) islandLocation.getY() + ", z-" + (int) islandLocation.getZ()
                        , "Owner: " + island.getOwner().getName());
                return true;
            }

            if(args[0].equalsIgnoreCase("refresh")){

                int west = -1;
                int south = 1;
                int east = 3;
                int north = -3;

                for (int x = west; x <= east; x++) {
                    for (int z = north; z <= south; z++) {
                        Chunk chunk = world.getChunkAt(playerChunk.getX() + x, playerChunk.getZ() + z);
                        world.refreshChunk(chunk.getX(), chunk.getZ());
                    }
                }

                player.sendMessage(ChatColor.GREEN + "Island clear.");
            }

            if(args[0].equalsIgnoreCase("chunkrefresh")){

                int viewDistance = player.getServer().getViewDistance();

                int west = -viewDistance;
                int east = viewDistance;
                int north = -viewDistance;
                int south = viewDistance;

                for (int x = west; x <= east; x++) {
                    for (int z = north; z <= south; z++) {
                        Chunk chunk = world.getChunkAt(playerChunk.getX() + x, playerChunk.getZ() + z);
                        world.refreshChunk(chunk.getX(), chunk.getZ());
                    }
                }
                player.sendMessage(ChatColor.GREEN + "Chunks visíveis recarregados.");
            }

            if(args[0].equalsIgnoreCase("load")){
                Location loc = player.getLocation().subtract(58, 70, 28);


                Island island = new Island(loc, loc, player, new ArrayList<>(), new ArrayList<>());
                islandBuffer.updatePlayerIsland(player, island);

                islandLoader.loadSchematic("positionisland.schem", loc, player);

                packetBlockManager.getBlockSet().put(player.getUniqueId(), islandLoader.getVisibleBlocks());

                player.sendMessage(ChatColor.YELLOW + "Island placed sucessfully!");

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

    public static Location getConfigLocation(FileConfiguration fileConfiguration, String path){
        int x = fileConfiguration.getInt(path + ".x");
        int y = fileConfiguration.getInt(path + ".y");
        int z = fileConfiguration.getInt(path + ".z");

        World world =  Bukkit.getWorld(fileConfiguration.getString("World"));
        return new Location(world, x, y, z);
    }



}
