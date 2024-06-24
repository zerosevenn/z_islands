package me.zeroseven.island;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.zeroseven.island.buffer.IslandBuffer;
import me.zeroseven.island.commands.IslandCommand;
import me.zeroseven.island.commands.MinionCommand;
import me.zeroseven.island.commands.SellCommand;
import me.zeroseven.island.commands.ShopCommand;
import me.zeroseven.island.config.IslandConfiguration;
import me.zeroseven.island.config.MenuConfiguration;
import me.zeroseven.island.config.other.FileManager;
import me.zeroseven.island.database.IslandDAO;
import me.zeroseven.island.database.MinionsDAO;
import me.zeroseven.island.database.PlayersDAO;
import me.zeroseven.island.listeners.ConnectionListener;
import me.zeroseven.island.listeners.island.IslandGUIListener;
import me.zeroseven.island.listeners.island.IslandListeners;
import me.zeroseven.island.listeners.minion.MinionGUIListener;
import me.zeroseven.island.listeners.minion.MinionListeners;
import me.zeroseven.island.listeners.minion.MinionSpawnerListener;
import me.zeroseven.island.listeners.minion.UpgradeGUIListener;
import me.zeroseven.island.listeners.shop.MailGUIListener;
import me.zeroseven.island.listeners.shop.ShopListener;
import me.zeroseven.island.minions.Minion;
import me.zeroseven.island.minions.MinionType;
import me.zeroseven.island.minions.types.BlockMinion;
import me.zeroseven.island.minions.types.CropMinion;
import me.zeroseven.island.minions.types.MobMinion;
import me.zeroseven.island.shop.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public final class IslandPlugin extends JavaPlugin {

    public static final List<Minion> MINIONS = new ArrayList<>();
    public static final List<LivingEntity> antiDrop = new ArrayList<>();
    public static final Map<LivingEntity, MobMinion> antiDropMinion = new HashMap<>();
    private static IslandBuffer islandBuffer;
    private static Market market;

    private ProtocolManager protocolManager;
    public static int TOTAL;

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        islandBuffer = new IslandBuffer(this);
        market = new Market(this);

        setupDAO();
        registerEvents();
        registerCommands();
        registerSerializations();
        saveConfigs();
        market.load();


        TOTAL = getConfig().getInt("total");
        setupMinions();
        loadPlayers();
    }

    @Override
    public void onDisable() {
        saveMinions();
        saveConfig();
        savePlayers();
        market.save();
    }

    private void saveConfigs(){
        new MenuConfiguration(this).saveDefaultConfig();
        new IslandConfiguration(this).saveDefaultConfig();
        saveDefaultConfig();
    }


    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new MinionListeners(), this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MinionSpawnerListener(), this);
        getServer().getPluginManager().registerEvents(new IslandListeners(this), this);
        getServer().getPluginManager().registerEvents(new IslandGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new MailGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
    }

    private void registerCommands() {
        getCommand("minion").setExecutor(new MinionCommand());
        getCommand("island").setExecutor(new IslandCommand(this));
        getCommand("sell").setExecutor(new SellCommand());
        getCommand("mail").setExecutor(new ShopCommand(this));
    }

    private void registerSerializations() {
        ConfigurationSerialization.registerClass(BlockMinion.class, "BlockMinion");
        ConfigurationSerialization.registerClass(MobMinion.class, "MobMinion");
        ConfigurationSerialization.registerClass(CropMinion.class, "CropMinion");
    }

    private void setupDAO() {
        new IslandDAO(this).createTable();
        new MinionsDAO(this).createTable();
        new PlayersDAO(this).createTable();
    }

    private void savePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getIslandBuffer().savePlayerIsland(player);
        }
    }

    private void loadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getIslandBuffer().loadPlayerIsland(player);
        }
        for (@NotNull OfflinePlayer offPlayer : Bukkit.getOfflinePlayers()) {
            Player player = Bukkit.getPlayer(offPlayer.getUniqueId());
        }
    }

    private void saveMinions() {
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

    private void setupMinions() {
        for (MinionType type : MinionType.values()) {
            FileConfiguration fc = FileManager.getInstance().getMinionsConfig(type);
            for (String str : fc.getKeys(false)) {
                Minion m = (Minion) fc.get(str);
                MINIONS.add(m);
                if (m instanceof BlockMinion) {
                    ((BlockMinion) m).breakBlock();
                } else if (m instanceof CropMinion) {
                    ((CropMinion) m).breakCrop();
                } else {
                    ((MobMinion) m).spawnMobs();
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
                        handleBlockMinion(cal, bm);
                    } else if (m instanceof CropMinion) {
                        CropMinion cm = (CropMinion) m;
                        handleCropMinion(cal, cm);
                    } else if (m instanceof MobMinion) {
                        MobMinion mm = (MobMinion) m;
                        handleMobMinion(cal, mm);
                    }
                }
            }
        }.runTaskTimer(this, 40, 20);
    }

    private void handleBlockMinion(Calendar cal, BlockMinion bm) {
        if (bm.getBlockBreakDate() == 0L || bm.getBlockPlaceDate() == 0L) return;

        if (cal.getTimeInMillis() >= bm.getBlockBreakDate()) {
            bm.breakBlock();
        } else if (cal.getTimeInMillis() >= bm.getBlockPlaceDate()) {
            bm.placeBlock();
        }
    }

    private void handleCropMinion(Calendar cal, CropMinion cm) {
        if (cm.getBlockBreakDate() == 0L || cm.getBlockPlaceDate() == 0L) return;

        if (cal.getTimeInMillis() >= cm.getBlockBreakDate()) {
            cm.breakCrop();
        } else if (cal.getTimeInMillis() >= cm.getBlockPlaceDate()) {
            cm.placeCrop();
        }
    }

    private void handleMobMinion(Calendar cal, MobMinion mm) {
        if (mm.getMobKillDate() == 0L || mm.getMobSpawnDate() == 0L) return;

        if (cal.getTimeInMillis() >= mm.getMobSpawnDate()) {
            mm.spawnMobs();
        } else if (cal.getTimeInMillis() >= mm.getMobKillDate()) {
            mm.killMobs();
        }
    }

    public static Market getMarket() {
        return market;
    }

    public static IslandBuffer getIslandBuffer() {
        return islandBuffer;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
