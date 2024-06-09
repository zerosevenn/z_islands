package me.zeroseven.island.GUI.island;

import me.zeroseven.island.island.Island;
import me.zeroseven.island.minions.Minion;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class IslandGUIHolder implements InventoryHolder {

    OfflinePlayer p;
    Inventory inv;
    Island island;

    public IslandGUIHolder(OfflinePlayer p,  Island island) {
        this.p = p;
        this.island = island;
    }

    public OfflinePlayer getOfflinePlayer() {
        return p;
    }


    public Island getIsland() {
        return island;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
