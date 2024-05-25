package me.zeroseven.island.GUI;

import me.zeroseven.island.minions.Minion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class MinionGUI {
	
	@SuppressWarnings("deprecation")
	public static Inventory createInventory(OfflinePlayer p, Minion m) {
		
		Inventory inv = Bukkit.createInventory(new MinionGUIHolder(p, m), 27, "Minion");
		
		ItemStack yellow = icon(Material.YELLOW_STAINED_GLASS_PANE, ChatColor.BLACK + "");
		ItemStack pane = icon(Material.WHITE_STAINED_GLASS_PANE, ChatColor.BLACK + "");
		ItemStack chest = icon(Material.ENDER_CHEST, ChatColor.GREEN + "Get Items");
		ItemStack bedrock = icon(Material.BEDROCK, ChatColor.RED + "Remove Minion");

		for(int i = 0; i < inv.getSize(); i++){
			inv.setItem(i, pane);
		}
		
		inv.setItem(13, chest);
		inv.setItem(15, bedrock);

		
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta sm = (SkullMeta) skull.getItemMeta();
		sm.setDisplayName("§6Level: §f" + m.getLevel());
		sm.setLore(Arrays.asList("§6Experience: §f" + m.getExperience(), "§6Drops: §f" + m.getDrops().length));
		sm.setOwner(p.getName());
		skull.setItemMeta(sm);
		
		inv.setItem(11, skull);
		
		
		for (int i = 9; i < 36; i+=9) {
			inv.setItem(i-1, yellow);
		}
		
		for (int i = 1; i < 27; i+=9) {
			inv.setItem(i-1, yellow);
		}
		
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
