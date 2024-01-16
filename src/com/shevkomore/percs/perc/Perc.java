package com.shevkomore.percs.perc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.shevkomore.percs.perc.receive.PlayerExistanceEventsReceiver;

import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutChat;

/**
 * abstract class for all {@linkplain Perc Percs} used in this plugin.
 * */
public abstract class Perc implements PlayerExistanceEventsReceiver, ConfigurationSerializable{
	/**
	 * returns a {@code String} that will be used as {@linkplain Perc}'s displayed name.
	 * */
	public abstract String getDisplayName();
	
	protected Plugin CurrentPlugin;
	protected BukkitScheduler scheduler;
	protected String PlayerName;
	/**
	 * {@linkplain TerminatableRepeatingTask TerminatableRepeatingTasks} have to be added to this list in order to {@linkplain PlayerExistanceEventsReceiver react} to whether a player is online and/or alive.
	 * */
	protected LinkedList<TerminatableRepeatingTask> TerminatableRepeatingTasks;
	/**
	 * {@linkplain TimedTask TImedTasks} have to be added to this list in order to {@linkplain PlayerExistanceEventsReceiver react} to whether a player is online and/or alive.
	 * */
	protected LinkedList<TimedTask> TimedTasks;
	/**
	 * {@linkplain TimedRepeatingTask TimedRepeatingTasks} have to be added to this list in order to {@linkplain PlayerExistanceEventsReceiver react} to whether a player is online and/or alive.
	 * */
	protected LinkedList<TimedRepeatingTask> TimedRepeatingTasks;
	protected ArrayList<Runnable> Runnables;
	protected void setTask(TerminatableRepeatingTask t) {
		if(TerminatableRepeatingTasks == null) TerminatableRepeatingTasks = new LinkedList<TerminatableRepeatingTask>();
		TerminatableRepeatingTasks.add(t);
	}
	protected void setTask(TimedTask t) {
		if(TimedTasks == null) TimedTasks = new LinkedList<TimedTask>();
		TimedTasks.add(t);
	}
	protected void setTask(TimedRepeatingTask t) {
		if(TimedRepeatingTasks == null) TimedRepeatingTasks = new LinkedList<TimedRepeatingTask>();
		TimedRepeatingTasks.add(t);
	}
	protected Player getPlayer() {
		return Bukkit.getPlayerExact(PlayerName);
	}
	protected void sendText(String text, int time) {
		if(getPlayer() == null) return;
		IChatBaseComponent cbc = new ComponentBuilder();
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,ChatMessageType.GAME_INFO, getPlayer().getUniqueId());
        ((CraftPlayer) getPlayer()).getHandle().connection.send(ppoc);
        getPlayer().spigot().sendMessage(ChatMessageType.GAME_INFO, null, ppoc);
	}
	
	public Perc(Plugin plugin, String PlayerName) {
		this.CurrentPlugin = plugin;
		this.PlayerName = PlayerName;
		this.scheduler = Bukkit.getServer().getScheduler();
	}
	/**
	 * An empty constructor that does nothing, so that you can create your constructors.
	 * */
	protected Perc() {}
	/**
	 * This method will be called immediately after deserializing the {@linkplain Perc} instance. Use it to activate your tasks.
	 * */
	public abstract void start(Plugin plugin);
	/**
	 * disables and deletes all tasks.
	 * */
	public void stop() {
		if(TerminatableRepeatingTasks != null)
			for(TerminatableRepeatingTask t: TerminatableRepeatingTasks)
				t.stop();
		TerminatableRepeatingTasks.clear();
		if(TimedTasks != null)
			for(TimedTask t: TimedTasks)
				t.stop();
		TimedTasks.clear();
		if(TimedRepeatingTasks != null)
			for(TimedRepeatingTask t: TimedRepeatingTasks)
				t.stop();
		TimedRepeatingTasks.clear();
	}
	/**
	 * returns an {@linkplain Aura} that will appear every 10 ticks around a player with this {@linkplain Perc} implementation instance.
	 * <p/><i>(use {@linkplain Aura#noAura} when no particles should appear)</i>
	 * 
	 * @return {@linkplain Aura} instance currently used on player. 
	 * */
	public abstract Aura getAura();
	//public ??? getInterface();
	/**
	 * this class manages {@linkplain Runnable Runnables} that only need to work when a player is online and alive. 
	 * To do this, {@linkplain Perc} class has implemented {@linkplain PlayerExistanceEventsReceiver} methods.
	 * <p/>{@linkplain TerminatableRepeatingTask} tries to run the moment it's created.
	 * <p/>It is not saved to file. Each one of them has to be created with {@linkplain Perc#setPlugin}.
	 * */
	protected class TerminatableRepeatingTask{
		int taskID;
		long delay;
		boolean IsRunning;
		Runnable runnable;
		public TerminatableRepeatingTask(Runnable runnable, long delay) {
			this.delay = delay;
			this.runnable = runnable;
			this.IsRunning = getPlayer() != null;
			if(this.IsRunning)
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, runnable, delay, delay);
		}
		public void stop() {
			if(this.IsRunning) {
				scheduler.cancelTask(taskID);
				this.IsRunning = false;
			}
		}
		public void run() {
			if(!this.IsRunning) {
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, runnable, delay, delay);
				this.IsRunning = true;
			}
		}
	}
	/**
	 * this class manages {@linkplain Runnable Runnables} that work like a timer, and only need to work when a player is online and alive. 
	 * To do this, {@linkplain Perc} class has implemented {@linkplain PlayerExistanceEventsReceiver} methods 
	 * and stores time as amount of given time intervals left.
	 * <p/>{@linkplain TimedTask} tries to run upon creation (can be changed with IsPaused).
	 * It can be {@linkplain TimedTask#stop stopped}, {@linkplain TimedTask#pause paused} or {@linkplain TimedTask#resume resumed}. 
	 * It will call {@linkplain LinkedList#remove(Object o) TimedTasks.remove}({@linkplain TimedTask this}) when finished or stopped.
	 * <p/>{@linkplain Runnable Runnables} in them are not saved to file. 
	 * Use {@linkplain TimedTask#RunnableID RunnableID} to define which {@linkplain Runnable} is used by which task yourself.
	 * */
	protected class TimedTask implements ConfigurationSerializable{
		public int RunnableID;
		int taskID;
		long delay;
		long interval;
		long time;
		boolean IsPaused,IsOn;
		public Runnable runnable;
		Runnable TimerRunnable = new Runnable() {
			@Override
			public void run() {
				time--;
				if(time<=0) {
					runnable.run();
					stop();
				}
			}
		};
		public TimedTask(Runnable runnable, long delay, long interval, int RunnableID, boolean IsPaused) {
			this.delay = delay/interval;
			this.interval = interval;
			this.time = this.delay;
			this.IsPaused = IsPaused;
			this.IsOn = true;
			if(!IsPaused)
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, TimerRunnable, interval, interval);
		}
		public TimedTask(Runnable runnable, long delay, long interval, int RunnableID) {
			this(runnable, delay, interval, RunnableID, false);
		}
		public TimedTask(MemorySection SerializedData) {
			this.RunnableID = (int) SerializedData.get("RunnableID");
			this.delay = (long) SerializedData.get("delay");
			this.interval = (long) SerializedData.get("interval");
			this.time = (long) SerializedData.get("time");
			this.IsPaused = (boolean) SerializedData.get("IsPaused");
			this.IsOn = false;
		}
		public void stop() {
			TimedTasks.remove(this);
			scheduler.cancelTask(taskID);
		}
		public void pause() {
			if(!IsPaused) {
				scheduler.cancelTask(taskID);
				IsPaused = true;
			}
		}
		public void resume() {
			if(runnable != null && IsPaused || !IsOn) {
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, TimerRunnable, interval, interval);
				IsPaused = false; IsOn = true;
			}
		}
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("RunnableID", RunnableID);
			map.put("delay", delay);
			map.put("interval", interval);
			map.put("time", time);
			map.put("IsPaused", IsPaused);
			return map;
		}
	}
	/**
	 * this class manages {@linkplain Runnable Runnables} that work like a timer, and only need to work when a player is online and alive. 
	 * To do this, {@linkplain Perc} class has implemented {@linkplain PlayerExistanceEventsReceiver} methods 
	 * and stores time as amount of given time intervals left untill next loop.
	 * <p/>{@linkplain TimedRepeatingTask} tries to run upon creation (can be changed with IsPaused).
	 * It can be {@linkplain TimedRepeatingTask#start started}, {@linkplain TimedRepeatingTask#stop stopped}, 
	 * {@linkplain TimedRepeatingTask#pause paused} or {@linkplain TimedRepeatingTask#resume resumed}. 
	 * <p/>It is {@linkplain TimedRepeatingTask#stop stopped} when player leaves the server, 
	 * {@linkplain TimedRepeatingTask#start started} when player joins a server, 
	 * {@linkplain TimedRepeatingTask#pause paused} when player dies, 
	 * and {@linkplain TimedRepeatingTask#resume resumed} when player respawns.
	 * */
	protected class TimedRepeatingTask implements ConfigurationSerializable{
		public int RunnableID;
		int taskID;
		long delay;
		long interval;
		long time;
		boolean IsPaused, IsOn;
		public Runnable runnable;
		Runnable TimerRunnable = new Runnable() {
			@Override
			public void run() {
				time--;
				if(time<=0) {
					runnable.run();
					time = delay;
				}
			}
		};
		public TimedRepeatingTask(Runnable runnable, long delay, long interval, boolean IsPaused) {
			this.delay = delay/interval;
			this.interval = interval;
			this.time = this.delay;
			this.IsPaused = IsPaused;
			this.IsOn = true;
			if(!IsPaused)
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, TimerRunnable, interval, interval);
		}
		public TimedRepeatingTask(Runnable runnable, long delay, long interval) {
			this(runnable, delay, interval, false);
		}
		public TimedRepeatingTask(MemorySection SerializedData) {
			this.RunnableID = (int) SerializedData.get("RunnableID");
			this.delay = (long) SerializedData.get("delay");
			this.interval = (long) SerializedData.get("interval");
			this.time = (long) SerializedData.get("time");
			this.IsPaused = (boolean) SerializedData.get("IsPaused");
			this.IsOn = false;
		}
		public void stop() {
			if(this.time > 0)
				scheduler.cancelTask(taskID);
			this.time = 0;
		}
		public void start() {
			if(this.time <= 0 || !IsOn) {
				this.time = this.delay;
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, TimerRunnable, interval, interval);
				this.IsOn = true;
			}
		}
		public void pause() {
			if(!IsPaused && time > 0)
				scheduler.cancelTask(taskID);
			IsPaused = true;
		}
		public void resume() {
			if(IsPaused && time > 0) 
				taskID = scheduler.scheduleSyncRepeatingTask(CurrentPlugin, TimerRunnable, interval, interval);
			IsPaused = false;
		}
		
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("RunnableID", RunnableID);
			map.put("delay", delay);
			map.put("interval", interval);
			map.put("time", time);
			map.put("IsPaused", IsPaused);
			return map;
		}
	}
	public void onPlayerLeave(PlayerQuitEvent e) {
		if(TerminatableRepeatingTasks != null)
			for(TerminatableRepeatingTask t: TerminatableRepeatingTasks)
				t.stop();
		if(TimedTasks != null)
			for(TimedTask t: TimedTasks)
				t.pause();
		if(TimedRepeatingTasks != null)
			for(TimedRepeatingTask t: TimedRepeatingTasks)
				t.stop();
	}
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(TerminatableRepeatingTasks != null)
			for(TerminatableRepeatingTask t: TerminatableRepeatingTasks)
				t.run();
		if(TimedTasks != null)
			for(TimedTask t: TimedTasks)
				t.resume();
		if(TimedRepeatingTasks != null)
			for(TimedRepeatingTask t: TimedRepeatingTasks)
				t.start();
	}
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(TerminatableRepeatingTasks != null)
			for(TerminatableRepeatingTask t: TerminatableRepeatingTasks)
				t.stop();
		if(TimedTasks != null)
			for(TimedTask t: TimedTasks)
				t.pause();
		if(TimedRepeatingTasks != null)
			for(TimedRepeatingTask t: TimedRepeatingTasks)
				t.pause();
	}
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(TerminatableRepeatingTasks != null)
			for(TerminatableRepeatingTask t: TerminatableRepeatingTasks)
				t.run();
		if(TimedTasks != null)
			for(TimedTask t: TimedTasks)
				t.resume();
		if(TimedRepeatingTasks != null)
			for(TimedRepeatingTask t: TimedRepeatingTasks)
				t.resume();
	}
}
