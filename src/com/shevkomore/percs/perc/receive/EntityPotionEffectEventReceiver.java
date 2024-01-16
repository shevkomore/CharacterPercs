package com.shevkomore.percs.perc.receive;

import org.bukkit.event.entity.EntityPotionEffectEvent;

public interface EntityPotionEffectEventReceiver {
	public void onEntityPotionEffectEvent(EntityPotionEffectEvent e);
}
