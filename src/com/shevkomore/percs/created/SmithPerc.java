package com.shevkomore.percs.created;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.PlayerDeathEventReceiver;

public class SmithPerc extends Perc implements PlayerDeathEventReceiver{
	@Override
	public String getDisplayName() {	return "The Smith";	}
	
	boolean HasArmor;
	static Aura SmithAura = new Aura(Particle.ENCHANTMENT_TABLE, 2);
	
	Runnable SmithMainRun = new Runnable() {
		@Override
		public void run() {
			Player player = getPlayer();
			if(player == null) return;
			ItemStack[] armor = player.getInventory().getArmorContents();
			HasArmor = false;
			for(ItemStack item:armor) {
				if(item != null) {
					item.addEnchantment(Enchantment.VANISHING_CURSE, 1);
					HasArmor = true;
				}
			}
			if(!HasArmor) {
				player.setExp(0);
				player.setLevel(100);
				player.getWorld().spawn(player.getLocation().add(0, 1, 0), ExperienceOrb.class).setExperience(20);
			} else {
				player.setExp(0);
				player.setLevel(0);
			}
		}
	};
	BukkitScheduler scheduler;
	
	public SmithPerc(Plugin pl, Player p) {
		super(pl, p);
		HasArmor = false;
		scheduler = Bukkit.getServer().getScheduler();
	    scheduler.scheduleSyncRepeatingTask(pl, SmithMainRun, 5, 10);
	}
	@Override
	public Aura getAura() {
		if(!HasArmor) return SmithAura;
		return Aura.noAura;
	}
	@Override
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		e.setKeepLevel(true);
	}

}
