package com.shevkomore.percs.perc.receive;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageByEntityEventReceiver {
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e, boolean IsAttacker);
}
