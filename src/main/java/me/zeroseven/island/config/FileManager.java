package me.zeroseven.island.config;

import java.io.File;
import java.io.IOException;

import me.zeroseven.island.minions.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileManager {
	
	private static FileManager instance = new FileManager();
	private Plugin p = Bukkit.getPluginManager().getPlugin("zIsland");
	
	public static FileManager getInstance() {
		return instance;
	}
	
	public FileConfiguration getMinionsConfig(MinionType type) {
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		File f = new File(p.getDataFolder() + File.separator + type.toString().toLowerCase() + ".yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return YamlConfiguration.loadConfiguration(f);
		
	}
	
	public File getMinionsFile(MinionType type) {
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		File f = new File(p.getDataFolder() + File.separator + type.toString().toLowerCase() + ".yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return f;
		
	}

}
