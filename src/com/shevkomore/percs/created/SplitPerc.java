package com.shevkomore.percs.created;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import com.shevkomore.percs.perc.Aura;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.perc.receive.PlayerJoinEventReceiver;
import com.shevkomore.percs.perc.receive.PlayerMoveEventReceiver;
import com.shevkomore.percs.perc.receive.PlayerQuitEventReceiver;

public class SplitPerc extends Perc implements PlayerMoveEventReceiver, PlayerJoinEventReceiver, PlayerQuitEventReceiver{
	@Override
	public String getDisplayName() {	return "Split/Flip";	}
	
	static Aura ChangeAura = new Aura(Particle.REDSTONE, 100, new Particle.DustOptions(Color.BLACK, 10)); 

	class PlayerData{
		ItemStack[] inventory;
		double health;
		int food;
		double exhaustion;
		int air;
		double exp;
		int fire;
		Collection<PotionEffect> effects;
		public PlayerData(Player p) {
			this.GetFromPlayer(p);
		}
		public void GetFromPlayer(Player p) {
			inventory = p.getInventory().getContents().clone();
			health = p.getHealth();
			food = p.getFoodLevel();
			exhaustion = p.getExhaustion();
			air = p.getRemainingAir();
			exp = p.getExp();
			fire = p.getFireTicks();
			effects = p.getActivePotionEffects();
		}
		public void SetToPlayer(Player p) {
			p.getInventory().setContents(inventory);
			p.setHealth(health);
			p.setFoodLevel(food);
			p.setExhaustion((float) exhaustion);
			p.setRemainingAir(air);
			p.setExp((float) exp);
			p.setFireTicks(fire);
			for (PotionEffect effect : p.getActivePotionEffects())
		        p.removePotionEffect(effect.getType());
			p.addPotionEffects(effects);
		}
		//Serialization
		public Map<String,Object> serialize(){
			HashMap<String,Object> mapSerializer = new HashMap<>();
			ArrayList<ItemStack> tempIS = new ArrayList<ItemStack>();
			for(ItemStack item: this.inventory)
				tempIS.add(item);
			mapSerializer.put("inventory", tempIS);
			mapSerializer.put("health", this.health);
			mapSerializer.put("food", this.food);
			mapSerializer.put("exhaustion", this.exhaustion);
			mapSerializer.put("air", this.air);
			mapSerializer.put("exp", this.exp);
			mapSerializer.put("fire", this.fire);
			mapSerializer.put("effects", this.effects);
			return mapSerializer;
		}
		@SuppressWarnings("unchecked")
		public PlayerData(MemorySection serializedPlayerData) {
			ArrayList<ItemStack> tl = ((ArrayList<ItemStack>) serializedPlayerData.get("inventory"));
			this.inventory = tl.toArray(new ItemStack[0]);
			this.health = (double) serializedPlayerData.get("health");
			this.food = (int) serializedPlayerData.get("food");
			this.exhaustion = (double) serializedPlayerData.get("exhaustion");
			this.air = (int) serializedPlayerData.get("air");
			this.exp = (double) serializedPlayerData.get("exp");
			this.fire = (int) serializedPlayerData.get("fire");
			this.effects = (Collection<PotionEffect>) serializedPlayerData.get("effects");
		}
	}
	PlayerData FirstPlayer;
	PlayerData SecondPlayer;
	public boolean IsSecond;
	public boolean IsStable;
	long delay;
	static long MinimumDelay = 4800;
	static long RandomDelayMultiplier = 1200;
	
	File file;
	
	Runnable BeforeChangeRun;
	Runnable ChangeMarkerRun;
	int BeforeChangeTask;
	int ChangeMarkerTask;
	BukkitScheduler scheduler;
	
	Player player;
	
	public boolean IsChanging;
	
	static Particle.DustOptions MarkerParticle1 = new Particle.DustOptions(Color.WHITE, 1);;
	static Particle.DustOptions MarkerParticle2 = new Particle.DustOptions(Color.BLACK, 1);;
	
	public SplitPerc(Plugin p, Player player) {
		super(p,player);
		player.getInventory().clear();
		FirstPlayer = new PlayerData(player);
		SecondPlayer = new PlayerData(player);
		file = new File(p.getDataFolder(), "SplitPercData.yml");
		if(file.exists())
			LoadFromFile();
		else SaveToFile();
		IsSecond = true;
		BeforeChangeRun = new Runnable() {
			@Override
	        public void run() {
				if(!IsStable) {
					IsChanging = true;
					player.setGameMode(GameMode.SPECTATOR);
				}
				if(IsSecond) SecondPlayer.GetFromPlayer(player);
				else FirstPlayer.GetFromPlayer(player);
				player.sendMessage("Saving...");
				SaveToFile();
	        }
	    };
	    ChangeMarkerRun = new Runnable() {
			@Override
	        public void run() {
				player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0,2,0), 1, 0.05,0.05,0.05, IsSecond?MarkerParticle2:MarkerParticle1);
	        }
	    };
	    scheduler = Bukkit.getServer().getScheduler();
	    ChangeMarkerTask = scheduler.scheduleSyncRepeatingTask(p, ChangeMarkerRun, 5, 5);
	    change();
	}
	public void change() {
		if(IsSecond) FirstPlayer.SetToPlayer(player);
		else SecondPlayer.SetToPlayer(player);
		
		IsSecond = !IsSecond;
		
		delay = (long) (MinimumDelay + (Math.random() * RandomDelayMultiplier));
		IsChanging = false;
		player.setGameMode(GameMode.SURVIVAL);
		if(!IsStable)
			BeforeChangeTask = scheduler.scheduleSyncDelayedTask(CurrentPlugin, BeforeChangeRun, delay);
	}
	
	public void LoadFromFile() {
		FileConfiguration SplitData = YamlConfiguration.loadConfiguration(file);
		FirstPlayer  = new PlayerData((MemorySection) SplitData.get(PlayerName+".FirstPlayer"));
		SecondPlayer = new PlayerData((MemorySection) SplitData.get(PlayerName+".SecondPlayer"));
	}
	public void SaveToFile() {
		FileConfiguration SplitData = YamlConfiguration.loadConfiguration(file);
		SplitData.createSection(PlayerName+".FirstPlayer", FirstPlayer.serialize());
		SplitData.createSection(PlayerName+".SecondPlayer", SecondPlayer.serialize());
		try {
			SplitData.save(file);
		} catch (IOException e) {
			Player player = getPlayer();
			CurrentPlugin.getLogger().log(Level.WARNING, "Player "+PlayerName+" data save failed");
			if(player != null)player.sendMessage("Player data save failed");
		}
	}
	
	
	public Aura getAura() {
		if(IsChanging) return ChangeAura;
		return Aura.noAura;
		
	}
	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if(IsChanging) {
			e.setTo(e.getFrom());
		}
	}
	@Override
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		
	}
	@Override
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		// TODO Auto-generated method stub
		
	}
}
