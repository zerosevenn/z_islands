package me.zeroseven.island.minions.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class MobMinion extends Minion implements ConfigurationSerializable {
	
	long spawnMobs;
	long killMobs;

	public MobMinion(int id, Location loc, UUID owner, double experience) {
		super(id, MinionType.MOBS, loc, owner, experience);
	}
	
	public MobMinion(int id, MinionType type, List<ItemStack> contents, Location loc, UUID owner, int level, double experience, ItemStack blockType) {
		super(id, type, contents, loc, owner, level,experience, blockType);
	}
	
	public Material[] getApplicableItems() {
		Material[] mats = {Material.LEATHER, Material.BEEF, Material.FEATHER, Material.CHICKEN, Material.ENDER_PEARL, 
				Material.MAGMA_CREAM, Material.BLAZE_ROD, Material.WHITE_WOOL, Material.MUTTON, Material.SLIME_BALL,
				Material.STRING, Material.SPIDER_EYE, Material.ROTTEN_FLESH, Material.GUNPOWDER, Material.PORKCHOP, 
				Material.BONE, Material.ARROW};
				
		return mats;
	}
	
	public ItemStack getDirector() {
		ItemStack item = getInventory().getItem(11);
		if (item == null) {
			return new ItemStack(Material.AIR);
		}
		
		return new ItemStack(item.getType());
	}
	
	public EntityType getMobType() {
		ItemStack item = getInventory().getItem(11);
		if (item == null) return null;
		
		Material type = item.getType();
		
		switch(type) {
		case COW_SPAWN_EGG:
			return EntityType.COW;
			
		case CHICKEN_SPAWN_EGG:
			return EntityType.CHICKEN;
			
		case ENDERMAN_SPAWN_EGG:
			return EntityType.ENDERMAN;
			
		case MAGMA_CUBE_SPAWN_EGG:
			return EntityType.MAGMA_CUBE;
			
		case BLAZE_SPAWN_EGG:
			return EntityType.BLAZE;
			
		case SHEEP_SPAWN_EGG:
			return EntityType.SHEEP;
			
		case SLIME_SPAWN_EGG:
			return EntityType.SLIME;
			
		case SPIDER_SPAWN_EGG:
			return EntityType.SPIDER;
			
		case ZOMBIE_SPAWN_EGG:
			return EntityType.ZOMBIE;
			
		case CREEPER_SPAWN_EGG:
			return EntityType.CREEPER;
		
		case PIG_SPAWN_EGG:
			return EntityType.PIG;
			
		case SKELETON_SPAWN_EGG:
			return EntityType.SKELETON;
			
		case CAVE_SPIDER_SPAWN_EGG:
			return EntityType.CAVE_SPIDER;
			
		default:
			return null;
			
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
		sm.setOwner("Hunter");
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
				spawnMobs();
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("zIsland"), 40);
		
	}
	
	public void spawnMobs() {
		
		int random = new Random().nextInt(3) + 1;
		EntityType type = getMobType();
		
		if (type != null) {
			for (int i = 0; i < random; i++) {
				Location spawnLoc = getSpawnLocations().get(new Random().nextInt(getSpawnLocations().size()));
				spawnLoc.getWorld().spawnEntity(spawnLoc, type);
			}
		}
		
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.SECOND, getInterval());
		killMobs = cal.getTimeInMillis();
		
		cal.add(Calendar.SECOND, 3);
		spawnMobs = cal.getTimeInMillis();
		
	}
	
	public void killMobs() {
		
		for (LivingEntity en : getNearbyEntities()) {
			if (!IslandPlugin.antiDrop.contains(en)) {
				IslandPlugin.antiDrop.add(en);
				IslandPlugin.antiDropMinion.put(en, this);
			}
			en.setHealth(0);
		}
	}
	
	public List<LivingEntity> getNearbyEntities() {
		List<LivingEntity> list = new ArrayList<>();
		for (Entity en : getLocation().getWorld().getNearbyEntities(getLocation(), 3, 3, 3)) {
			if (getMobType() != null) {
				if (en instanceof LivingEntity && getMobType() == en.getType())
					list.add((LivingEntity) en);
			}
		}
		return list;
	}
	
	public List<Location> getSpawnLocations() {

		Block b = getLocation().getBlock();

		List<Location> list = new ArrayList<>();
		list.add(b.getRelative(BlockFace.EAST).getLocation());
		list.add(b.getRelative(BlockFace.NORTH).getLocation());
		list.add(b.getRelative(BlockFace.WEST).getLocation());
		list.add(b.getRelative(BlockFace.SOUTH).getLocation());
		list.add(b.getRelative(BlockFace.NORTH_EAST).getLocation());
		list.add(b.getRelative(BlockFace.NORTH_WEST).getLocation());
		list.add(b.getRelative(BlockFace.SOUTH_EAST).getLocation());
		list.add(b.getRelative(BlockFace.SOUTH_WEST).getLocation());

		return list;
	}
	
	public long getMobSpawnDate() {
		return spawnMobs;
	}
	
	public long getMobKillDate() {
		return killMobs;
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
		map.put("mob-type", getDirector());
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static MobMinion deserialize(Map<String, Object> map) {
		return new MobMinion((int) map.get("id"), MinionType.CROPS, (List<ItemStack>) map.get("contents"),
				(Location) map.get("loc"), UUID.fromString((String) (map.get("owner"))), (int) map.get("level"), (double) map.get("experience") , (ItemStack) map.get("crop-type"));
	}
}
