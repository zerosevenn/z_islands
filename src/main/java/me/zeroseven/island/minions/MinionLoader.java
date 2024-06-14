package me.zeroseven.island.minions;

import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.Animator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MinionLoader {

    private Island island;
    private Animator animator;


    public MinionLoader(Island island) {
        this.island = island;
        this.animator = new Animator((IslandPlugin) Bukkit.getPluginManager().getPlugin("zIsland"));
    }

    public void load(){
        Player p = island.getOwner();
        Location location = island.getLocation();
        switch (island.getIslandType()){
            case MEDIEVAL:
                break;
            case NETHER:
                MinionSpawner spawner = new MinionSpawner(new ItemStack(Material.AIR));
                spawner.spawn(location.add(-9, 0, -1), p);
                ItemStack hand = p.getItemInHand();
                hand.setAmount(hand.getAmount() - 1);
                p.setItemInHand(hand);
                animator.animateArmorStand(p.getPlayer(), Minion.getArmorStand(spawner.getMinion().getLocation()));
                break;
            case JUNGLE:
                break;
            case DESERT:
                break;
        }
    }

}
