package com.shevkomore.percs.created;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.EntityDamageByEntityEventReceiver;

public class EyePerc extends Perc implements EntityDamageByEntityEventReceiver{
	@Override
	public String getDisplayName() {	return "The Eye";	}
	
	static Aura EyeAura = new Aura(Particle.CRIT, 3);
	
	LivingEntity SeenEntity;
	
	Location SeenEntityLocation;
	
	Runnable EyeMainRun = new Runnable() {
		@Override
		public void run() {
			if(SeenEntity != null) {
				SeenEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5, 1));
				if(SeenEntityLocation == null) {
					SeenEntityLocation = SeenEntity.getLocation();
				} else SeenEntity.teleport(SeenEntityLocation);
			} else SeenEntityLocation = null;
			LivingEntity NewEntity = getViewedEntity();
			if(!NewEntity.equals(SeenEntity))
				SeenEntityLocation = NewEntity.getLocation();
			SeenEntity = NewEntity;
		}
	};
	BukkitScheduler scheduler;
	
	public EyePerc(Plugin pl, Player p) {
		super(pl, p);
		scheduler = Bukkit.getServer().getScheduler();
	    scheduler.scheduleSyncRepeatingTask(pl, EyeMainRun, 5, 1);
	}
	@Override
	public Aura getAura() {
		if(SeenEntity != null) return EyeAura;
		return Aura.noAura;
	}
	
	
	//Code from Androm, who got it from someone else
	private LivingEntity getViewedEntity(){
		Player player = getPlayer();
        if (player == null)
            return null;
        Collection<Entity> entities = player.getWorld().getNearbyEntities(player.getLocation(), 32, 32, 32);
        LivingEntity target = null;
        final double threshold = 1;
        for (final Entity other : entities) {
        	if(!(other instanceof LivingEntity)) continue;
            final Vector n = other.getLocation().toVector()
                    .subtract(player.getLocation().toVector());
            if (player.getLocation().getDirection().normalize().crossProduct(n)
                    .lengthSquared() < threshold
                    && n.normalize().dot(
                    		player.getLocation().getDirection().normalize()) >= 0) {
                if (target == null
                        || target.getLocation().distanceSquared(
                        		player.getLocation()) > other.getLocation()
                                .distanceSquared(player.getLocation()))
                    target = (LivingEntity) other;
            }
        }
        return target;
    }
	@Override
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e, boolean IsAttacker) {
		if(IsAttacker)
			e.setCancelled(e.getCause() == DamageCause.ENTITY_ATTACK);
	}
}
