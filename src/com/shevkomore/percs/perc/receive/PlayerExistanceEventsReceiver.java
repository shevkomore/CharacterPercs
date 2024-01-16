package com.shevkomore.percs.perc.receive;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
/**
 * <i>WARNING this interface is used by {@linkplain Perc#TerminatableTask}. 
 * Overriding it in a {@linkplain Perc} instance will render {@linkplain Perc#TerminatableTask TerminatableTasks} unusable.</i>
 * */
public interface PlayerExistanceEventsReceiver {
	/**
	 * <i>WARNING this method is used by {@linkplain Perc#TerminatableTask}. 
	 * Overriding it in a {@linkplain Perc} instance will render {@linkplain Perc#TerminatableTask TerminatableTasks} unusable.</i>
	 * 
	 * <p/>This method is activated when a {@linkplain Perc} instance exists in {@linkplain PercsData#PercMap PercMap}
	 * and player related to it disconnected.
	 * @param   e   - a {@linkplain PlayerQuitEvent} instance that occured.
	 * */
	public void onPlayerLeave(PlayerQuitEvent e);
	/**
	 * <i>WARNING this method is used by {@linkplain Perc#TerminatableTask}. 
	 * Overriding it in a {@linkplain Perc} instance will render {@linkplain Perc#TerminatableTask TerminatableTasks} unusable.</i>
	 * 
	 * <p/>This method is activated when a {@linkplain Perc} instance exists in {@linkplain PercsData#PercMap PercMap}
	 * and player related to it joined the server.
	 * @param   e   - a {@linkplain PlayerJoinEvent} instance that occured.
	 * */
	public void onPlayerJoin(PlayerJoinEvent e);
	/**
	 * <i>WARNING this method is used by {@linkplain Perc#TerminatableTask}. 
	 * Overriding it in a {@linkplain Perc} instance will render {@linkplain Perc#TerminatableTask TerminatableTasks} unusable.</i>
	 * 
	 * <p/>This method is activated when a {@linkplain Perc} instance exists in {@linkplain PercsData#PercMap PercMap}
	 * and player related to it died.
	 * @param   e   - a {@linkplain PlayerDeathEvent} instance that occured.
	 * */
	public void onPlayerDeath(PlayerDeathEvent e);
	/**
	 * <i>WARNING this method is used by {@linkplain Perc#TerminatableTask}. 
	 * Overriding it in a {@linkplain Perc} instance will render {@linkplain Perc#TerminatableTask TerminatableTasks} unusable.</i>
	 * 
	 * <p/>This method is activated when a {@linkplain Perc} instance exists in {@linkplain PercsData#PercMap PercMap}
	 * and player related to it respawned.
	 * @param   e   - a {@linkplain PlayerRespawnEvent} instance that occured.
	 * */
	public void onPlayerRespawn(PlayerRespawnEvent e);
}
