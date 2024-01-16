package com.shevkomore.percs.perc.receive;

import org.bukkit.event.entity.EntityDamageEvent;

public interface EntityDamageEventReceiver {
	public void onEntityDamageEvent(EntityDamageEvent e);
}
