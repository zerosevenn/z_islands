package me.zeroseven.island.GUI;

import me.zeroseven.island.minions.Minion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeGUI {
	
	public static Inventory createUpgradeGUI(Player p, Minion m) {
		
		Inventory inv = Bukkit.createInventory(new UpgradeGUIHolder(p, m), 54, ChatColor.GREEN + "Upgrade Minion");
		
		ItemStack white = icon(Material.WHITE_STAINED_GLASS_PANE, ChatColor.BLACK + "");
		ItemStack back = icon(Material.ARROW, ChatColor.RED + "<<< Back");
		ItemStack barrier = icon(Material.BARRIER, ChatColor.RED + "Exit");
		ItemStack craftTable = icon(Material.CRAFTING_TABLE, ChatColor.YELLOW + "Upgrade Minions!");
		
		for (int i = 0; i < 54; i++) {
			inv.setItem(i, white);
		}
		
		inv.setItem(23, craftTable);
//		inv.setItem(25, new ItemStack(Material.AIR));
		inv.setItem(25, new ItemStack(Material.RED_WOOL));
		inv.setItem(48, back);
		inv.setItem(49, barrier);
		
		inv.setItem(10, new ItemStack(Material.AIR));
		inv.setItem(11, new ItemStack(Material.AIR));
		inv.setItem(12, new ItemStack(Material.AIR));
		
		inv.setItem(19, new ItemStack(Material.AIR));
		inv.setItem(20, new ItemStack(Material.AIR));
		inv.setItem(21, new ItemStack(Material.AIR));
		
		inv.setItem(28, new ItemStack(Material.AIR));
		inv.setItem(29, new ItemStack(Material.AIR));
		inv.setItem(30, new ItemStack(Material.AIR));
		
		return inv;
	}
	
	private static ItemStack icon(Material mat, String displayName) {
		
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}


}
