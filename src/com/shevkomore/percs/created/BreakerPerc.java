package com.shevkomore.percs.created;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.BlockBreakEventReceiver;
import com.shevkomore.percs.perc.receive.EntityDamageEventReceiver;

public class BreakerPerc extends Perc implements BlockBreakEventReceiver, EntityDamageEventReceiver{
	@Override
	public String getDisplayName() {	return "The Breaker";	}
	
	public BreakerPerc(Plugin plugin, String PlayerName) {
		super(plugin, PlayerName);
	}
	public BreakerPerc(Map<String, Object> data) {
		this.PlayerName = (String) data.get("PlayerName");
	}
	
	public static float power = 5;
	@Override
	public Aura getAura() {
		return Aura.noAura;
	}
	@Override
	public void onBlockBreakEvent(BlockBreakEvent e) {
		e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(), BreakerPerc.power);
	}
	@Override
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.BLOCK_EXPLOSION)
			e.setCancelled(true);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("PlayerName", PlayerName);
		return map;
	}

	@Override
	public void start(Plugin plugin) {
		this.CurrentPlugin = plugin;
		
	}

}
