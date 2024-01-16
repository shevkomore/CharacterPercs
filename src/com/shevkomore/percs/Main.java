package com.shevkomore.percs;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.shevkomore.percs.commands.SplitChangeCommand;
import com.shevkomore.percs.created.BreakerPerc;
import com.shevkomore.percs.created.CaverPerc;
import com.shevkomore.percs.created.EyePerc;
import com.shevkomore.percs.created.PacifistPerc;
import com.shevkomore.percs.created.PlantPerc;
import com.shevkomore.percs.created.SmithPerc;
import com.shevkomore.percs.created.SplitPerc;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.*;
import com.shevkomore.percs.percdata.*;

public class Main extends JavaPlugin implements Listener {
	static {
		
	}
	Runnable OncePerHalfSecondEffects =  new Runnable(){
        @Override
        public void run() {
         for(String p: PercsData.getPlayersSet()) {
        	 Player player = Bukkit.getPlayerExact(p);
        	 if(player == null) continue;
        	 Perc perc = PercsData.getPerc(p);
        	 Particle particle = perc.getAura().particle;
        	 if(particle == null) continue;
        	 if(particle == Particle.REDSTONE) {
        		 player.getWorld().spawnParticle(
        				 perc.getAura().particle, player.getLocation(), perc.getAura().intensity, 0.5,1,0.5, perc.getAura().options);
        	 }else {
        		 player.getWorld().spawnParticle(perc.getAura().particle, player.getLocation(), perc.getAura().intensity, 0.5,1,0.5);
        	 }
         }
     }
        };
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		this.getCommand("perc").setExecutor(new PercCommand(this));
		this.getCommand("change").setExecutor(new SplitChangeCommand());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, OncePerHalfSecondEffects, 0, 10);
		//PercsData.PercNameList.put("Split",SplitPerc.class);
		//PercsData.PercNameList.put("Pacifist",PacifistPerc.class);
		ConfigurationSerialization.registerClass(CaverPerc.class, "CaverPerc");
		PercsData.PercNameList.put("Caver",CaverPerc.class);
		//PercsData.PercNameList.put("Plant",PlantPerc.class);
		ConfigurationSerialization.registerClass(BreakerPerc.class, "BreakerPerc");
		PercsData.PercNameList.put("Breaker",BreakerPerc.class);
		//PercsData.PercNameList.put("Eye",EyePerc.class);
		//PercsData.PercNameList.put("Smith",SmithPerc.class);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		Entity attacker = e.getDamager();
		Entity receiver = e.getEntity();
		
		if(attacker.getType() == EntityType.PLAYER) {
			Perc PlayerPerc = PercsData.getPerc(((Player)attacker).getName());
			if (PlayerPerc == null) return;
			if (PlayerPerc instanceof EntityDamageByEntityEventReceiver)
				((EntityDamageByEntityEventReceiver) PlayerPerc).onEntityDamageByEntityEvent(e, true);
		}
		
		if(receiver.getType() == EntityType.PLAYER) {
			Perc PlayerPerc = PercsData.getPerc(((Player)receiver).getName());
			if (PlayerPerc == null) return;
			if (PlayerPerc instanceof EntityDamageByEntityEventReceiver)
				((EntityDamageByEntityEventReceiver) PlayerPerc).onEntityDamageByEntityEvent(e, false);
		}
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		Entity receiver = e.getEntity();
		if(receiver.getType() != EntityType.PLAYER) return;
		Perc PlayerPerc = PercsData.getPerc(((Player)receiver).getName());
		if (PlayerPerc instanceof EntityDamageEventReceiver)
			((EntityDamageEventReceiver) PlayerPerc).onEntityDamageEvent(e);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if(PlayerPerc instanceof PlayerMoveEventReceiver)
			((PlayerMoveEventReceiver) PlayerPerc).onPlayerMoveEvent(e);
	}
	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) return;
		Perc PlayerPerc = PercsData.getPerc(((Player)e.getEntity()).getName());
		if (PlayerPerc == null) return;
		if (PlayerPerc instanceof EntityPotionEffectEventReceiver)
			((EntityPotionEffectEventReceiver) PlayerPerc).onEntityPotionEffectEvent(e);
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		if (PlayerPerc instanceof BlockBreakEventReceiver)
			((BlockBreakEventReceiver) PlayerPerc).onBlockBreakEvent(e);
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getEntity().getName());
		if (PlayerPerc == null) return;
		PlayerPerc.onPlayerDeath(e);
		if (PlayerPerc instanceof PlayerDeathEventReceiver)
			((PlayerDeathEventReceiver) PlayerPerc).onPlayerDeathEvent(e);
	}
	@EventHandler
	public void onToolUse(PlayerInteractEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		if (PlayerPerc instanceof PlayerInteractEventReceiver)
			((PlayerInteractEventReceiver) PlayerPerc).onPlayerInteractEvent(e);
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		PlayerPerc.onPlayerJoin(e);
		if (PlayerPerc instanceof PlayerJoinEventReceiver)
			((PlayerJoinEventReceiver) PlayerPerc).onPlayerJoinEvent(e);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		PlayerPerc.onPlayerLeave(e);
		if (PlayerPerc instanceof PlayerQuitEventReceiver)
			((PlayerQuitEventReceiver) PlayerPerc).onPlayerQuitEvent(e);
	}
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		if (PlayerPerc instanceof PlayerItemConsumeEventReceiver) {
			((PlayerItemConsumeEventReceiver) PlayerPerc).onPlayerItemConsumeEvent(e);
		}
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Perc PlayerPerc = PercsData.getPerc(e.getPlayer().getName());
		if (PlayerPerc == null) return;
		PlayerPerc.onPlayerRespawn(e);
		if (PlayerPerc instanceof PlayerRespawnEventReceiver)
			((PlayerRespawnEventReceiver) PlayerPerc).onPlayerRespawnEvent(e);
	}
}
