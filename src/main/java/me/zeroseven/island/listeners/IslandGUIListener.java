package me.zeroseven.island.listeners;


import me.zeroseven.island.GUI.IslandGUIHolder;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandLoader;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IslandGUIListener implements Listener {

    IslandPlugin plugin;
    IslandLoader islandLoader;

    public IslandGUIListener(IslandPlugin plugin) {
        this.plugin = plugin;
        this.islandLoader = new IslandLoader(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }

        if(event.getInventory().getHolder() instanceof IslandGUIHolder){
            event.setCancelled(true);

            if(event.getInventory().getHolder() == null){
                return;
            }



            IslandGUIHolder islandGUIHolder = (IslandGUIHolder) event.getClickedInventory().getHolder();

            Island island = islandGUIHolder.getIsland();
            Location islandLocation = island.getLocation();

            if(island.getLocation() == null){
                System.out.println("island is null");
                return;
            }

            switch (event.getRawSlot()){
                case 14 -> islandLoader.loadSchematic("desertisland.schem", islandLocation.getWorld(), islandLocation, player);
                case 12 -> islandLoader.loadSchematic("netherisland.schem", islandLocation.getWorld(), islandLocation, player);
                case 11 -> islandLoader.loadSchematic("medievalisland.schem", islandLocation.getWorld(), islandLocation, player);
                case 15 -> islandLoader.loadSchematic("jungleisland.schem", islandLocation.getWorld(), islandLocation, player);
            }

            System.out.println("island teleporting");
            player.teleport(island.getSpawnLocation());

        }
    }

}
