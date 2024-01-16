package com.shevkomore.percs.created;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;

public class ToolmasterPerc extends Perc {
	@Override
	public String getDisplayName() {	return "The Toolmaster";	}
	
	Plugin currentPlugin;
	Player player;
	
	Random random;
	
	ItemStack[] MaxTools = {
			new ItemStack(Material.NETHERITE_SWORD),
			new ItemStack(Material.NETHERITE_AXE),
			new ItemStack(Material.NETHERITE_PICKAXE),
			new ItemStack(Material.NETHERITE_SHOVEL),
			new ItemStack(Material.NETHERITE_HOE),
	};
	public ItemStack CurrentTool;
	boolean IsSilkTouch = false;
	
	Runnable ChangeToolRun = new Runnable() {
		@Override
        public void run() {
			player.getInventory().remove(CurrentTool);
			ChooseTool();
			player.getInventory().addItem(CurrentTool);
        }
    };
    BukkitScheduler scheduler;
	
	public ToolmasterPerc(Plugin p, Player pl) {
		super(p,pl);
		random = new Random();
		
		MaxTools[0].addEnchantment(Enchantment.DAMAGE_ALL, 5);
		MaxTools[0].addEnchantment(Enchantment.KNOCKBACK, 3);
		//MaxTools[0].addEnchantment(Enchantment.FIRE_ASPECT, 2); OR MaxTools[0].addEnchantment(Enchantment.SWEEPING_EDGE, 3);
		MaxTools[0].addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
		
		MaxTools[1].addEnchantment(Enchantment.DIG_SPEED, 5);
		MaxTools[1].addEnchantment(Enchantment.DAMAGE_ALL, 5);
		//MaxTools[1].addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5); OR MaxTools[1].addEnchantment(Enchantment.SILK_TOUCH, 1);
		
		MaxTools[2].addEnchantment(Enchantment.DIG_SPEED, 5);
		//MaxTools[1].addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5); OR MaxTools[1].addEnchantment(Enchantment.SILK_TOUCH, 1);
		MaxTools[3].addEnchantment(Enchantment.DIG_SPEED, 5);
		//MaxTools[1].addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5); OR MaxTools[1].addEnchantment(Enchantment.SILK_TOUCH, 1);
		MaxTools[4].addEnchantment(Enchantment.DIG_SPEED, 5);
		MaxTools[4].addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
		
		MaxTools[0].getItemMeta().setUnbreakable(true);
		MaxTools[1].getItemMeta().setUnbreakable(true);
		MaxTools[2].getItemMeta().setUnbreakable(true);
		MaxTools[3].getItemMeta().setUnbreakable(true);
		MaxTools[4].getItemMeta().setUnbreakable(true);
		
		ChooseTool();
		
		scheduler = Bukkit.getServer().getScheduler();
	    scheduler.scheduleSyncRepeatingTask(currentPlugin, ChangeToolRun, 5, 600);
	}
	
	void ChooseTool() {
		int id = random.nextInt(5);
		CurrentTool = MaxTools[id];
		if(id>0&&id<4) {
			if(random.nextInt(2)==0)
				MaxTools[1].addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
			else
				MaxTools[1].addEnchantment(Enchantment.SILK_TOUCH, 1);
		} else if(id==0) {
			if(random.nextInt(2)==0)
				MaxTools[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
			else
				MaxTools[0].addEnchantment(Enchantment.SWEEPING_EDGE, 3);
		}
	}

	@Override
	public Aura getAura() {
		return Aura.noAura;
	}

}
