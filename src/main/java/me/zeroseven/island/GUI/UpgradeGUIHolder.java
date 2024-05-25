package me.zeroseven.island.GUI;


import me.zeroseven.island.minions.Minion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class UpgradeGUIHolder implements InventoryHolder {
	
	Minion m;
	Player p;
	
	public UpgradeGUIHolder(Player p, Minion m) {
		this.m = m;
		this.p = p;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Minion getMinion() {
		return m;
	}

}
