package me.zeroseven.island.minions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zeroseven.island.GUI.minion.MinionGUI;
import me.zeroseven.island.IslandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Minion {
	
	int id;
	MinionType type;
	Inventory inv;
	Location loc;
	UUID owner;
	int level;
	double experience;
	ItemStack[] drops;
	ArmorStand armorStand;

	public Minion(int id, MinionType type, Location loc, UUID owner, double experience) {
		this.id = id;
		this.type = type;
		this.loc = loc;
		this.owner = owner;
		this.level = 1;
		this.experience = experience;
		this.inv = MinionGUI.createInventory(Bukkit.getOfflinePlayer(owner), this);
	}
	
	public Minion(int id, MinionType type, List<ItemStack> contents, Location loc, UUID owner, int level, double experience, ItemStack blockType) {
		this.id = id;
		this.type = type;
		this.loc = loc;
		this.owner = owner;
		this.level = level;
		this.inv = MinionGUI.createInventory(Bukkit.getOfflinePlayer(owner), this);
		this.experience = experience;
		
		for (int i = 0; i < contents.size(); i++) {
			int index = getContentSlots()[i];
			inv.setItem(index, contents.get(i));
		}
		
		inv.setItem(11, blockType);
	}

	public static ArmorStand getArmorStand(Location location) {
		for (Entity en : location.getWorld().getNearbyEntities(location, 1, 1, 1)) {
			if (en instanceof ArmorStand) {
				ArmorStand stand = (ArmorStand) en;
				return stand;
			}
		}
		return null;
	}

	public void upgrade() {
		if (canLevelUp()) {
			level++;
			inv.setItem(getContentSlots()[level-1], new ItemStack(Material.AIR));
		}
	}
	
	public int[] getUpgradeItemSlots() { 
		return new int[] {10, 11, 12, 19, 21, 28, 29, 30};
	}
	
	public int[] getContentSlots() {
		return new int[] {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
	}
	
	public abstract void spawn();
	
	public int getID() {
		return id;
	}
	
	public MinionType getType() {
		return type;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public UUID getOwnerID() {
		return owner;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean canLevelUp() {
		return level < 10;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	public ItemStack[] getDrops() {
		return drops != null ? drops : new ItemStack[0];
	}

	public void setDrops(ItemStack[] drops) {
		this.drops = drops;
	}


	public double getExperience() {
		return experience;
	}

	public void setExperience(double experience) {
		this.experience = experience;
	}
	
	public List<ItemStack> getContents() {
		List<ItemStack> list = new ArrayList<>();
		
		for (int i = 0; i < level; i++) {
			int slot = getContentSlots()[i];
			if (inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR) {
				list.add(new ItemStack(Material.AIR));
			} else {
				list.add(inv.getItem(slot));
			}
		}
		
		return list;
	}
	
	public int getInterval() {
		return 12-level+1;
	}
	
	public int getUpgradeCost() {
		
		if (level == 1) return 32;
		
		int cost = 32;
		
		for (int i = 3; i <= level+1; i++) {
			cost *= 2;
 		}
		
		return cost;
	}
	
	public int[] getUpgradeCost2() {
		
		int[] toReturn = new int[2];
		
		int cost = 32;
		
		if (level == 1) return new int[] {32, 0};
		
		for (int i = 3; i <= level+1; i++) {
			cost *= 2;
 		}
		
		if (cost >= 1024) {
			toReturn[0] = 512;
//			toReturn[1] = level-5;
			toReturn[1] = cost/1024;
		} else {
			toReturn[0] = cost;
			toReturn[1] = 0;
		}
		
		return toReturn;
		
	}
	
	public void destroy() {
		for (Entity en : getLocation().getWorld().getNearbyEntities(getLocation(), 1, 1, 1)) {
			if (en instanceof ArmorStand) {
				ArmorStand stand = (ArmorStand) en;
				if (stand.getLocation().getX() == getLocation().getX() && 
						stand.getLocation().getY() == getLocation().getY() &&
						stand.getLocation().getZ() == getLocation().getZ()) {
					stand.remove();
					MinionSpawner spawner = new MinionSpawner(id, type);
					Player p = Bukkit.getPlayer(getOwnerID());
					
					
					if (p.getInventory().firstEmpty() == -1) {
						p.getWorld().dropItem(p.getLocation(), spawner.getItemStack());
						p.sendMessage(ChatColor.RED + "Since your inventory is full, the spawner has been dropped on the ground near you.");
					} else {
						p.getInventory().addItem(spawner.getItemStack());
						p.updateInventory();
					}
					IslandPlugin.MINIONS.remove(this);
					p.sendMessage(ChatColor.GREEN + "You have received a Minion Spawner of type " + type.toString() + ".");
				}
			}
		}
	}
}
