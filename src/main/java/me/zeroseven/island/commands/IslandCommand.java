package me.zeroseven.island.commands;

import me.zeroseven.island.IslandPlugin;
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

public class IslandCommand implements CommandExecutor {



    private IslandLoader islandLoader;

    public IslandCommand(IslandPlugin instance) {
        this.islandLoader = new IslandLoader(instance);
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

        try {
            islandLoader.loadSchematic("positionisland.schem", player.getWorld(), player.getLocation(), player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(ChatColor.YELLOW + "Island placed sucessfully!");
        teleport(player);

        return false;
    }

    public void teleport(Player player){
        Location playerLocation = player.getLocation();
        Location  islandLocation = new Location(playerLocation.getWorld(), playerLocation.getX() + 58.00,
                playerLocation.getY() + 70.00, playerLocation.getZ() + 31);
        player.teleport(islandLocation);
    }


}
