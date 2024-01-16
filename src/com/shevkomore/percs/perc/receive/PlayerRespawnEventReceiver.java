package com.shevkomore.percs.perc.receive;

import org.bukkit.event.player.PlayerRespawnEvent;

public interface PlayerRespawnEventReceiver {
	public void onPlayerRespawnEvent(PlayerRespawnEvent e);
}
