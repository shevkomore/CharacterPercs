package com.shevkomore.percs.perc.receive;

import org.bukkit.event.entity.PlayerDeathEvent;

public interface PlayerDeathEventReceiver {
	public void onPlayerDeathEvent(PlayerDeathEvent e);
}
