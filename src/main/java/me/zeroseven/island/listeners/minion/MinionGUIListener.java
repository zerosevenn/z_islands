package me.zeroseven.island.listeners.minion;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.GUI.minion.MinionGUIHolder;
import me.zeroseven.island.nms.Animator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@SuppressWarnings({})
public class MinionGUIListener implements Listener {

	private final IslandPlugin plugin;
	private final Animator animator;

	public MinionGUIListener(IslandPlugin plugin) {
		this.plugin = plugin;
		this.animator = new Animator(plugin);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (!(e.getWhoClicked() instanceof Player)) return;

		Player p = (Player) e.getWhoClicked();

		if (inv.getHolder() instanceof MinionGUIHolder) {
			MinionGUIHolder holder = (MinionGUIHolder) inv.getHolder();
			Minion m = holder.getMinion();

			if (!m.getOwnerID().equals(p.getUniqueId())) return;

			e.setCancelled(true);

			ItemStack current = e.getCurrentItem();
			if (current == null) return;


			if (e.getRawSlot() == 13) {
				for (int i : m.getContentSlots()) {
					ItemStack item = inv.getItem(i);
					if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE)
						continue;

					Map<Integer, ItemStack> map = p.getInventory().addItem(item);

					if (!map.isEmpty()) {
						p.getWorld().dropItem(m.getLocation(), map.get(0));
					}
					inv.setItem(i, new ItemStack(Material.AIR));
				}
			}


			if (e.getRawSlot() == 15) {

				animator.animateArmorStand(p.getPlayer(), Minion.getArmorStand(m.getLocation()));
				p.closeInventory();

			}
		}
	}
}
