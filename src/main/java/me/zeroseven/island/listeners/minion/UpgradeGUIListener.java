package me.zeroseven.island.listeners.minion;

import java.util.ArrayList;
import java.util.List;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.GUI.minion.MinionGUI;
import me.zeroseven.island.GUI.minion.UpgradeGUIHolder;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;



public class UpgradeGUIListener implements Listener {
	
	IslandPlugin plugin;
	public UpgradeGUIListener(IslandPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		Inventory inv = e.getInventory();
		
		if (e.getWhoClicked() instanceof Player) {
			
			Player p = (Player) e.getWhoClicked();
			
			if (inv.getHolder() != null) {
				if (inv.getHolder() instanceof UpgradeGUIHolder) {
					
					UpgradeGUIHolder holder = (UpgradeGUIHolder) inv.getHolder();
					
					if (holder.getPlayer().getUniqueId().equals(p.getUniqueId())) {
						
						Minion m = holder.getMinion();
						
						List<Integer> slots = arrayToList(m.getUpgradeItemSlots());
						
//						if (e.getRawSlot() < 54 && !slots.contains(e.getRawSlot()) && e.getRawSlot() != 25) {
						if (e.getRawSlot() < 54 && !slots.contains(e.getRawSlot()) && e.getRawSlot() != 25 && e.getRawSlot() != 20
								&& e.getRawSlot() != 48 && e.getRawSlot() != 49) {
							e.setCancelled(true);	
							return;
						}
						
						if (e.getRawSlot() == 48) {
							e.setCancelled(true);
							p.closeInventory();
							new BukkitRunnable() {
								
								@Override
								public void run() {
									p.openInventory(MinionGUI.createInventory(p, m));
									
								}
							}.runTaskLater(plugin, 1);
							return;
						} 
						
						if (e.getRawSlot() == 49) {
							e.setCancelled(true);
							p.closeInventory();
							return;
						}
						
						List<Material> mats;
						
						if (m instanceof BlockMinion) {
							BlockMinion bm = (BlockMinion) m;
							mats = arrayToList(bm.getApplicableBlocks());
						} else if (m instanceof CropMinion) {
							mats = arrayToList(((CropMinion) m).getApplicableCropItems());
						} else {
							mats = arrayToList(((MobMinion) m).getApplicableItems());
						}
							
//							List<Material> mats = arrayToList(bm.getApplicableBlocks());
							
							if (e.getCurrentItem() == null) return;
							
							if (e.getRawSlot() == 25) {
								if (e.getCurrentItem().getType() != Material.GREEN_WOOL) {
									e.setCancelled(true);
									return;
								}
								
//								int cost = bm.getUpgradeCost2();
								int cost = m.getUpgradeCost2()[0];
								int stars = m.getUpgradeCost2()[1];
								
								int total = 0;
								int starTotal = 0;
								
								if (inv.getItem(20) != null && inv.getItem(20).getType() == Material.NETHER_STAR) {
									starTotal = inv.getItem(20).getAmount();
								}
								
								for (int i : m.getUpgradeItemSlots()) {
									
									ItemStack item = inv.getItem(i);
//									int total = 0;
									
									if (item != null && item.getType() != Material.AIR) {
										
										int amount = item.getAmount();
										
										if ((total + amount) > cost) {
											
											if (total == cost) {
												inv.setItem(i, new ItemStack(Material.AIR));
												p.getInventory().addItem(item);
											} else {
												int required = cost - total;
												int left = amount - required;
												
												ItemStack leftItem = new ItemStack(item);
												leftItem.setAmount(left);
												
												p.getInventory().addItem(leftItem);
												inv.setItem(i, new ItemStack(Material.AIR));
												total = cost;
											}
											
										} else {
											inv.setItem(i, new ItemStack(Material.AIR));
											total += amount;
										}
										
									}
								}
								
								if (starTotal >= stars) {
									inv.setItem(20, new ItemStack(Material.AIR));
									p.getInventory().addItem(new ItemStack(Material.NETHER_STAR, starTotal - stars));
								}
								
								m.upgrade();
								p.updateInventory();
								p.closeInventory();
//								p.sendMessage(m.getLevel() + "");
								p.sendMessage(ChatColor.GREEN + "Minion has been upgraded to level " + m.getLevel() + "!");
							}
							
							if (!mats.contains(e.getCurrentItem().getType()) && e.getCurrentItem().getType() != Material.NETHER_STAR) {
								e.setCancelled(true);
								return;
							}
							
							if (!m.canLevelUp()) return;
							
							new BukkitRunnable() {
								
								@Override
								public void run() {
									
									if (inv.getViewers().isEmpty()) {
										cancel();
										return;
									}
									
//									int cost = m.getUpgradeCost();
									int cost = m.getUpgradeCost2()[0];
									int stars = m.getUpgradeCost2()[1];
									int bal = 0;
									
									for (int i : slots) {
										if (inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR && 
												inv.getItem(i).getType() != Material.NETHER_STAR) {
											bal += inv.getItem(i).getAmount();
										}
									}
									
									int starTotal = 0;
									
									if (inv.getItem(20) != null && inv.getItem(20).getType() == Material.NETHER_STAR) {
										starTotal = inv.getItem(20).getAmount();
									}
									
									if (bal >= cost && starTotal >= stars) {
										inv.setItem(25, new ItemStack(Material.GREEN_WOOL));
									} else {
										inv.setItem(25, new ItemStack(Material.RED_WOOL));
									}
									
								}
							}.runTaskTimerAsynchronously(plugin, 0, 20);
							
//						}
						
						
					}
					
				}
			}
		}
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		
		if (e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			Inventory inv = e.getInventory();
			if (inv.getHolder() instanceof UpgradeGUIHolder) {
				UpgradeGUIHolder holder = (UpgradeGUIHolder) inv.getHolder();
				
				if (holder.getPlayer().getUniqueId().equals(p.getUniqueId())) {
					
					Minion m = holder.getMinion();
					
					for (int i : m.getUpgradeItemSlots()) {
						ItemStack item = inv.getItem(i);
						if (item != null && item.getType() != Material.AIR) {
							p.getInventory().addItem(item);
							p.updateInventory();
						}
					}
					
					if (inv.getItem(20) != null && inv.getItem(20).getType() != Material.AIR) {
						p.getInventory().addItem(inv.getItem(20));
						p.updateInventory();
					}
					
				}
			}
		}
		
	}
	
	private List<Integer> arrayToList(int[] arr) {
		List<Integer> list = new ArrayList<>();
		for (int i : arr) list.add(i);
		return list;
	}
	
	private List<Material> arrayToList(Material[] arr) {
		List<Material> list = new ArrayList<>();
		for (Material i : arr) list.add(i);
		return list;
	}

}
