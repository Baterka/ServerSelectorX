package xyz.derkades.serverselectorx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages configuration files
 */
public class ConfigurationManager {

	private final Map<String, FileConfiguration> files = new ConcurrentHashMap<>();
	private FileConfiguration serverConfig;
	
	public Collection<FileConfiguration> getAll() {
		return files.values();
	}
	
	public FileConfiguration getByName(String name) {
		return files.get(name);
	}
	
	public FileConfiguration getConfig() {
		return Main.getPlugin().getConfig();
	}
	
	private void loadServerConfig() {
		File file = new File(Main.getPlugin().getDataFolder(), "server.yml");
		serverConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration getServerConfig() {
		if (serverConfig == null) loadServerConfig();
		
		return serverConfig;
	}
	
	public void reloadAll() {
		System.out.println("[debug] " + Main.getPlugin().getDataFolder().getAbsolutePath());
		
		//Create default configuration files
		
		Main.getPlugin().saveDefaultConfig();
		
		File dir = new File(Main.getPlugin().getDataFolder() + File.separator + "menu");
		dir.mkdirs();
		if (dir.listFiles().length == 0){
			URL inputUrl = getClass().getResource("/xyz/derkades/serverselectorx/default-selector.yml");
			try {
				File defaultConfig = new File(Main.getPlugin() + File.separator + "menu", "default.yml");
				FileUtils.copyURLToFile(inputUrl, defaultConfig);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		
		//Reload configuration files
		
		Main.getPlugin().reloadConfig();
		
		loadServerConfig();

		files.clear();
		for (File file : new File(Main.getPlugin().getDataFolder() + File.separator + "menu").listFiles()){
			if (!file.getName().endsWith(".yml"))
				continue;

			String name = file.getName().replace(".yml", "");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			files.put(name, config);
		}
		
		
		
		//Initialize variables
		ItemMoveDropCancelListener.DROP_PERMISSION_ENABLED = getConfig().getBoolean("cancel-item-drop", false);
		ItemMoveDropCancelListener.MOVE_PERMISSION_ENABLED = getConfig().getBoolean("cancel-item-move", false);
		

	}

}