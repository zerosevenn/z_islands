package me.zeroseven.island.commands;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.minions.MinionSpawner;
import me.zeroseven.island.minions.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!cmd.getName().equalsIgnoreCase("minion")) {
			return false;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /minion <player> <type>");
			return true;
		}

		if (!player.hasPermission("minions.cmd")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Specified player not found!");
			return true;
		}

		MinionType type = MinionType.fromString(args[1].toUpperCase());

		if (type == null) {
			sender.sendMessage(ChatColor.RED + "Invalid Minion Type!");
			sender.sendMessage(ChatColor.YELLOW + "Available types: " + ChatColor.AQUA + "blocks, crops, and mobs");
			return true;
		}

		MinionSpawner spawner = new MinionSpawner(++IslandPlugin.TOTAL, type);

		player.sendMessage(ChatColor.GREEN + "Minion spawner given!");
		target.sendMessage(ChatColor.GREEN + "You have received a Minion Spawner of type " + type.toString() + ".");

		if (target.getInventory().firstEmpty() == -1) {
			target.getWorld().dropItem(target.getLocation(), spawner.getItemStack());
			target.sendMessage(ChatColor.RED + "Since your inventory is full, the spawner has been dropped on the ground near you.");
		} else {
			target.getInventory().addItem(spawner.getItemStack());
			target.updateInventory();
		}

		return true;
	}
}
