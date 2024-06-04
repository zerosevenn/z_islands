package me.zeroseven.island.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public abstract class AbstractConfiguration {

    private File configFile;
    private FileConfiguration configuration;
    private Reader stream;
    private YamlConfiguration defaultConfig;

    public AbstractConfiguration(JavaPlugin instance, String file){
        this.configFile = new File(instance.getDataFolder(), file + ".yml");
        if (!configFile.exists()) {
            try {
                instance.saveResource(file + ".yml", false);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        this.defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(file +".yml"), StandardCharsets.UTF_8));
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        configuration.setDefaults(defaultConfig);
        saveDefaultConfig();
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        }
        configuration.options().copyDefaults(true);
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public String getMessage(String path, Player player){
        return configuration.getString(path)
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{tag}", getConfiguration()
                        .getString("Config.tag").replace("&", "§"));
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public File getConfigFile() {
        return configFile;
    }

    public YamlConfiguration getDefaultConfig() {
        return defaultConfig;
    }
}