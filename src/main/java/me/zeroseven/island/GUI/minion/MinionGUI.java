package me.zeroseven.island.GUI.minion;

import me.zeroseven.island.config.MenuConfiguration;
import me.zeroseven.island.config.other.ConfigLoader;
import me.zeroseven.island.minions.Minion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class MinionGUI {
	
	@SuppressWarnings("deprecation")
	public static Inventory createInventory(OfflinePlayer p, Minion m) {

		ConfigLoader configLoader = new ConfigLoader(new MenuConfiguration((JavaPlugin) Bukkit.getPluginManager().getPlugin("zIsland")).getConfiguration());

		Inventory inv = Bukkit.createInventory(new MinionGUIHolder(p, m), 27, "Minion");
		
		ItemStack yellow = configLoader.getItemStack("Minion.Tapper");
		ItemStack pane = configLoader.getItemStack("Minion.Background");
		ItemStack chest = configLoader.getItemStack("Minion.GetItems");
		ItemStack bedrock = configLoader.getItemStack("Minion.Bedrock");

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
