package me.zeroseven.island.commands;

import me.zeroseven.island.GUI.shop.MailGUI;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.shop.Market;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShopCommand implements CommandExecutor {
    private Market market;

    public ShopCommand(IslandPlugin islandPlugin) {
        market = new Market(islandPlugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;

       if(cmd.getName().equalsIgnoreCase("mail")){
           Inventory inv = MailGUI.createInventory(player, market.getMailItems(player));
           player.openInventory(inv);
           return true;
        }

        return false;
    }








}
