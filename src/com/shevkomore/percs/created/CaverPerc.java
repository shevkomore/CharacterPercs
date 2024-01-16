package com.shevkomore.percs.created;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.EntityPotionEffectEventReceiver;

public class CaverPerc extends Perc implements EntityPotionEffectEventReceiver {
	@Override
	public String getDisplayName() {	return "The Lost Miner";	}
	
	Aura BurnAura = new Aura(Particle.FLAME, 10);
	int Overheat = 0;
	static int OverheatCritical = 20;
	static int OverheatMax = 25;
	static int OverheatMin = -5;
	
	Runnable BeforeBurnRun = new Runnable() {
		@Override
        public void run() {
			Player player = getPlayer();
			if(player == null) return;
			if(player.getLocation().getBlock().getLightLevel() > 9) {
				if(Overheat<OverheatMax) ++Overheat;
				if(Overheat>0)player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				if(Overheat>OverheatCritical)
					player.setFireTicks(40);
			} else {
				if(Overheat>OverheatMin) --Overheat;
				if(Overheat<0) player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true));
			}
			//overheat counter
			sendText(""+String.valueOf(Overheat), 40);
		}
	};

	public CaverPerc(Plugin plugin, String PlayerName) {
		super(plugin, PlayerName);
		scheduler = Bukkit.getServer().getScheduler();
		setTask(new TerminatableRepeatingTask(BeforeBurnRun, 40));
	}
	
	public CaverPerc(Map<String, Object> data) {
		PlayerName = (String) data.get("PlayerName");
		Overheat = (int) data.get("Overheat");
	}

	@Override
	public Aura getAura() {
		if(Overheat>0)
			return BurnAura;
		return Aura.noAura;
	}

	@Override
	public void onEntityPotionEffectEvent(EntityPotionEffectEvent e) {
		if(e.getAction() != EntityPotionEffectEvent.Action.ADDED) return;
		e.setCancelled(e.getNewEffect().getType().equals(PotionEffectType.HUNGER) | e.getNewEffect().getType().equals(PotionEffectType.POISON));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("PlayerName", PlayerName);
		map.put("Overheat", Overheat);
		return map;
	}

	@Override
	public void start(Plugin plugin) {
		CurrentPlugin = plugin;
		scheduler = plugin.getServer().getScheduler();
		if(getPlayer() != null) {
			setTask(new TerminatableRepeatingTask(BeforeBurnRun, 40));
		}
	}

}
