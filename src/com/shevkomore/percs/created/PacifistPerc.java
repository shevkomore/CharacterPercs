package com.shevkomore.percs.created;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.EntityDamageByEntityEventReceiver;

public class PacifistPerc extends Perc implements EntityDamageByEntityEventReceiver{
	@Override
	public String getDisplayName() {	return "Pacifist";	}
	
	static Aura AttackedAura = new Aura(Particle.REDSTONE, 8, new Particle.DustOptions(Color.RED, 1));
	boolean Attacked = false;
	long delay = 1800;
	Runnable AttackedRun;
	public PacifistPerc(Plugin p, Player pl) {
		super(p,pl);
		AttackedRun = new Runnable() {
			@Override
	        public void run() {
	            setAttacked(false);
	        }
	    };
	}
	void setAttacked(boolean v) {
		Attacked = v;
	}
	public void Attacked() {
		Attacked = true;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(CurrentPlugin, AttackedRun, delay);
	}
	public Aura getAura() {
		if(Attacked)
			return AttackedAura;
		return Aura.noAura;
	}
	@Override
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e, boolean IsAttacker) {
		if(IsAttacker)
			Attacked();
		else if(!Attacked) {
			e.setCancelled(true);
		} else {
			e.setDamage(e.getDamage()*4);
		}
	}
}
