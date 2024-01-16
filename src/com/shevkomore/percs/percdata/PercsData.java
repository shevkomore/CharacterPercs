package com.shevkomore.percs.percdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.shevkomore.percs.perc.Perc;

public class PercsData {
	private PercsData(){}
	
	public static Map<String, Perc> PlayerPercMap = new HashMap<String, Perc>();
	public static Map<String, Class<? extends Perc>> PercNameList = new HashMap<String, Class<? extends Perc>>();
	
	public static Perc generatePerk(String name, Plugin plugin, String PlayerName) {
		Perc perc = null;
		Class<? extends Perc> PercClass = PercNameList.get(name);
		if(PercClass != null) {
			try {
				perc = PercClass.getConstructor(Plugin.class, String.class).newInstance(plugin, PlayerName);
			} catch (NoSuchMethodException e) {
				System.out.println("Every Perc must have a (Plugin, String) constructor.");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(perc != null)
				setPerc(plugin, PlayerName, perc);
		}
		return perc;
	}
	public static Set<String> getPlayersSet(){
		return PercNameList.keySet();
	}
	public static Perc getPerc(String PlayerName) {
		if(PlayerPercMap.containsKey(PlayerName))
			return PlayerPercMap.get(PlayerName);
		return null;
	}
	public static void setPerc(Plugin plugin, String PlayerName, Perc perc) {
		PlayerPercMap.put(PlayerName, perc);
		SavePlayerPercToFile(plugin, PlayerName);
	}
	public static void deletePerc(Plugin plugin, String PlayerName) {
		PlayerPercMap.remove(PlayerName);
		DeleteFromFile(plugin, PlayerName);
	}
	public static List<String> getPlayerPercList(){
		List<String> list = new ArrayList<String>();
		for(String name:PlayerPercMap.keySet())
			list.add(name + " - " + PlayerPercMap.get(name).getDisplayName());
		return list;
	}
	static void SavePlayerPercToFile(Plugin plugin, String PlayerName) {
		File file = new File(plugin.getDataFolder(), "PercData.yml");
		FileConfiguration PercMapData = YamlConfiguration.loadConfiguration(file);
		Map<String, Object> serializedPerc = PlayerPercMap.get(PlayerName).serialize();
		serializedPerc.put("==", PlayerPercMap.get(PlayerName).getClass().getName());
		PercMapData.createSection(PlayerName, serializedPerc);
		try {
			PercMapData.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Saving failed for "+PlayerName+"'s perc entry");
		}
	}
	
	static void SavePlayerMapToFile(Plugin plugin) {
		for(String name: PlayerPercMap.keySet())
			SavePlayerPercToFile(plugin, name);
	}
	
	public static void LoadPlayerMapFromFile(Plugin plugin) {
		File file = new File(plugin.getDataFolder(), "PercData.yml");
		if(file.exists()) {
			FileConfiguration PercMapData = YamlConfiguration.loadConfiguration(file);
			Map<String, Object> map = PercMapData.getValues(false);
			for(String name:map.keySet()){
				if(PlayerPercMap.containsKey(name)) continue;
				Perc perc = (Perc) PercMapData.get(name);
				perc.start(plugin);
				PlayerPercMap.put(name, perc);
			}
		}
	}
	static void DeleteFromFile(Plugin plugin, String PlayerName) {
		File file = new File(plugin.getDataFolder(), "PercData.yml");
		if(file.exists()) {
			YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
			data.set(PlayerName, null);
			try {
				data.save(file);
			} catch (IOException e) {
				plugin.getLogger().log(Level.WARNING, "Perc deletion save failed");
			}
		}
	}
}
