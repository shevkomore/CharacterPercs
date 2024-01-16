package com.shevkomore.percs.created;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.PlayerItemConsumeEventReceiver;

public class PlantPerc extends Perc implements PlayerItemConsumeEventReceiver{
	@Override
	public String getDisplayName() {	return "Human Plant";	}
	
	Aura SunAura = new Aura(Particle.GLOW, 1);
	boolean IsOnLight;
	
	Runnable LightTestRun = new Runnable() {
		@Override
        public void run() {
			Player player = getPlayer();
			if(player == null) return;
			IsOnLight = false;
			if(player.getLocation().getBlock().getLightLevel() > 11) {
				IsOnLight = true; 
				player.setFoodLevel(player.getFoodLevel()+1);
			} 
		}
	};
	BukkitScheduler scheduler;

	public PlantPerc(Plugin plugin, Player p) {
		super(plugin, p);
		scheduler = Bukkit.getServer().getScheduler();
		IsOnLight = false;
		scheduler.scheduleSyncRepeatingTask(plugin, LightTestRun, 100, 100);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true));
	}

	@Override
	public Aura getAura() {
		if(IsOnLight)
			return SunAura;
		return Aura.noAura;
	}

	@Override
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent e) {
		e.setCancelled(e.getItem().getType().isEdible());
	}

}
