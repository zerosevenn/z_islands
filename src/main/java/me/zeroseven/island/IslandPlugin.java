package me.zeroseven.island;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.zeroseven.island.commands.IslandCommand;
import me.zeroseven.island.commands.MinionCommand;
import me.zeroseven.island.config.FileManager;
import me.zeroseven.island.listeners.MinionGUIListener;
import me.zeroseven.island.listeners.MinionSpawnerListener;
import me.zeroseven.island.listeners.UpgradeGUIListener;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public final class IslandPlugin extends JavaPlugin implements @NotNull Listener {

    public static final List<Minion> MINIONS = new ArrayList<>();
    public static final List<LivingEntity> antiDrop = new ArrayList<>();
    public static final Map<LivingEntity, MobMinion> antiDropMinion = new HashMap<>();



    private ProtocolManager protocolManager;
    public static int TOTAL;

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionSpawnerListener(), this);
        getCommand("minion").setExecutor(new MinionCommand());
        getCommand("island").setExecutor(new IslandCommand(this));
        ConfigurationSerialization.registerClass(BlockMinion.class, "BlockMinion");
        ConfigurationSerialization.registerClass(MobMinion.class, "MobMinion");
        ConfigurationSerialization.registerClass(CropMinion.class, "CropMinion");

        saveDefaultConfig();
        TOTAL = getConfig().getInt("total");

        for (MinionType type : MinionType.values()) {
            FileConfiguration fc = FileManager.getInstance().getMinionsConfig(type);
            for (String str : fc.getKeys(false)) {

                if (type == MinionType.BLOCKS) {
                    BlockMinion bm = (BlockMinion) fc.get(str);
                    MINIONS.add(bm);
                    bm.breakBlock();
                } else if (type == MinionType.CROPS) {
                    CropMinion cm = (CropMinion) fc.get(str);
                    MINIONS.add(cm);
                    cm.breakCrop();
                } else {
                    MobMinion mm = (MobMinion) fc.get(str);
                    MINIONS.add(mm);
                    mm.spawnMobs();
                }

            }
        }

        for (MinionType type : MinionType.values()) {
            FileManager.getInstance().getMinionsFile(type).delete();
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                Calendar cal = Calendar.getInstance();

                for (Minion m : MINIONS) {
                    if (m instanceof BlockMinion) {
                        BlockMinion bm = (BlockMinion) m;

                        if (bm.getBlockBreakDate() == 0L || bm.getBlockPlaceDate() == 0L)
                            return;

                        if (cal.getTimeInMillis() >= bm.getBlockBreakDate()) {
                            bm.breakBlock();
                        } else if (cal.getTimeInMillis() >= bm.getBlockPlaceDate()) {
                            bm.placeBlock();
                        }
                    }

                    if (m instanceof CropMinion) {
                        CropMinion cm = (CropMinion) m;

                        if (cm.getBlockBreakDate() == 0L || cm.getBlockPlaceDate() == 0L)
                            return;

                        if (cal.getTimeInMillis() >= cm.getBlockBreakDate()) {
                            cm.breakCrop();
                        } else if (cal.getTimeInMillis() >= cm.getBlockPlaceDate()) {
                            cm.placeCrop();
                        }
                    }

                    if (m instanceof MobMinion) {
                        MobMinion mm = (MobMinion) m;

                        if (mm.getMobKillDate() == 0L || mm.getMobSpawnDate() == 0L)
                            return;

                        if (cal.getTimeInMillis() >= mm.getMobSpawnDate()) {
                            mm.spawnMobs();
                        } else if (cal.getTimeInMillis() >= mm.getMobKillDate()) {
                            mm.killMobs();
                        }
                    }
                }

            }
        }.runTaskTimer(this, 40, 20);
    }

    @Override
    public void onDisable() {

        getConfig().set("total", TOTAL);

        for (Minion m : MINIONS) {
            FileConfiguration fc = FileManager.getInstance().getMinionsConfig(m.getType());
            if (m.getType() == MinionType.BLOCKS) {
                fc.set(m.getID() + "", ((BlockMinion) m));
            } else if (m.getType() == MinionType.CROPS) {
                fc.set(m.getID() + "", ((CropMinion) m));
            } else {
                fc.set(m.getID() + "", ((MobMinion) m));
            }

            try {
                fc.save(FileManager.getInstance().getMinionsFile(m.getType()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        saveConfig();

    }

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

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (antiDrop.contains(e.getEntity())) {

            for (ItemStack item : e.getDrops()) {
                antiDropMinion.get(e.getEntity()).getInventory().addItem(item);
            }
            e.getDrops().clear();
            antiDrop.remove(e.getEntity());
            antiDropMinion.remove(e.getEntity());
        }
    }
}
