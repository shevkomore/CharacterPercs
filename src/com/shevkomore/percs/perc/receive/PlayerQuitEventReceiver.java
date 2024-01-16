package com.shevkomore.percs.perc.receive;

import org.bukkit.event.player.PlayerQuitEvent;

public interface PlayerQuitEventReceiver {
	public void onPlayerQuitEvent(PlayerQuitEvent e);
}
