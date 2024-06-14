package me.zeroseven.island.minions;

import java.util.ArrayList;
import java.util.List;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import me.zeroseven.island.utils.HiddenStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MinionSpawner {

	int id;
	MinionType type;
	ItemStack item;

	Minion minion;

	public MinionSpawner(int id, MinionType type, ItemStack item) {
		this.id = id;
		this.type = type;
		this.item = item;
	}

	@SuppressWarnings("deprecation")
	public MinionSpawner(int id, MinionType type) {
		this.id = id;
		this.type = type;

		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta sm = (SkullMeta) skull.getItemMeta();

		if (type == MinionType.MOBS) {
			sm.setOwner("Hunter");
		} else if (type == MinionType.BLOCKS) {
			sm.setOwner("Miner");
		} else {
			sm.setOwner("Farmer");
		}
		skull.setItemMeta(sm);

		this.item = skull;
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Minion Spawner" + HiddenStringUtils.encodeString("@!" + type.toString() + "_" + id));
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "" + WordUtils.capitalize(type.toString().toLowerCase()) + " Minion");
		lore.add(ChatColor.RED + "Right click on the block to spawn the Minion!");
		
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public MinionSpawner(ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			String displayName = item.getItemMeta().getDisplayName();
			if (HiddenStringUtils.hasHiddenString(displayName)) {
				String hidden = HiddenStringUtils.extractHiddenString(displayName);
				if (hidden.contains("@!")) {
					hidden = hidden.replace("@!", "");
					String[] split = hidden.split("_");
					this.type = MinionType.fromString(split[0]);
					this.id = Integer.parseInt(split[1]);
				}
			}
		}
	}

	public static boolean isSpawner(ItemStack item) {
		return item.getType().equals(Material.PLAYER_HEAD);
	}


	public void spawn(Location loc, Player owner) {
		
		Minion m;
		
		if (type == MinionType.BLOCKS) {
			m = new BlockMinion(id, loc, owner.getUniqueId(), 0);

		} else if (type == MinionType.CROPS) {
			m = new CropMinion(id, loc, owner.getUniqueId(), 0);
		} else {
			m = new MobMinion(id, loc, owner.getUniqueId(), 0);
		}
		this.minion = m;
		m.spawn();
		IslandPlugin.MINIONS.add(m);
	}

	public Minion getMinion() {
		return minion;
	}

	public int getID() {
		return id;
	}

	public MinionType getMinionType() {
		return type;
	}

	public ItemStack getItemStack() {
		return item;
	}
}