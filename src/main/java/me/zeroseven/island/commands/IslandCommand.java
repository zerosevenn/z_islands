package me.zeroseven.island.commands;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandLoader;
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

    public IslandCommand(IslandPlugin instance) {
        this.islandLoader = new IslandLoader(instance);
        this.islandBuffer = IslandPlugin.getBuffer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!cmd.getName().equalsIgnoreCase("island")){
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        Location playerLocation = player.getLocation();

        Location islandLocation = new Location(playerLocation.getWorld(), playerLocation.getX() + 58.00,
                playerLocation.getY() + 70.00, playerLocation.getZ() + 31);

        Island island = new Island(islandLocation, playerLocation, player, new ArrayList<>(), new ArrayList<>());
        islandBuffer.updatePlayerIsland(player, island);

        try {
            islandLoader.loadSchematic("positionisland.schem", player.getWorld(), player.getLocation(), player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(ChatColor.YELLOW + "Island placed sucessfully!");
        player.teleport(islandLocation);

        return false;
    }


}
