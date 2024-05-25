package me.zeroseven.island.minions.types;

import java.util.*;

import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class BlockMinion extends Minion implements ConfigurationSerializable {

	long breakBlock;
	long placeBlock;
	Location toPlace;

	public BlockMinion(int id, Location loc, UUID owner, double experience) {
		super(id, MinionType.BLOCKS, loc, owner, experience);
	}
	
	public BlockMinion(int id, MinionType type, List<ItemStack> contents, Location loc, UUID owner, int level, double experience, ItemStack blockType) {
		super(id, type, contents, loc, owner, level, experience, blockType);
	}

	public Material getBlockType() {
		if (getInventory().getItem(11) == null) return Material.AIR;
		return getInventory().getItem(11).getType();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void spawn() {
		
		Location location = getLocation();
		Player player = Bukkit.getPlayer(getOwnerID());
		location.setY(location.getBlockY());
		location.setX(location.getBlockX() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		location.setDirection(player.getLocation().subtract(location).toVector());
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta sm = (SkullMeta) skull.getItemMeta();
		sm.setOwner("Ayush_03");
		skull.setItemMeta(sm);

		ArmorStand stand = (ArmorStand) getLocation().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

		stand.setSmall(true);
		stand.setBasePlate(false);
		stand.setCanPickupItems(true);
		stand.setArms(true);
		stand.setHelmet(skull);
		stand.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		stand.setItemInHand(new ItemStack(Material.DIAMOND_PICKAXE));
		stand.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		stand.setBoots(new ItemStack(Material.LEATHER_BOOTS));
		stand.setGravity(false);
		stand.setCustomNameVisible(true);
		stand.setCustomName("§e§lLevel: §f" + getLevel() +
				" | §a§lXP: §f" + getExperience() +
				" | §5§lDrops: §f" + getDrops().length);

		new BukkitRunnable() {
			
			@Override
			public void run() {
				breakBlock();
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("zIsland"), 40);
	}
	
	public void breakBlock() {
		
		List<Block> applicable = new ArrayList<>();
		
		for (Block b : getEffectiveBlocks()) {
			if (b.getType() == getBlockType()) {
				applicable.add(b);
			}
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 2);
		placeBlock = cal.getTimeInMillis();
		
		cal.add(Calendar.SECOND, getInterval());
		breakBlock = cal.getTimeInMillis();
		
		if (applicable.size() == 0) return;
		
		Block b = applicable.get(new Random().nextInt(applicable.size()));
		
		Inventory inv = getInventory();
		inv.setItem(11, new ItemStack(getBlockType(), 64));
		inv.addItem(new ItemStack(b.getType()));
		inv.setItem(11, new ItemStack(getBlockType(), 1));
		
		b.setType(Material.AIR);
		
		if (getBlockType() == Material.BLUE_ICE || getBlockType() == Material.ICE || getBlockType() == Material.GLOWSTONE) {
			b.getWorld().playSound(b.getLocation(), Sound.BLOCK_GLASS_BREAK, 3, 1);
		} else {
		
		b.getWorld().playSound(b.getLocation(), Sound.BLOCK_STONE_BREAK, 3, 1);
		}
		toPlace = b.getLocation();
		
	}
	
	public void placeBlock() {
		if (toPlace == null) return;
		toPlace.getBlock().setType(getBlockType());
		toPlace.getWorld().playSound(toPlace, Sound.BLOCK_STONE_PLACE, 1, 1);
		toPlace = null;
	}
	
	public Material[] getApplicableBlocks() {
		
		Material[] mats = {Material.STONE, Material.COBBLESTONE, Material.GOLD_ORE, Material.IRON_ORE,
				Material.ANDESITE, Material.DIRT, Material.COBBLESTONE, Material.SAND, Material.GRAVEL, Material.COAL,
				Material.OAK_WOOD, Material.JUNGLE_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.ACACIA_WOOD,
				Material.SPRUCE_WOOD, Material.LAPIS_ORE, Material.BRICK, Material.OBSIDIAN, Material.DIAMOND_ORE, 
				Material.REDSTONE_ORE, Material.ICE, Material.BLUE_ICE, Material.QUARTZ_BLOCK, Material.CLAY, 
				Material.NETHER_BRICK, Material.GLOWSTONE, Material.CLAY, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK,
				Material.END_STONE, Material.EMERALD_ORE};
		
		return mats;
		
	}
	
	public List<Block> getEffectiveBlocks() {
		
		Block b = getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		List<Block> list = new ArrayList<>();
		list.add(b.getRelative(BlockFace.EAST));
		list.add(b.getRelative(BlockFace.NORTH));
		list.add(b.getRelative(BlockFace.WEST));
		list.add(b.getRelative(BlockFace.SOUTH));
		list.add(b.getRelative(BlockFace.NORTH_EAST));
		list.add(b.getRelative(BlockFace.NORTH_WEST));
		list.add(b.getRelative(BlockFace.SOUTH_EAST));
		list.add(b.getRelative(BlockFace.SOUTH_WEST));
		
		return list;
	}
	
	public long getBlockBreakDate() {
		return breakBlock;
	}
	
	public long getBlockPlaceDate() {
		return placeBlock;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", getID());
		map.put("loc", getLocation());
		map.put("contents", getContents());
		map.put("level", getLevel());
		map.put("experience", getExperience());
		map.put("owner", getOwnerID().toString());
		map.put("block-type", new ItemStack(getBlockType()));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static BlockMinion deserialize(Map<String, Object> map) {
//		return new Minion(iint id, MinionType type, List<ItemStack> contents, Location loc, UUID owner, int level)r
		return new BlockMinion((int) map.get("id"), MinionType.CROPS, (List<ItemStack>) map.get("contents"),
				(Location) map.get("loc"), UUID.fromString((String) (map.get("owner"))), (int) map.get("level"), (double) map.get("experience") , (ItemStack) map.get("crop-type"));
	}
}
