package me.zeroseven.island.commands;

import me.zeroseven.island.GUI.island.IslandGUI;
import me.zeroseven.island.GUI.island.IslandThemeGUI;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.config.IslandConfiguration;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.island.IslandType;
import me.zeroseven.island.nms.SchematicLoader;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IslandCommand implements CommandExecutor {



    private SchematicLoader islandLoader;
    private IslandBuffer islandBuffer;
    private FileConfiguration islandConfiguration;

    public IslandCommand(IslandPlugin instance) {
        this.islandLoader = new SchematicLoader(instance);
        this.islandBuffer = IslandPlugin.getIslandBuffer();
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
                if(island.getLocation() == null){
                    return false;
                }
                Location islandLocation =island.getSpawnLocation();
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

            if (args[0].equalsIgnoreCase("theme")) {
                Island island = islandBuffer.getPlayerIsland(player);

                if (island == null) {
                    player.sendMessage(ChatColor.RED + "You are not on a Island!");
                    return false;
                }
                player.openInventory(IslandThemeGUI.createInventory(player, island));
            }
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("load")){
                IslandType type = IslandType.valueOf(args[1].toUpperCase());
                Location islandLocation = player.getLocation();
                System.out.println("x: " + islandLocation.getX() + " y: " + islandLocation.getY() + "z: " + islandLocation.getZ());
                Island island = new Island(islandLocation.clone(), islandLocation.clone(), player, new ArrayList<>(), new ArrayList<>(),type);
                islandBuffer.updatePlayerIsland(player, island);


                SchematicLoader.loadIslandByType(player, islandLocation, type);



                player.sendMessage(ChatColor.YELLOW + "Island placed sucessfully!");

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
