package me.zeroseven.island.listeners;

import me.zeroseven.island.minions.MinionSpawner;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;



public class MinionSpawnerListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
				
				if (e.getClickedBlock().getRelative(BlockFace.UP).getType() != Material.AIR) return;
				
				ItemStack item = e.getItem();
				if (MinionSpawner.isSpawner(item)) {
					System.out.println("is spawner");
					MinionSpawner spawner = new MinionSpawner(item);
					spawner.spawn(e.getClickedBlock().getRelative(BlockFace.UP).getLocation(), p);
					ItemStack hand = p.getItemInHand();
					hand.setAmount(hand.getAmount() - 1);
					p.setItemInHand(hand);
				}
				System.out.println("is not");
			}
		}	
	}
}
