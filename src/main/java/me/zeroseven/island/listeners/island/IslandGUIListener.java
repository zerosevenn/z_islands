package me.zeroseven.island.listeners.island;


import me.zeroseven.island.GUI.island.IslandGUIHolder;
import me.zeroseven.island.IslandPlugin;
import me.zeroseven.island.island.Island;
import me.zeroseven.island.nms.IslandSchematic;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IslandGUIListener implements Listener {

    IslandPlugin plugin;

    public IslandGUIListener(IslandPlugin plugin) {
        this.plugin = plugin;
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

            if(islandGUIHolder.getIsland() == null){
                return;
            }

            Island island = islandGUIHolder.getIsland();

            if(island.getLocation() == null){
                return;
            }

            Location islandLocation = island.getLocation().clone().add(0, 0, 0);


            switch (event.getRawSlot()){
                case 14 -> new IslandSchematic("desertisland.schem", islandLocation, player).paste();
                case 12 -> new IslandSchematic("netherisland.schem",  islandLocation.add(90, 68, 6), player).pasteAndRotate(180);
                case 11 -> new IslandSchematic("medievalisland.schem", islandLocation, player);
                case 15 -> new IslandSchematic("jungleisland.schem",   islandLocation, player);
            }





        }
    }

}
