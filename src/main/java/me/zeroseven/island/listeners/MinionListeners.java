package me.zeroseven.island.listeners;

import me.zeroseven.island.minions.Minion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import static me.zeroseven.island.IslandPlugin.MINIONS;

public class MinionListeners implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent e) {

        if (e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            Entity en = e.getRightClicked();
            Player p = e.getPlayer();

            for (Minion m : MINIONS) {

                Location l1 = en.getLocation();
                Location l2 = m.getLocation();

                if (l1.getWorld().getName().equals(l2.getWorld().getName())) {

                    if (l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ()) {

                        e.setCancelled(true);

                        if (m.getOwnerID().equals(p.getUniqueId())) {
                            p.openInventory(m.getInventory());
                        }

                        break;

                    }

                }

            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getEntityType() == EntityType.ARMOR_STAND) {
            Entity en = e.getEntity();

            for (Minion m : MINIONS) {

                Location l1 = en.getLocation();
                Location l2 = m.getLocation();

                if (l1.getWorld().getName().equals(l2.getWorld().getName())) {

                    if (l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ()) {

                        e.setCancelled(true);

                        break;

                    }

                }

            }
        }

    }
}
