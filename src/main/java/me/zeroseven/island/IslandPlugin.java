package me.zeroseven.island;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.commands.IslandCommand;
import me.zeroseven.island.commands.MinionCommand;
import me.zeroseven.island.config.FileManager;
import me.zeroseven.island.database.IslandDAO;
import me.zeroseven.island.database.MinionsDAO;
import me.zeroseven.island.database.PlayersDAO;
import me.zeroseven.island.listeners.*;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import org.bukkit.Bukkit;
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

public final class IslandPlugin extends JavaPlugin{

    public static final List<Minion> MINIONS = new ArrayList<>();
    public static final List<LivingEntity> antiDrop = new ArrayList<>();
    public static final Map<LivingEntity, MobMinion> antiDropMinion = new HashMap<>();
    private static IslandBuffer islandBuffer;

    private ProtocolManager protocolManager;
    public static int TOTAL;

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.islandBuffer = new IslandBuffer(this);
        setupDAO();
        getServer().getPluginManager().registerEvents(new MinionListeners(), this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionSpawnerListener(), this);
        getServer().getPluginManager().registerEvents(new IslandListeners(), this);
        getCommand("minion").setExecutor(new MinionCommand());
        getCommand("island").setExecutor(new IslandCommand(this));
        ConfigurationSerialization.registerClass(BlockMinion.class, "BlockMinion");
        ConfigurationSerialization.registerClass(MobMinion.class, "MobMinion");
        ConfigurationSerialization.registerClass(CropMinion.class, "CropMinion");

        saveDefaultConfig();
        TOTAL = getConfig().getInt("total");
        setupMinions();
        loadPlayers();

    }

    @Override
    public void onDisable() {
        saveMinions();
        saveConfig();
        savePlayers();
    }

    public void savePlayers(){
        for(Player player : Bukkit.getOnlinePlayers()){
            getBuffer().savePlayerIsland(player);
        }
    }
    public void loadPlayers(){
        for(Player player : Bukkit.getOnlinePlayers()){
            getBuffer().loadPlayerIsland(player);
        }
    }

    public void setupDAO(){
        new IslandDAO(this).createTable();
        new MinionsDAO(this).createTable();
        new PlayersDAO(this).createTable();
    }

    public void saveMinions(){

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
    }

    public void setupMinions(){
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

    public static IslandBuffer getBuffer(){
        return islandBuffer;
    }


    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
