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
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;



public class CropMinion extends Minion implements ConfigurationSerializable {

	long placeBlock;
	long breakBlock;
	Location toPlace;

	public CropMinion(int id, Location loc, UUID owner, double exp) {
		super(id, MinionType.CROPS, loc, owner, exp);
	}
	
	public CropMinion(int id, MinionType type, List<ItemStack> contents, Location loc, UUID owner, int level, double experience, ItemStack blockType) {
		super(id, type, contents, loc, owner, level, experience, blockType);
	}

	public Material getCropType() {
		if (getInventory().getItem(11) == null)
			return Material.AIR;

		Material type = getInventory().getItem(11).getType();

		switch (type) {

		case WHEAT:
			return Material.WHEAT;

		case BEETROOT:
			return Material.BEETROOTS;

		case POTATO:
			return Material.POTATOES;

		case CARROT:
			return Material.CARROTS;

		case NETHER_WART:
			return Material.NETHER_WART;

		case SWEET_BERRIES:
			return Material.SWEET_BERRY_BUSH;
			
		case MELON:
			return Material.MELON;

		default:
			return Material.AIR;

		}
	}

	public Material getCropItem() {
		Material type = getCropType();

		switch (type) {

		case WHEAT:
			return Material.WHEAT;

		case BEETROOTS:
			return Material.BEETROOT;

		case POTATOES:
			return Material.POTATO;

		case CARROTS:
			return Material.CARROT;

		case NETHER_WART:
			return Material.NETHER_WART;

		case SWEET_BERRY_BUSH:
			return Material.SWEET_BERRIES;
			
		case MELON:
			return Material.MELON;

		default:
			return Material.AIR;

		}
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
		sm.setOwner("Farmer");
		skull.setItemMeta(sm);

		ArmorStand stand = (ArmorStand) getLocation().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

		stand.setSmall(true);
		stand.setBasePlate(false);
		stand.setCanPickupItems(true);
		stand.setArms(true);
		stand.setHelmet(skull);
		stand.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		stand.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
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
				breakCrop();
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("zIsland"), 40);

	}

	public void breakCrop() {

		List<Block> applicable = new ArrayList<>();

		for (Block b : getEffectiveBlocks()) {
			if (b.getType() == Material.AIR) continue;
			if (b.getType() == getCropType()) {
				if (b.getBlockData() instanceof Ageable) {
					Ageable age = (Ageable) b.getBlockData();
					if (age.getAge() == age.getMaximumAge())
						applicable.add(b);
				} else {
					applicable.add(b);
				}
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 2);
		placeBlock = cal.getTimeInMillis();

		cal.add(Calendar.SECOND, getInterval());
		breakBlock = cal.getTimeInMillis();

		if (applicable.size() == 0)
			return;

		Block b = applicable.get(new Random().nextInt(applicable.size()));

		Inventory inv = getInventory();
		inv.setItem(11, new ItemStack(getCropItem(), 64));
		inv.addItem(new ItemStack(getCropItem()));
		inv.setItem(11, new ItemStack(getCropItem(), 1));

		b.setType(Material.AIR);

		b.getWorld().playSound(b.getLocation(), Sound.BLOCK_CROP_BREAK, 1, 1);

		toPlace = b.getLocation();

	}

	public void placeCrop() {
		if (toPlace == null)
			return;
		toPlace.getBlock().setType(getCropType());
		if (toPlace.getBlock().getBlockData() instanceof Ageable) {
		Ageable ageable = (Ageable) toPlace.getBlock().getBlockData();
		ageable.setAge(ageable.getMaximumAge());
		toPlace.getBlock().setBlockData(ageable);
		}
		toPlace.getWorld().playSound(toPlace, Sound.ITEM_CROP_PLANT, 1, 1);
		toPlace = null;
	}

	public Material[] getApplicableBlocks() {

		Material[] mats = { Material.WHEAT, Material.PUMPKIN, Material.MELON, Material.POTATOES, Material.CARROTS,
				Material.CACTUS, Material.SUGAR_CANE, Material.BAMBOO, Material.SWEET_BERRY_BUSH,
				Material.NETHER_WART };

		return mats;
	}
	
	public Material[] getApplicableCropItems() {
		return new Material[] {Material.WHEAT, Material.POTATO, Material.CARROT, Material.MELON, Material.BEETROOT,
				Material.NETHER_WART, Material.SWEET_BERRIES};
	}

	public List<Block> getEffectiveBlocks() {

		Block b = getLocation().getBlock();

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
		map.put("crop-type", new ItemStack(getCropItem()));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static CropMinion deserialize(Map<String, Object> map) {
		return new CropMinion((int) map.get("id"), MinionType.CROPS, (List<ItemStack>) map.get("contents"),
				(Location) map.get("loc"), UUID.fromString((String) (map.get("owner"))), (int) map.get("level"), (double) map.get("experience") , (ItemStack) map.get("crop-type"));
	}
}
