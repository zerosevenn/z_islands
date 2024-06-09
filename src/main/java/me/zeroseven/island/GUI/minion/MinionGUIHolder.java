package me.zeroseven.island.GUI.minion;

import me.zeroseven.island.minions.Minion;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MinionGUIHolder implements InventoryHolder {
	
	OfflinePlayer p;
	Inventory inv;
	Minion m;
	
	public MinionGUIHolder(OfflinePlayer p, Minion m) {
		this.p = p;
		this.m = m;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return p;
	}
	
	public Minion getMinion() {
		return m;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

}
