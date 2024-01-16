package com.shevkomore.percs.percdata;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.shevkomore.percs.perc.Perc;

public class PercEntry implements ConfigurationSerializable{
	public String name;
	public Perc perc;
	public PercEntry(Perc perc) {
		this.name = perc.getClass().getName();
		this.perc = perc;
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("perc", perc);
		return map;
	}
}
